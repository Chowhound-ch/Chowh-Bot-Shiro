package per.chowh.bot.core.registery;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import per.chowh.bot.core.bot.domain.ChowhBot;
import per.chowh.bot.core.registery.config.EventListenerConfig;
import per.chowh.bot.core.registery.domain.EventMethod;
import per.chowh.bot.core.registery.domain.EventParam;
import per.chowh.bot.core.registery.interceptor.EventHandlerInterceptorComposite;
import per.chowh.bot.core.registery.support.ListenerArgumentResolverComposite;
import per.chowh.bot.core.utils.EventWrapper;
import per.chowh.bot.core.utils.ListenerUtils;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author : Chowhound
 * @since : 2025/9/1 - 9:59
 */
@Slf4j
@Component
public class ListenerRunner {
    @Autowired
    private EventRegister eventRegister;
    @Autowired
    private EventListenerConfig config;
    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(4, 8,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(512), new ThreadPoolExecutor.CallerRunsPolicy());

    public void run(ChowhBot bot, EventWrapper eventWrapper) {
        List<EventMethod> eventMethods = eventRegister.getEventMethods(eventWrapper.getEvent().getClass());
        eventMethods.stream()
                // 按顺序执行（异步方法仅保证开始时间）
                .sorted(Comparator.comparingInt(e -> e.getEventListener().order()))
                .forEach(eventMethod -> {
                    if (eventMethod.getEventListener().async()) {
                        runAsync(bot, eventMethod, eventWrapper);
                    } else {
                        run(bot, eventMethod, eventWrapper);
                    }
                });
    }

    private void runAsync(ChowhBot bot, EventMethod eventMethod, EventWrapper eventWrapper){
        executor.execute(()-> run(bot, eventMethod, eventWrapper));
    }

    private void run(ChowhBot bot, EventMethod eventMethod, EventWrapper eventWrapper){
        EventHandlerInterceptorComposite interceptors = config.getInterceptors();
        try {
            if (interceptors.preHandle(bot, eventMethod, eventWrapper)) {
                Object[] args = getArgs(bot, eventMethod, eventWrapper);
                Object res = eventMethod.getMethod().invoke(eventMethod.getBean(), args);
                interceptors.postHandle(bot, eventMethod, eventWrapper, res); // 后置操作
            }
        } catch (Exception e) {
            log.error("[{}]执行失败：{}", ListenerUtils.getListenerName(eventMethod), e.getMessage(), e);
        }
    }


    private Object[] getArgs(ChowhBot bot, EventMethod eventMethod, EventWrapper eventWrapper) throws Exception {
        List<EventParam> params = eventMethod.getParams();
        Object[] args = new Object[params.size()];
        ListenerArgumentResolverComposite argumentResolvers = config.getArgumentResolvers();
        for (int i = 0; i < params.size(); i++) {
            EventParam parameter = params.get(i);
            if (argumentResolvers.supportsParameter(bot, eventMethod, parameter)) {
                args[i] = argumentResolvers.resolveArgument(bot, eventMethod, parameter, eventWrapper);
            } else {
                args[i] = null;
                log.warn("监听器[{}]不支持的参数：{}", ListenerUtils.getListenerName(eventMethod), parameter);
            }
        }
        return args;
    }
}

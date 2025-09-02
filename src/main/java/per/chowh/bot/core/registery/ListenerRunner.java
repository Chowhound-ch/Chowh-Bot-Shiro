package per.chowh.bot.core.registery;

import com.mikuac.shiro.dto.event.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import per.chowh.bot.core.bot.domain.ChowhBot;
import per.chowh.bot.core.registery.config.EventListenerConfig;
import per.chowh.bot.core.registery.domain.EventMethod;
import per.chowh.bot.core.registery.domain.EventParam;
import per.chowh.bot.core.registery.support.ListenerArgumentResolverComposite;
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

    public void run(ChowhBot bot, Event event) {
        List<EventMethod> eventMethods = eventRegister.getEventMethods(event.getClass());
        eventMethods.stream()
                .sorted(Comparator.comparingInt(e -> e.getEventListener().order()))
                .forEach(eventMethod -> {
                    if (eventMethod.getEventListener().async()) {
                        runAsync(bot, eventMethod, event);
                    } else {
                        run(bot, eventMethod, event);
                    }
                });
    }

    private void runAsync(ChowhBot bot, EventMethod eventMethod, Event event){
        executor.execute(()-> runAsync(bot, eventMethod, event));
    }

    private void run(ChowhBot bot, EventMethod eventMethod, Event event){
        long start = System.currentTimeMillis();
        try {
            Object[] args = getArgs(bot, eventMethod, event);
            Object res = eventMethod.getMethod().invoke(args);
            listenerPostProcessor(res, bot, eventMethod, event);
            long end = System.currentTimeMillis();
            log.info("监听器[{}]执行成功，耗时：{}s", ListenerUtils.getListenerName(eventMethod), (start - end) / 1000.0);
        } catch (Exception e) {
            log.error("[{}]执行失败：{}", ListenerUtils.getListenerName(eventMethod), e.getMessage(), e);
        }
    }


    private Object[] getArgs(ChowhBot bot, EventMethod eventMethod, Event event) throws Exception {
        List<EventParam> params = eventMethod.getParams();
        Object[] args = new Object[params.size() + 1];
        ListenerArgumentResolverComposite argumentResolvers = config.getArgumentResolvers();
        args[0] = eventMethod.getBean();
        for (int i = 0; i < params.size(); i++) {
            EventParam parameter = params.get(i);
            if (argumentResolvers.supportsParameter(parameter)) {
                args[i] = argumentResolvers.resolveArgument(bot, event, parameter);
            } else {
                args[i] = null;
                log.warn("监听器[{}]不支持的参数：{}", ListenerUtils.getListenerName(eventMethod), parameter);
            }
        }
        return args;
    }

    protected void listenerPostProcessor(Object res, ChowhBot bot, EventMethod eventMethod, Event event) {
    }
}

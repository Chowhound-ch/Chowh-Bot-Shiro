package per.chowh.bot.core.registery;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import per.chowh.bot.core.bot.domain.ChowhBot;
import per.chowh.bot.core.event.message.GroupMessageEvent;
import per.chowh.bot.core.event.message.PrivateMessageEvent;
import per.chowh.bot.core.registery.config.EventListenerConfig;
import per.chowh.bot.core.registery.domain.EventInvokeLog;
import per.chowh.bot.core.registery.domain.EventMethod;
import per.chowh.bot.core.registery.domain.EventParam;
import per.chowh.bot.core.registery.interceptor.EventHandlerInterceptorComposite;
import per.chowh.bot.core.registery.mapper.BotLogMapper;
import per.chowh.bot.core.registery.support.ListenerArgumentResolverComposite;
import per.chowh.bot.core.utils.EventWrapper;
import per.chowh.bot.core.utils.ListenerUtils;
import per.chowh.bot.utils.exception.CmdExecException;

import javax.net.ssl.SSLHandshakeException;
import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
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
    @Autowired
    private BotLogMapper botLogMapper;
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
                recordInvocation(eventMethod, eventWrapper);// 记录触发日志
                Object res = eventMethod.getMethod().invoke(eventMethod.getBean(), args);
                interceptors.postHandle(bot, eventMethod, eventWrapper, res); // 后置操作
            }
        } catch (Exception e) {
            String msg = getErrorMsg(e);
            bot.sendMsg(eventWrapper, "执行失败：" + msg, false);
            log.error("[{}]执行失败：{}", ListenerUtils.getListenerName(eventMethod), msg, e);
            throw new RuntimeException(e);
        }
    }


    private void recordInvocation(EventMethod eventMethod, EventWrapper eventWrapper) {
        Long groupId;
        switch (eventWrapper.getEvent()) {
            case GroupMessageEvent group when group.getGroupId() != null -> groupId = group.getGroupId();
            case PrivateMessageEvent ignored -> groupId = null;
            default -> { return; }
        }
        Long userId = eventWrapper.getUserId();
        if (userId == null) {
            return;
        }
        String key = groupId == null ? userId.toString() : groupId + "#" + userId;
        var listener = eventMethod.getEventListener();
        String message = listener.isRaw() ? eventWrapper.getRawMessage() : eventWrapper.getMessage();
        EventInvokeLog invokeLog = eventMethod.getInvokeLog().compute(key,
                (ignored, previous) -> new EventInvokeLog(userId, groupId, Instant.now(), message));
        try {
            botLogMapper.insert(eventMethod.getMethodId(), invokeLog);
        } catch (DataAccessException e) {
            log.warn("[{}]写入 bot_log 失败", eventMethod.getMethodId(), e);
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

    private String getErrorMsg(Exception e) {
        String msg =  (e.getMessage() == null ? "" : e.getMessage());
        if (e instanceof InvocationTargetException invException) { // 反射被调用方法中抛出的异常
            Throwable targetException = invException.getTargetException();
            if (targetException == null) {
                return msg;
            }
            if (targetException.getClass() == SSLHandshakeException.class) {
                return "SSL连接异常，请检查网络及代理配置";
            } else if (targetException.getClass() == CmdExecException.class) {
                String errMsg = targetException.getMessage();
                int i;
                if ((i = errMsg.indexOf("jmcomic.jm_exception.")) > 0) { // jm脚本报错
                    return errMsg.substring(i);
                }
            }
            return targetException.getMessage();
        }
        return msg;
    }
}

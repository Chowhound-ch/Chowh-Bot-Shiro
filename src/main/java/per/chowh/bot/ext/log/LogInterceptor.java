package per.chowh.bot.ext.log;

import jakarta.annotation.Priority;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import per.chowh.bot.core.bot.domain.ChowhBot;
import per.chowh.bot.core.registery.domain.EventMethod;
import per.chowh.bot.core.registery.interceptor.EventHandlerInterceptor;
import per.chowh.bot.core.utils.EventWrapper;
import per.chowh.bot.core.utils.ListenerUtils;

/**
 * 记录日志
 * @author : Chowhound
 * @since : 2025/9/3 - 14:11
 */
// 使用Priority保证优先级最高
@Priority(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
@Component
public class LogInterceptor implements EventHandlerInterceptor {
    private final ThreadLocal<Long> startTime = new ThreadLocal<>();

    @Override
    public boolean preHandle(ChowhBot bot, EventMethod method, EventWrapper eventWrapper) {
        long currentTimeMillis = System.currentTimeMillis();
        startTime.set(currentTimeMillis);
        log.info("监听器[{}]：开始执行", ListenerUtils.getListenerName(method));
        return true;
    }

    @Override
    public void postHandle(ChowhBot bot, EventMethod method, EventWrapper eventWrapper, Object returnValue) {
        Long start = this.startTime.get();
        long end = System.currentTimeMillis();
        log.info("监听器[{}]执行成功，耗时：{}s", ListenerUtils.getListenerName(method), (start - end) / 1000.0);
    }
}

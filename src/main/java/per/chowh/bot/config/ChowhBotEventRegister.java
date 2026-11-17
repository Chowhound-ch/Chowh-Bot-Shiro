package per.chowh.bot.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Role;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import per.chowh.bot.common.constants.Constants;
import per.chowh.bot.core.registery.EventRegister;
import per.chowh.bot.core.registery.domain.EventMethod;
import per.chowh.bot.ext.filter.annotation.EventFilter;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

/**
 * @author : Chowhound
 * @since : 2025/9/5 - 9:50
 */
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@Component
public class ChowhBotEventRegister extends EventRegister {
    private static final Duration INVOKE_LOG_RETENTION = Duration.ofHours(24);

    // 支持EventFilter注解
    @Override
    public void beforeRegister(EventMethod eventMethod) {
        Method method = eventMethod.getMethod();
        EventFilter annotation = AnnotationUtils.findAnnotation(method, EventFilter.class);
        if (annotation != null) {
            eventMethod.putExt(Constants.EVENT_FILTER, annotation);
        }
    }

    @Scheduled(fixedDelay = 30, timeUnit = TimeUnit.MINUTES)
    public void evictExpiredInvokeLogs() {
        Instant cutoff = Instant.now().minus(INVOKE_LOG_RETENTION);
        for (EventMethod eventMethod : eventMethods) {
            eventMethod.getInvokeLog().values().removeIf(invoke -> !invoke.getInvokeTime().isAfter(cutoff));
        }
    }
}

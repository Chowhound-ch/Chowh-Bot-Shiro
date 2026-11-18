package per.chowh.bot.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Role;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import per.chowh.bot.common.constants.Constants;
import per.chowh.bot.core.registery.EventRegister;
import per.chowh.bot.core.registery.domain.EventMethod;
import per.chowh.bot.ext.dialogue.annotation.SessionDependOn;
import per.chowh.bot.ext.filter.annotation.EventFilter;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
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
        // EventFilter注解支持
        Method method = eventMethod.getMethod();
        EventFilter eventFilter = AnnotationUtils.findAnnotation(method, EventFilter.class);
        if (eventFilter != null) {
            eventMethod.putExt(Constants.EVENT_FILTER, eventFilter);
        }

        // SessionDependOn注解支持
        SessionDependOn sessionDependOn = AnnotationUtils.findAnnotation(method, SessionDependOn.class);
        if (sessionDependOn != null) {
            eventMethod.putExt(Constants.SESSION_DEPEND, sessionDependOn);
        }
    }

    @Scheduled(fixedDelay = 60, timeUnit = TimeUnit.MINUTES)
    public void evictExpiredInvokeLogs() {
        long defaultRetention = INVOKE_LOG_RETENTION.toNanos();
        Map<String, Long> retentionByMethod = new HashMap<>();
        for (EventMethod eventMethod : eventMethods) {
            if (eventMethod.getExt(Constants.SESSION_DEPEND) instanceof SessionDependOn session) {
                long retention = session.unit().toNanos(session.time());
                if (retention > defaultRetention) {
                    retentionByMethod.merge(eventMethod.getMethodId(), retention, Math::max);
                    for (String dependency : session.dependOn()) {
                        retentionByMethod.merge(dependency, retention, Math::max);
                    }
                }
            }
        }

        Instant now = Instant.now();
        for (EventMethod eventMethod : eventMethods) {
            Instant cutoff = now.minusNanos(retentionByMethod.getOrDefault(eventMethod.getMethodId(), defaultRetention));
            eventMethod.getInvokeLog().values().removeIf(invoke -> invoke.getInvokeTime().isBefore(cutoff));
        }
    }
}

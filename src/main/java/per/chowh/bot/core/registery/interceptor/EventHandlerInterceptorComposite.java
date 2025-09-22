package per.chowh.bot.core.registery.interceptor;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import per.chowh.bot.core.bot.domain.ChowhBot;
import per.chowh.bot.core.registery.domain.EventMethod;
import per.chowh.bot.core.utils.EventWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : Chowhound
 * @since : 2025/9/3 - 13:44
 */
@SuppressWarnings("UnusedReturnValue")
@Getter
@Slf4j
@Configuration
public class EventHandlerInterceptorComposite implements EventHandlerInterceptor, BeanPostProcessor {
    private final List<EventHandlerInterceptor> interceptors = new ArrayList<>();

    public EventHandlerInterceptorComposite addInterceptor(EventHandlerInterceptor argumentResolver) {
        interceptors.add(argumentResolver);
        AnnotationAwareOrderComparator.sort(this.interceptors);
        return this;
    }
    public EventHandlerInterceptorComposite addInterceptorLast(EventHandlerInterceptor argumentResolver) {
        interceptors.add(argumentResolver);
        return this;
    }

    public EventHandlerInterceptorComposite addInterceptor(List<? extends EventHandlerInterceptor> argumentResolvers) {
        this.interceptors.addAll(argumentResolvers);
        AnnotationAwareOrderComparator.sort(this.interceptors);
        return this;
    }

    @Override
    public boolean preHandle(ChowhBot bot, EventMethod method, EventWrapper event) {
        for (EventHandlerInterceptor interceptor : interceptors) {
            if (!interceptor.preHandle(bot, method, event)) {
                return false;
            }
        }
        return true;
    }


    @Override
    public void postHandle(ChowhBot bot, EventMethod method, EventWrapper event, Object returnValue) {
        for (int i = interceptors.size() - 1; i >= 0; i--) {
            interceptors.get(i).postHandle(bot, method, event, returnValue);
        }
    }

    public void sort() {
        AnnotationAwareOrderComparator.sort(this.interceptors);
    }

    @Override
    public Object postProcessAfterInitialization(@NotNull Object bean, @NotNull String beanName) throws BeansException {
        if (bean instanceof EventHandlerInterceptor interceptor) {
            this.addInterceptor(interceptor);
        }
        return bean;
    }
}

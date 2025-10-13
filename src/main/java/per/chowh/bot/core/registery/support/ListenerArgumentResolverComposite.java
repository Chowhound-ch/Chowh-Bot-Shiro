package per.chowh.bot.core.registery.support;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.OrderComparator;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import per.chowh.bot.core.bot.domain.ChowhBot;
import per.chowh.bot.core.registery.domain.EventMethod;
import per.chowh.bot.core.registery.domain.EventParam;
import per.chowh.bot.core.utils.EventWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : Chowhound
 * @since : 2025/9/2 - 11:16
 */
@SuppressWarnings({"UnusedReturnValue", "unused"})
@Configuration
public class ListenerArgumentResolverComposite implements ListenerArgumentResolver, BeanPostProcessor {

    @Getter
    private final List<ListenerArgumentResolver> argumentResolvers = new ArrayList<>();

    private final Map<EventParam, ListenerArgumentResolver> argumentResolverCache =
            new ConcurrentHashMap<>(256);

    public ListenerArgumentResolverComposite addResolver(ListenerArgumentResolver argumentResolver) {
        argumentResolvers.add(argumentResolver);
        AnnotationAwareOrderComparator.sort(this.argumentResolvers);
        return this;
    }
    public ListenerArgumentResolverComposite addResolverLast(ListenerArgumentResolver argumentResolver) {
        argumentResolvers.add(argumentResolver);
        return this;
    }

    public ListenerArgumentResolverComposite addResolver(List<? extends ListenerArgumentResolver> argumentResolvers) {
        this.argumentResolvers.addAll(argumentResolvers);
        AnnotationAwareOrderComparator.sort(this.argumentResolvers);
        return this;
    }

    public ListenerArgumentResolver getArgumentResolver(ChowhBot bot, EventMethod method, EventParam parameter) {
        // 先取缓存
        ListenerArgumentResolver result = this.argumentResolverCache.get(parameter);
        if (result == null) {
            for (ListenerArgumentResolver resolver : this.argumentResolvers) {
                if (resolver.supportsParameter(bot, method, parameter)) {
                    result = resolver;
                    this.argumentResolverCache.put(parameter, result);
                    break;
                }
            }
        }
        return result;
    }


    @Override
    public boolean supportsParameter(ChowhBot bot, EventMethod method, EventParam parameter) {
        return getArgumentResolver(bot, method, parameter) != null;
    }

    @Override
    public Object resolveArgument(ChowhBot bot, EventMethod method, EventParam parameter, EventWrapper eventWrapper) throws Exception {
        ListenerArgumentResolver resolver = getArgumentResolver(bot, method, parameter);
        if (resolver == null) {
            throw new IllegalArgumentException("无法解析的参数[" + parameter.getName() + "]");
        }
        return resolver.resolveArgument(bot,  method, parameter, eventWrapper);
    }

    public void sort() {
        AnnotationAwareOrderComparator.sort(this.argumentResolvers);
    }

    @Override
    public Object postProcessAfterInitialization(@NotNull Object bean, @NotNull String beanName) throws BeansException {
        if (bean instanceof ListenerArgumentResolver resolver) {
            this.addResolver(resolver);
        }
        return bean;
    }
}

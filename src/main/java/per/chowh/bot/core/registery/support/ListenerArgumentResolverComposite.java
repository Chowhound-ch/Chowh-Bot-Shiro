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
 * 参数解析器的组合，也是加载其他参数解析器的地方和参数解析实际调用的地方
 * @author : Chowhound
 * @since : 2025/9/2 - 11:16
 */
@SuppressWarnings({"UnusedReturnValue", "unused"})
@Configuration
public class ListenerArgumentResolverComposite implements ListenerArgumentResolver, BeanPostProcessor {

    @Getter
    private final List<ListenerArgumentResolver> argumentResolvers = new ArrayList<>();
    @Getter
    private final List<CoreListenerArgumentResolver> coreArgumentResolvers = new ArrayList<>();

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

    public ListenerArgumentResolverComposite addCoreResolver(CoreListenerArgumentResolver argumentResolver) {
        coreArgumentResolvers.add(argumentResolver);
        AnnotationAwareOrderComparator.sort(this.coreArgumentResolvers);
        return this;
    }
    public ListenerArgumentResolverComposite addCoreResolverLast(CoreListenerArgumentResolver argumentResolver) {
        coreArgumentResolvers.add(argumentResolver);
        return this;
    }

    public ListenerArgumentResolverComposite addCoreResolver(List<? extends CoreListenerArgumentResolver> argumentResolvers) {
        this.coreArgumentResolvers.addAll(argumentResolvers);
        AnnotationAwareOrderComparator.sort(this.coreArgumentResolvers);
        return this;
    }

    public ListenerArgumentResolver getArgumentResolver(ChowhBot bot, EventMethod method, EventParam parameter) {
        // 先取缓存
        ListenerArgumentResolver result = this.argumentResolverCache.get(parameter);
        if (result == null) {
            // 先取核心参数解析器
            for (ListenerArgumentResolver resolver : this.coreArgumentResolvers) {
                if (resolver.supportsParameter(bot, method, parameter)) {
                    result = resolver;
                    this.argumentResolverCache.put(parameter, result);
                    break;
                }
            }
            if (result == null) {
                // 再取普通参数解析器
                for (ListenerArgumentResolver resolver : this.argumentResolvers) {
                    if (resolver.supportsParameter(bot, method, parameter)) {
                        result = resolver;
                        this.argumentResolverCache.put(parameter, result);
                        break;
                    }
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
        if (bean instanceof CoreListenerArgumentResolver resolver) {
            this.addCoreResolver(resolver);
        } else if (bean instanceof ListenerArgumentResolver resolver) {
            this.addResolver(resolver);
        }
        return bean;
    }
}

package per.chowh.bot.core.registery.support;

import com.mikuac.shiro.dto.event.Event;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.OrderComparator;
import per.chowh.bot.core.bot.domain.ChowhBot;
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
@Configuration
public class ListenerArgumentResolverComposite implements ListenerArgumentResolver, BeanPostProcessor {

    @Getter
    private final List<ListenerArgumentResolver> argumentResolvers = new ArrayList<>();

    private final Map<EventParam, ListenerArgumentResolver> argumentResolverCache =
            new ConcurrentHashMap<>(256);

    public ListenerArgumentResolverComposite addResolver(ListenerArgumentResolver argumentResolver) {
        argumentResolvers.add(argumentResolver);
        OrderComparator.sort(this.argumentResolvers);
        return this;
    }
    public ListenerArgumentResolverComposite addResolverLast(ListenerArgumentResolver argumentResolver) {
        argumentResolvers.add(argumentResolver);
        return this;
    }

    public ListenerArgumentResolverComposite addResolver(List<? extends ListenerArgumentResolver> argumentResolvers) {
        this.argumentResolvers.addAll(argumentResolvers);
        OrderComparator.sort(this.argumentResolvers);
        return this;
    }

    public ListenerArgumentResolver getArgumentResolver(EventParam parameter) {
        // 先取缓存
        ListenerArgumentResolver result = this.argumentResolverCache.get(parameter);
        if (result == null) {
            for (ListenerArgumentResolver resolver : this.argumentResolvers) {
                if (resolver.supportsParameter(parameter)) {
                    result = resolver;
                    this.argumentResolverCache.put(parameter, result);
                    break;
                }
            }
        }
        return result;
    }


    @Override
    public boolean supportsParameter(EventParam parameter) {
        return getArgumentResolver(parameter) != null;
    }

    @Override
    public Object resolveArgument(ChowhBot bot, EventWrapper eventWrapper, EventParam parameter) throws Exception {
        ListenerArgumentResolver resolver = getArgumentResolver(parameter);
        if (resolver == null) {
            throw new IllegalArgumentException("无法解析的参数[" + parameter.getName() + "]");
        }
        return resolver.resolveArgument(bot, eventWrapper, parameter);
    }

    public void sort() {
        OrderComparator.sort(this.argumentResolvers);
    }

    @Override
    public Object postProcessAfterInitialization(@NotNull Object bean, @NotNull String beanName) throws BeansException {
        if (bean instanceof ListenerArgumentResolver resolver) {
            this.addResolver(resolver);
        }
        return bean;
    }
}

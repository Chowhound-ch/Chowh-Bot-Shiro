package per.chowh.bot.core.registery.support;

import com.mikuac.shiro.dto.event.Event;
import lombok.Getter;
import per.chowh.bot.core.bot.domain.ChowhBot;
import per.chowh.bot.core.registery.domain.EventParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @TODO 暂无使用，
 * @author : Chowhound
 * @since : 2025/9/2 - 11:16
 */
public class ListenerReturnResolverComposite implements ListenerReturnResolver{

    @Getter
    private final List<ListenerReturnResolver> argumentResolvers = new ArrayList<>();

    public ListenerReturnResolverComposite addResolver(ListenerReturnResolver argumentResolver) {
        argumentResolvers.add(argumentResolver);
        return this;
    }

    public ListenerReturnResolverComposite addResolver(List<? extends ListenerReturnResolver> argumentResolvers) {
        this.argumentResolvers.addAll(argumentResolvers);
        return this;
    }

    public void clear() {
        this.argumentResolvers.clear();
    }

    public ListenerReturnResolver getReturnResolver(Object returnValue) {
        for (ListenerReturnResolver resolver : this.argumentResolvers) {
            if (resolver.supportsReturn(returnValue)) {
                return resolver;
            }
        }
        return null;
    }

    @Override
    public boolean supportsReturn(Object returnValue) {
        return getReturnResolver(returnValue) != null;
    }

    @Override
    public Object resolveReturn(ChowhBot bot, Event event, Object returnValue) throws Exception {
        ListenerReturnResolver resolver = getReturnResolver(returnValue);
        if (resolver == null) {
            throw new IllegalArgumentException("无法解析的返回值[" + returnValue + "]");
        }
        return resolver.resolveReturn(bot, event, returnValue);
    }


}

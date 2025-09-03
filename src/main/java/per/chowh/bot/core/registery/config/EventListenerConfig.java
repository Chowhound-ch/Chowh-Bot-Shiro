package per.chowh.bot.core.registery.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import per.chowh.bot.core.registery.interceptor.EventHandlerInterceptorComposite;
import per.chowh.bot.core.registery.support.*;

/**
 * @author : Chowhound
 * @since : 2025/9/2 - 10:52
 */
@Getter
@Configuration
public class EventListenerConfig{
    @Autowired
    private ListenerArgumentResolverComposite argumentResolvers;
    @Autowired
    private EventHandlerInterceptorComposite interceptors;


}

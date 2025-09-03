package per.chowh.bot.core.registery.interceptor;

import per.chowh.bot.core.bot.domain.ChowhBot;
import per.chowh.bot.core.registery.domain.EventMethod;
import per.chowh.bot.core.utils.EventWrapper;

/**
 * @author : Chowhound
 * @since : 2025/9/3 - 13:44
 */
public interface EventHandlerInterceptor {
    default boolean preHandle(ChowhBot bot, EventMethod method, EventWrapper event){
        return true;
    }

    default void postHandle(ChowhBot bot, EventMethod method, EventWrapper event, Object returnValue){
    }
}

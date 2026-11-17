package per.chowh.bot.ext.dialogue;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import per.chowh.bot.core.bot.domain.ChowhBot;
import per.chowh.bot.core.registery.domain.EventMethod;
import per.chowh.bot.core.registery.interceptor.EventHandlerInterceptor;
import per.chowh.bot.core.utils.EventWrapper;

@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class DialogueEventHandlerInterceptor implements EventHandlerInterceptor {

    @Override
    public boolean preHandle(ChowhBot bot, EventMethod method, EventWrapper event) {


        return EventHandlerInterceptor.super.preHandle(bot, method, event);
    }
}

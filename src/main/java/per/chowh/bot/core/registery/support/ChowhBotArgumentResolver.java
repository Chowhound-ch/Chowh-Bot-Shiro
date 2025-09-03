package per.chowh.bot.core.registery.support;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import per.chowh.bot.core.bot.domain.ChowhBot;
import per.chowh.bot.core.registery.domain.EventMethod;
import per.chowh.bot.core.registery.domain.EventParam;
import per.chowh.bot.core.utils.EventWrapper;

/**
 * @author : Chowhound
 * @since : 2025/9/2 - 10:58
 */
@Order(1)
@Component
public class ChowhBotArgumentResolver implements ListenerArgumentResolver{
    @Override
    public boolean supportsParameter(ChowhBot bot, EventMethod method, EventParam parameter) {
        return parameter.getParameter().getType().isAssignableFrom(ChowhBot.class);
    }

    @Override
    public Object resolveArgument(ChowhBot bot, EventMethod method, EventParam parameter, EventWrapper eventWrapper) {
        return bot;
    }
}

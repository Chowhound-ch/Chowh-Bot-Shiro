package per.chowh.bot.core.registery.support;

import org.springframework.core.annotation.Order;
import per.chowh.bot.core.bot.domain.ChowhBot;
import per.chowh.bot.core.registery.domain.EventParam;
import per.chowh.bot.core.utils.EventWrapper;

/**
 * @author : Chowhound
 * @since : 2025/9/2 - 10:58
 */
@Order(1)
public class ChowhBotArgumentResolver implements ListenerArgumentResolver{
    @Override
    public boolean supportsParameter(EventParam parameter) {
        return parameter.getParameter().getType().isAssignableFrom(ChowhBot.class);
    }

    @Override
    public Object resolveArgument(ChowhBot bot, EventWrapper eventWrapper, EventParam parameter) {
        return bot;
    }
}

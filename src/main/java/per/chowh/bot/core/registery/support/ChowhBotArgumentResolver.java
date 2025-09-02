package per.chowh.bot.core.registery.support;

import com.mikuac.shiro.dto.event.Event;
import org.springframework.core.annotation.Order;
import per.chowh.bot.core.bot.domain.ChowhBot;
import per.chowh.bot.core.registery.domain.EventMethod;
import per.chowh.bot.core.registery.domain.EventParam;

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
    public Object resolveArgument(ChowhBot bot, Event event, EventParam parameter) {
        return bot;
    }
}

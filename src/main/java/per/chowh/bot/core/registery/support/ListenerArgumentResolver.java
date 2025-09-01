package per.chowh.bot.core.registery.support;

import per.chowh.bot.core.bot.domain.ChowhBot;
import per.chowh.bot.core.model.event.Event;
import per.chowh.bot.core.registery.domain.EventParam;

/**
 * @author : Chowhound
 * @since : 2025/8/29 - 13:45
 */
public interface ListenerArgumentResolver {

    boolean supportsParameter(EventParam parameter);

    Object resolveArgument(EventParam parameter, Event event, ChowhBot bot) throws Exception;
}

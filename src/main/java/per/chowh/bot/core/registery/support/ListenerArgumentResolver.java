package per.chowh.bot.core.registery.support;

import per.chowh.bot.core.bot.domain.ChowhBot;
import per.chowh.bot.core.registery.domain.EventParam;
import per.chowh.bot.core.utils.EventWrapper;

/**
 * @author : Chowhound
 * @since : 2025/8/29 - 13:45
 */
public interface ListenerArgumentResolver{

    boolean supportsParameter(EventParam parameter);

    Object resolveArgument(ChowhBot bot, EventWrapper eventWrapper, EventParam parameter) throws Exception;
}

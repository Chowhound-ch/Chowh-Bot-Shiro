package per.chowh.bot.core.registery.support;

import per.chowh.bot.core.bot.domain.ChowhBot;
import per.chowh.bot.core.registery.domain.EventMethod;
import per.chowh.bot.core.registery.domain.EventParam;
import per.chowh.bot.core.utils.EventWrapper;

/**
 * @author : Chowhound
 * @since : 2025/8/29 - 13:45
 */
public interface ListenerArgumentResolver{

    boolean supportsParameter(ChowhBot bot, EventMethod method, EventParam parameter);

    Object resolveArgument(ChowhBot bot, EventMethod method, EventParam parameter, EventWrapper eventWrapper) throws Exception;
}

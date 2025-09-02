package per.chowh.bot.core.registery.support;

import com.mikuac.shiro.dto.event.Event;
import per.chowh.bot.core.bot.domain.ChowhBot;
import per.chowh.bot.core.registery.domain.EventParam;

/**
 * @author : Chowhound
 * @since : 2025/8/29 - 13:45
 */
public interface ListenerReturnResolver {

    boolean supportsReturn(Object returnValue);

    Object resolveReturn(ChowhBot bot, Event event, Object returnValue) throws Exception;
}

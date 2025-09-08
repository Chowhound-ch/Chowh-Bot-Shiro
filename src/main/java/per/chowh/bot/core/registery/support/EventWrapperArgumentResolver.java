package per.chowh.bot.core.registery.support;

import org.springframework.stereotype.Component;
import per.chowh.bot.core.bot.domain.ChowhBot;
import per.chowh.bot.core.registery.domain.EventMethod;
import per.chowh.bot.core.registery.domain.EventParam;
import per.chowh.bot.core.utils.EventWrapper;

/**
 * @author : Chowhound
 * @since : 2025/9/8 - 16:51
 */
@Component
public class EventWrapperArgumentResolver implements ListenerArgumentResolver{
    @Override
    public boolean supportsParameter(ChowhBot bot, EventMethod method, EventParam parameter) {
        return parameter.getParameter().getType().isAssignableFrom(EventWrapper.class);
    }

    @Override
    public Object resolveArgument(ChowhBot bot, EventMethod method, EventParam parameter, EventWrapper eventWrapper) {
        return eventWrapper;
    }
}
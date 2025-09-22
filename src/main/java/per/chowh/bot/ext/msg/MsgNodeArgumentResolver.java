package per.chowh.bot.ext.msg;

import org.springframework.stereotype.Component;
import per.chowh.bot.core.bot.domain.ChowhBot;
import per.chowh.bot.core.registery.domain.EventMethod;
import per.chowh.bot.core.registery.domain.EventParam;
import per.chowh.bot.core.registery.support.ListenerArgumentResolver;
import per.chowh.bot.core.utils.EventWrapper;

/**
 * @author : Chowhound
 * @since : 2025/9/18 - 13:53
 */
@Component
public class MsgNodeArgumentResolver implements ListenerArgumentResolver {
    @Override
    public boolean supportsParameter(ChowhBot bot, EventMethod method, EventParam parameter) {
        return false;
    }

    @Override
    public Object resolveArgument(ChowhBot bot, EventMethod method, EventParam parameter, EventWrapper eventWrapper) throws Exception {
        return null;
    }
}

package per.chowh.bot.core.registery.support;

import com.mikuac.shiro.dto.event.Event;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import per.chowh.bot.core.bot.domain.ChowhBot;
import per.chowh.bot.core.registery.domain.EventMethod;
import per.chowh.bot.core.registery.domain.EventParam;
import per.chowh.bot.core.utils.EventWrapper;

/**
 * 处理Event参数，默认优先级最高，需要顶掉时通过@Priority顶掉
 * @author : Chowhound
 * @since : 2025/9/2 - 10:59
 * @see jakarta.annotation.Priority
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class EventArgumentResolver implements ListenerArgumentResolver{
    @Override
    public boolean supportsParameter(ChowhBot bot, EventMethod method, EventParam parameter) {
        return parameter.getParameter().getType().isAssignableFrom(Event.class);
    }

    @Override
    public Object resolveArgument(ChowhBot bot, EventMethod method, EventParam parameter, EventWrapper eventWrapper) {
        return eventWrapper.getEvent();
    }
}

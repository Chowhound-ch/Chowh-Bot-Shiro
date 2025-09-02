package per.chowh.bot.core.registery.support;

import com.mikuac.shiro.dto.event.Event;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import per.chowh.bot.core.bot.domain.ChowhBot;
import per.chowh.bot.core.registery.domain.EventMethod;
import per.chowh.bot.core.registery.domain.EventParam;

/**
 * 处理Event参数，默认优先级最高，需要顶掉时通过@Priority顶掉
 * @author : Chowhound
 * @since : 2025/9/2 - 10:59
 * @see jakarta.annotation.Priority
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
public class EventArgumentResolver implements ListenerArgumentResolver{
    @Override
    public boolean supportsParameter(EventParam parameter) {
        return parameter.getParameter().getType().isAssignableFrom(Event.class);
    }

    @Override
    public Object resolveArgument(ChowhBot bot, Event event, EventParam parameter) {
        return event;
    }
}

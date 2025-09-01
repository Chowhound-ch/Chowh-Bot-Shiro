package per.chowh.bot.core.registery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import per.chowh.bot.core.bot.domain.ChowhBot;
import per.chowh.bot.core.model.event.Event;
import per.chowh.bot.core.registery.domain.EventMethod;

import java.util.List;

/**
 * @author : Chowhound
 * @since : 2025/9/1 - 9:59
 */
@Component
public class ListenerRunner {
    @Autowired
    private EventRegister eventRegister;

    public List<Object> run(ChowhBot bot, Event event) {
        List<EventMethod> eventMethods = eventRegister.getEventMethods(event.getClass());

        return null;
    }

}

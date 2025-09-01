package per.chowh.bot.core.registery.api;

import per.chowh.bot.core.bot.domain.ChowhBot;
import per.chowh.bot.core.model.event.Event;

/**
 * @author : Chowhound
 * @since : 2025/9/1 - 10:09
 */
public interface ListenerApi {

    void execute(ChowhBot bot, Event event);

}

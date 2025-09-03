package per.chowh.bot.core.registery.api;

import org.springframework.beans.factory.annotation.Autowired;
import per.chowh.bot.core.bot.domain.ChowhBot;
import per.chowh.bot.core.registery.ListenerRunner;
import per.chowh.bot.core.utils.EventWrapper;

/**
 * @author : Chowhound
 * @since : 2025/9/1 - 14:07
 */
public abstract class AbstractListenerApi implements ListenerApi {
    @Autowired
    protected ListenerRunner listenerRunner;

    public void execute(ChowhBot bot, EventWrapper eventWrapper){
        listenerRunner.run(bot, eventWrapper);
    }
}

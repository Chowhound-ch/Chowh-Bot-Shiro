package per.chowh.bot.core.registery.api;

import com.mikuac.shiro.dto.event.Event;
import org.springframework.beans.factory.annotation.Autowired;
import per.chowh.bot.core.bot.domain.ChowhBot;
import per.chowh.bot.core.registery.ListenerRunner;

/**
 * @author : Chowhound
 * @since : 2025/9/1 - 14:07
 */
public abstract class AbstractListenerApi implements ListenerApi {
    @Autowired
    protected ListenerRunner listenerRunner;

    public void execute(ChowhBot bot, Event event){
        listenerRunner.run(bot, event);
    }
}

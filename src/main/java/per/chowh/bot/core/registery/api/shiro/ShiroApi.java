package per.chowh.bot.core.registery.api.shiro;

import com.mikuac.shiro.annotation.GroupMessageHandler;
import com.mikuac.shiro.annotation.PrivateMessageHandler;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.Event;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import per.chowh.bot.core.bot.domain.ChowhBot;
import per.chowh.bot.core.registery.api.AbstractListenerApi;
import per.chowh.bot.core.utils.EventWrapper;

/**
 * @author : Chowhound
 * @since : 2025/9/1 - 10:09
 */
@Slf4j
@Shiro
@Component
public class ShiroApi extends AbstractListenerApi {

    @GroupMessageHandler
    public void groupMessageHandler(Bot bot, AnyMessageEvent event) {
        exec(bot, event);
    }

    @PrivateMessageHandler
    public void privateMessageHandler(Bot bot, AnyMessageEvent event) {
        exec(bot, event);
    }
    protected void exec(Bot bot, Event event) {
        execute((ChowhBot) bot, new EventWrapper(event));
    }
}

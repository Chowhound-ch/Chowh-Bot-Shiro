package per.chowh.bot.core.registery.api.shiro;

import com.mikuac.shiro.annotation.GroupMessageHandler;
import com.mikuac.shiro.annotation.PrivateMessageHandler;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.Event;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
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
@SuppressWarnings("unused")
public class ShiroApi extends AbstractListenerApi {

    @GroupMessageHandler
    public void groupMessageHandler(Bot bot, GroupMessageEvent event) {
        exec(bot, event);
    }

    @PrivateMessageHandler
    public void privateMessageHandler(Bot bot, PrivateMessageEvent event) {
        exec(bot, event);
    }
    protected void exec(Bot bot, Event event) {
        execute((ChowhBot) bot, new EventWrapper(event));
    }
}

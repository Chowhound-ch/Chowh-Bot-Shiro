package per.chowh.bot.core.registery.api.shiro;

import com.mikuac.shiro.annotation.AnyMessageHandler;
import com.mikuac.shiro.annotation.PrivateMessageHandler;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.Event;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
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

    @AnyMessageHandler
    public void anyMessageHandler(Bot bot, AnyMessageEvent event) {
        if ("private".equals(event.getMessageType())) {
            return;
        }
        log.info("{}[{}]:{}", event.getSender().getCard(), event.getSender().getUserId(), event.getMessage());
        exec(bot, event);
    }

    @PrivateMessageHandler
    public void privateMessageHandler(Bot bot, PrivateMessageEvent event) {
        log.info("{}[{}]:{}", event.getPrivateSender().getNickname(), event.getPrivateSender().getUserId(), event.getMessage());
        exec(bot, event);
    }

//    @PrivateMessageHandler
//    public void privateMessageHandler(Bot bot, PrivateMessageEvent event) {
//        exec(bot, event);
//    }
    protected void exec(Bot bot, Event event) {
        execute((ChowhBot) bot, new EventWrapper(event));
    }
}

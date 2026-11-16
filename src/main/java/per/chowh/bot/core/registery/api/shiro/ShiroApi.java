package per.chowh.bot.core.registery.api.shiro;

import com.mikuac.shiro.annotation.AnyMessageHandler;
import com.mikuac.shiro.annotation.PrivateMessageHandler;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.constant.ActionParams;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.Event;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import per.chowh.bot.core.bot.domain.ChowhBot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.GuildMessageEvent;
import com.mikuac.shiro.dto.event.message.MessageEvent;
import per.chowh.bot.core.registery.api.AbstractListenerApi;
import per.chowh.bot.core.utils.EventWrapper;
import per.chowh.bot.utils.JacksonUtil;

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
        if (ActionParams.PRIVATE.equals(event.getMessageType())) {
            return;
        }
        log.info("{}[{}]:{}", event.getSender().getCard(), event.getSender().getUserId(), event.getMessage());
        exec(bot, event);
    }

    @PrivateMessageHandler
    public void privateMessageHandler(Bot bot, PrivateMessageEvent event) {
        exec(bot, event);
    }

    protected void exec(Bot bot, Event event) {
        if (event instanceof com.mikuac.shiro.dto.event.message.MessageEvent message) {
            Class<? extends per.chowh.bot.core.event.message.MessageEvent> targetType = switch (message) {
                case GuildMessageEvent ignored -> per.chowh.bot.core.event.message.GuildMessageEvent.class;
                case AnyMessageEvent any when ActionParams.PRIVATE.equals(any.getMessageType()) ->
                        per.chowh.bot.core.event.message.PrivateMessageEvent.class;
                case AnyMessageEvent any when ActionParams.GROUP.equals(any.getMessageType()) ->
                        per.chowh.bot.core.event.message.GroupMessageEvent.class;
                case PrivateMessageEvent ignored -> per.chowh.bot.core.event.message.PrivateMessageEvent.class;
                case GroupMessageEvent ignored -> per.chowh.bot.core.event.message.GroupMessageEvent.class;
                default -> per.chowh.bot.core.event.message.AnyMessageEvent.class;
            };
            per.chowh.bot.core.event.message.MessageEvent mapped = JacksonUtil.objectMapper.convertValue(message, targetType);
            // 消息段和纯文本不会随 JSON 序列化，复用 Shiro 已解析的值。
            mapped.setArrayMsg(message.getArrayMsg());
            mapped.setPlainText(message.getPlainText());
            execute((ChowhBot) bot, new EventWrapper(mapped));
        }

    }
}

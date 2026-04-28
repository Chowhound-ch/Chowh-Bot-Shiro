package per.chowh.bot.core.utils;


import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.constant.ActionParams;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.MessageEvent;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;

/**
 * @author : Chowhound
 * @since : 2025/9/4 - 11:45
 */
public class MsgBuilder extends MsgUtils {

    public static MsgBuilder builder() {
        return new MsgBuilder();
    }

    public static MsgBuilder replyBuilder(MessageEvent event) {
        if (ActionParams.PRIVATE.equals(event.getMessageType())) {
            return (MsgBuilder) new MsgBuilder().reply(((PrivateMessageEvent) event).getMessageId());
        } else if (ActionParams.GROUP.equals(event.getMessageType())) {
            return (MsgBuilder) new MsgBuilder().reply(((GroupMessageEvent) event).getMessageId());
        }
        return builder();
    }
}

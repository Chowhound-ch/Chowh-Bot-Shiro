package per.chowh.bot.core.utils;


import com.mikuac.shiro.constant.ActionParams;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.MessageEvent;

/**
 * @author : Chowhound
 * @since : 2025/9/4 - 11:45
 */
public class MsgUtils extends com.mikuac.shiro.common.utils.MsgUtils {


    public static Long getGroupId(MessageEvent event) {
        if (ActionParams.GROUP.equals(event.getMessageType())) {
            return ((GroupMessageEvent) event).getGroupId();
        }
        return null;
    }

}

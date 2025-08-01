package per.chowhound.bot.plugins.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.mikuac.shiro.annotation.MessageHandlerFilter;
import com.mikuac.shiro.annotation.PrivateMessageHandler;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import com.mikuac.shiro.enums.MsgTypeEnum;
import com.mikuac.shiro.model.ArrayMsg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import per.chowhound.bot.utils.JacksonUtil;

import java.util.List;
import java.util.regex.Matcher;

/**
 * @author : Chowhound
 * @since : 2025/8/1 - 10:01
 */
@Slf4j
@Shiro
@Component
public class PermissionPlugins {

    @PrivateMessageHandler
    @MessageHandlerFilter(types = {MsgTypeEnum.image, MsgTypeEnum.forward, MsgTypeEnum.video})
    public void updatePermission(Bot bot, PrivateMessageEvent event, Matcher matcher) {

    }


}

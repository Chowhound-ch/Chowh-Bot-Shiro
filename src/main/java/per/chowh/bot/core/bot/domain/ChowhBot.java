package per.chowh.bot.core.bot.domain;

import com.mikuac.shiro.common.utils.JsonObjectWrapper;
import com.mikuac.shiro.common.utils.ShiroUtils;
import com.mikuac.shiro.constant.ActionParams;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotFactory;
import com.mikuac.shiro.core.BotMessageEventInterceptor;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.action.common.ActionData;
import com.mikuac.shiro.dto.action.common.ActionRaw;
import com.mikuac.shiro.dto.action.common.MsgId;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.MessageEvent;
import com.mikuac.shiro.handler.ActionHandler;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.servlet.mvc.LastModified;
import org.springframework.web.socket.WebSocketSession;
import per.chowh.bot.core.bot.action.NapcatExtendApi;
import per.chowh.bot.core.bot.response.LatestMsgResp;
import per.chowh.bot.core.utils.EventWrapper;
import per.chowh.bot.core.utils.MsgUtils;
import per.chowh.bot.utils.JacksonUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 核心接口
 * @author : Chowhound
 * @since : 2025/7/9 - 14:41
 */
@Getter
@Setter
@SuppressWarnings({"unused", "Duplicates", "UnusedReturnValue"})
public class ChowhBot extends Bot implements NapcatExtendApi {
    public ChowhBot(long selfId, WebSocketSession session, ActionHandler actionHandler, List<Class<? extends BotPlugin>> pluginList, BotFactory.AnnotationMethodContainer annotationMethodContainer, Class<? extends BotMessageEventInterceptor> botMessageEventInterceptor) {
        super(selfId, session, actionHandler, pluginList, annotationMethodContainer, botMessageEventInterceptor);
    }

    @Override
    public ActionData<List<LatestMsgResp>> getRecentContact(int count) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.COUNT, count);
        JsonObjectWrapper result = super.getActionHandler().action(super.getSession(), ExtActionPath.GET_RECENT_CONTACT, params);
        return result != null ? JacksonUtil.readValue(result,
                JacksonUtil.getGenericJavaType(ActionData.class, List.class, LatestMsgResp.class)) : null;
    }

    public ActionData<MsgId> sendMsg(EventWrapper event, String msg, boolean autoEscape) {
        Long groupId = event.getGroupId();
        Long userId = event.getUserId();
        ActionData<MsgId> msgIdActionData = null;
        if (groupId != null) {
            msgIdActionData = sendGroupMsg(groupId, msg, autoEscape);
        } else if (userId != null) {
            msgIdActionData =  sendPrivateMsg(userId, msg, autoEscape);
        }
        MsgUtils.handleErrorMsg(this, event.getEvent(), msgIdActionData);
        return msgIdActionData;
    }

    public ActionData<MsgId> sendMsg(MessageEvent event, String msg, boolean autoEscape) {
        ActionData<MsgId> msgIdActionData = null;
        if (ActionParams.PRIVATE.equals(event.getMessageType())) {
            msgIdActionData = sendPrivateMsg(event.getUserId(), msg, autoEscape);
        } else if (ActionParams.GROUP.equals(event.getMessageType())) {
            msgIdActionData = sendGroupMsg(((GroupMessageEvent) event).getGroupId(), msg, autoEscape);
        }
        MsgUtils.handleErrorMsg(this, event, msgIdActionData);
        return msgIdActionData;
    }
    public ActionRaw uploadFile(MessageEvent event, String file, String name) {
        if (ActionParams.PRIVATE.equals(event.getMessageType())) {
            return uploadPrivateFile(event.getUserId(), file, file);
        }
        if (ActionParams.GROUP.equals(event.getMessageType())) {
            return uploadGroupFile(((GroupMessageEvent) event).getGroupId(), file, file);
        }
        return null;
    }

    public ActionData<MsgId> sendForwardMsg(MessageEvent event, List<String> msgList) {
        List<Map<String, Object>> forwardMsg = ShiroUtils.generateForwardMsg(
                event.getUserId(),
                MsgUtils.getNickName(event),
                msgList
        );
        ActionData<MsgId> msgIdActionData = null;
        if (ActionParams.PRIVATE.equals(event.getMessageType())) {
            msgIdActionData = sendPrivateForwardMsg(event.getUserId(), forwardMsg);
        } else if (ActionParams.GROUP.equals(event.getMessageType())) {
            msgIdActionData = sendGroupForwardMsg(((GroupMessageEvent) event).getGroupId(), forwardMsg);
        }
        MsgUtils.handleErrorMsg(this, event, msgIdActionData);
        return msgIdActionData;
    }
}

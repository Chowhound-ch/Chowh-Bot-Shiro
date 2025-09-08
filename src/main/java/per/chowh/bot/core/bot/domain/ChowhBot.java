package per.chowh.bot.core.bot.domain;

import com.mikuac.shiro.common.utils.JsonObjectWrapper;
import com.mikuac.shiro.constant.ActionParams;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotFactory;
import com.mikuac.shiro.core.BotMessageEventInterceptor;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.action.common.ActionData;
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
@SuppressWarnings({"unused", "Duplicates"})
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

    public ActionData<MsgId> sendMsg(MessageEvent event, String msg, boolean autoEscape) {
        if (ActionParams.PRIVATE.equals(event.getMessageType())) {
            return sendPrivateMsg(event.getUserId(), msg, autoEscape);
        }
        if (ActionParams.GROUP.equals(event.getMessageType())) {
            return sendGroupMsg(((GroupMessageEvent) event).getGroupId(), msg, autoEscape);
        }
        return null;
    }
}

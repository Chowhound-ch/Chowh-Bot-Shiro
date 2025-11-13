package per.chowh.bot.ext.support.msgarr;

import com.fasterxml.jackson.databind.JsonNode;
import com.mikuac.shiro.dto.action.common.ActionData;
import com.mikuac.shiro.dto.action.response.GetForwardMsgResp;
import com.mikuac.shiro.dto.action.response.MsgResp;
import com.mikuac.shiro.dto.event.Event;
import com.mikuac.shiro.dto.event.message.MessageEvent;
import com.mikuac.shiro.enums.MsgTypeEnum;
import com.mikuac.shiro.model.ArrayMsg;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import per.chowh.bot.core.bot.domain.ChowhBot;
import per.chowh.bot.core.registery.domain.EventMethod;
import per.chowh.bot.core.registery.domain.EventParam;
import per.chowh.bot.core.registery.support.ListenerArgumentResolver;
import per.chowh.bot.core.utils.EventWrapper;
import per.chowh.bot.core.utils.MsgUtils;
import per.chowh.bot.ext.support.msgarr.domain.ArrayMsgFlatList;
import per.chowh.bot.utils.JacksonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : Chowhound
 * @since : 2025/10/9 - 11:25
 */
@Order(0)
@Component
public class MsgArrayArgumentResolver implements ListenerArgumentResolver {
    // MessageEvent子类且有ForwardMsg作为参数
    @Override
    public boolean supportsParameter(ChowhBot bot, EventMethod method, EventParam parameter) {
        if (MessageEvent.class.isAssignableFrom(method.getEventClass())) {
            Class<?> type = parameter.getParameter().getType();
            return ArrayMsgFlatList.class.isAssignableFrom(type);
        }
        return false;
    }

    @Override
    public Object resolveArgument(ChowhBot bot, EventMethod method, EventParam parameter, EventWrapper eventWrapper) throws Exception {
        Event event = eventWrapper.getEvent();
        if (event instanceof MessageEvent msgEvent) {
            ArrayMsgFlatList resMsgList = new ArrayMsgFlatList();
            List<ArrayMsg> arrayMsg = msgEvent.getArrayMsg();
            // 合并转发消息只有一条.如果这条消息是转发
            if (arrayMsg != null && !arrayMsg.isEmpty() && MsgTypeEnum.forward.equals(arrayMsg.get(0).getType())) {
                Integer messageId = MsgUtils.getMessageId(msgEvent);
                if (messageId == null) return null;
                ActionData<GetForwardMsgResp> forwardMsg = bot.getForwardMsg(messageId);
                if (forwardMsg == null) return null;
                // 遍历第一层转发消息
                for (MsgResp msgResp : forwardMsg.getData().getMessages()) {
                    getMsgArrayRecur(resMsgList, JacksonUtil.readTree(msgResp.getArrayMsg())); // 开始递归
                }
            } else {
                resMsgList.add(arrayMsg);
            }
            return resMsgList;
        }
        return null;
    }

    private static void getMsgArrayRecur(ArrayMsgFlatList arrayMsgList, JsonNode msg) {
        List<ArrayMsg> arrayMsg = new ArrayList<>();
        for (JsonNode msgItem : msg) {
            String type = msgItem.get("type").asText();
            // 如果是转发消息
            if (MsgTypeEnum.forward.name().equals(type)){
                JsonNode content = msgItem.get("data").get("content");
                for (JsonNode node : content) { // 转发消息中每条消息
                    JsonNode message = node.get("message");
                    getMsgArrayRecur(arrayMsgList, message); // 递归节点列表
                }
                continue;
            }
            // 非转发消息，保存ArrayMsg
            arrayMsg.add(JacksonUtil.readValue(msgItem, ArrayMsg.class));
        }
        if (!arrayMsg.isEmpty()) {
            arrayMsgList.add(arrayMsg);
        }
    }
}

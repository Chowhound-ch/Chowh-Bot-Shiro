package per.chowh.bot.core.utils;

import com.mikuac.shiro.common.utils.ShiroUtils;
import lombok.extern.slf4j.Slf4j;
import per.chowh.bot.core.event.Event;
import per.chowh.bot.core.event.message.AnyMessageEvent;
import per.chowh.bot.core.event.message.GroupMessageEvent;
import per.chowh.bot.core.event.message.MessageEvent;
import per.chowh.bot.core.event.message.PrivateMessageEvent;
import per.chowh.bot.utils.HttpProxyUtils;
import per.chowh.bot.utils.StringUtils;
import tools.jackson.databind.JsonNode;
import com.mikuac.shiro.constant.ActionParams;
import com.mikuac.shiro.dto.action.common.ActionData;
import com.mikuac.shiro.dto.action.response.GetForwardMsgResp;
import com.mikuac.shiro.dto.action.response.MsgResp;
import com.mikuac.shiro.enums.MsgTypeEnum;
import com.mikuac.shiro.model.ArrayMsg;
import per.chowh.bot.core.bot.domain.ChowhBot;
import per.chowh.bot.utils.JacksonUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author : Chowhound
 * @since : 2025/9/9 - 15:02
 */
@Slf4j
public class MsgUtils {
    private static final Pattern CQ_IMAGE =
            Pattern.compile("\\[CQ:image,(?:[^,\\[\\]]+,)*?file=([^,\\[\\]]+)(?:,[^\\[\\]]*)?\\]");

    public static Long getGroupId(MessageEvent event) {
        if (ActionParams.GROUP.equals(event.getMessageType())) {
            return ((GroupMessageEvent) event).getGroupId();
        }
        return null;
    }

    public static String getNickName(MessageEvent event) {
        if (ActionParams.GROUP.equals(event.getMessageType())) {
            return ((AnyMessageEvent) event).getSender().getNickname();
        } else if (ActionParams.PRIVATE.equals(event.getMessageType())) {
            return ((AnyMessageEvent) event).getSender().getNickname();
        }
        return null;
    }

    public static Integer getMessageId(MessageEvent event) {
        if (ActionParams.GROUP.equals(event.getMessageType())) {
            return ((GroupMessageEvent) event).getMessageId();
        } else if (ActionParams.PRIVATE.equals(event.getMessageType())) {
            return ((PrivateMessageEvent) event).getMessageId();
        }
        return null;
    }

    public static List<FileInfo> getImageUrlFromMsg(ChowhBot bot, MessageEvent event, boolean video) {
        List<FileInfo> fileInfos = new ArrayList<>();
        // 先判断是否为forward消息
        List<ArrayMsg> arrayMsg = event.getArrayMsg();

        if (arrayMsg != null && !arrayMsg.isEmpty()) {
            for (ArrayMsg msg : arrayMsg) {
                if (MsgTypeEnum.forward == msg.getType()) {
                    Integer messageId = getMessageId(event);
                    if (messageId == null) continue;
                    ActionData<GetForwardMsgResp> forwardMsg = bot.getForwardMsg(messageId);
                    GetForwardMsgResp forwardMsgData = forwardMsg.getData();
                    List<MsgResp> messages = forwardMsgData.getMessages();
                    // 遍历第一层转发消息
                    for (MsgResp msgResp : messages) {
                        List<ArrayMsg> arrMsg = msgResp.getArrayMsg();
                        getImgUrlRecur(fileInfos, JacksonUtil.readTree(arrMsg), video); // 开始递归
                    }
                } else {
                    FileInfo fileInfo = getFileInfo(msg, video);
                    if (fileInfo != null) {
                        fileInfos.add(fileInfo);
                    }
                }
            }

        }
        return fileInfos;
    }

    /**
     * @param video 是否包括视频
     */
    private static void getImgUrlRecur(List<FileInfo> fileInfoList, JsonNode msg, boolean video) {
        for (JsonNode msgItem : msg) {
            String type = msgItem.get("type").asText();
            // 如果是转发消息
            if (MsgTypeEnum.forward.name().equals(type)){
                JsonNode content = msgItem.get("data").get("content");
                for (JsonNode node : content) { // 转发消息中每条消息
                    JsonNode message = node.get("message");
                    getImgUrlRecur(fileInfoList, message, video); // 递归节点列表
                }
                continue;
            }
            // 非转发消息，尝试获取图片
            FileInfo fileInfo = getFileInfo(msgItem, video);
            if (fileInfo != null) {
                fileInfoList.add(fileInfo);
            }
        }
    }
    private static FileInfo getFileInfo(Object objMsg, boolean video) {
        if (objMsg instanceof ArrayMsg arrayMsg) {
            return getFileInfo(JacksonUtil.readTree(arrayMsg), video);
        } else if (objMsg instanceof JsonNode jsonMsg) {
            return getFileInfo(jsonMsg, video);
        }
        return null;
    }
    private static FileInfo getFileInfo(ArrayMsg arrayMsg, boolean video) {
        return getFileInfo(JacksonUtil.readTree(arrayMsg), video);
    }

    private static FileInfo getFileInfo(JsonNode node, boolean video) {
        String type = node.get("type").asText();
        JsonNode data = node.get("data");
        if (MsgTypeEnum.image.name().equals(type)) {
            // 图片
            return new FileInfo(data.get("file").asText(), data.get("url").asText(), false,
                    "[动画表情]".equals(data.get("summary").asText()));
        } else if (video && MsgTypeEnum.video.name().equals(type)) {
            // 视频
            return new FileInfo(data.get("file").asText(), data.get("url").asText(), true, false);
        }
        return null;
    }

    public static <T> void handleErrorMsg(ChowhBot bot, Event event, ActionData<T> actionData) {
        if (actionData != null && "failed".equals(actionData.getStatus())) {
            if (MessageEvent.class.isAssignableFrom(event.getClass())) {
                bot.sendMsg((MessageEvent) event, "消息发送失败", true);
            }
        }
    }


    private void getImageUrlFromMsgFromForwardMsg(ArrayMsg msg, List<FileInfo> fileInfos, boolean video) {
        if (msg.getType() == MsgTypeEnum.forward) {
//            msg.getData().
        }
    }

    public static String parseTextImageMsg(String msg) {
        MsgBuilder builder = MsgBuilder.builder();
        Matcher matcher = CQ_IMAGE.matcher(msg);
        int textStart = 0;
        while (matcher.find()) {
            builder.text(msg.substring(textStart, matcher.start()));
            textStart = matcher.end();

            String fileUrl = StringUtils.parseByEnvironment(ShiroUtils.unescape(matcher.group(1)));
            if (fileUrl == null) {
                builder.text(matcher.group());
                continue;
            }
            try {
                byte[] bytes = HttpProxyUtils.doGetBytes(fileUrl, true);
                if (bytes.length > 0) {
                    builder.img(bytes);
                } else {
                    builder.text("[图片下载失败]");
                }
            } catch (IOException e) {
                log.warn("图片下载失败，错误类型 {}，url {}", e.getClass().getSimpleName(), fileUrl);
            }
        }
        return builder.text(msg.substring(textStart)).build();
    }
}

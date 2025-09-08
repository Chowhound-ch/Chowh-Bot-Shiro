package per.chowh.bot.core.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.mikuac.shiro.dto.event.Event;
import com.mikuac.shiro.dto.event.message.MessageEvent;
import lombok.Data;
import per.chowh.bot.utils.JacksonUtil;

import java.util.regex.Matcher;

/**
 * @author : Chowhound
 * @since : 2025/9/3 - 14:46
 */
@Data
public class EventWrapper {
    private Event event;
    private Class<? extends Event> eventClass;
    private JsonNode eventJson;
    private Matcher matcher;
    public static final String USER_ID = "userId";
    public static final String GROUP_ID = "groupId";
    public static final String MESSAGE = "message";
    public static final String RAW_MESSAGE = "rawMessage";

    public EventWrapper(Object event) {
        this.event = (Event) event;
        this.eventClass = this.event.getClass();
        eventJson = JacksonUtil.readTree(event);
    }

    public JsonNode get(String name){
        return eventJson.get(name);
    }

    public Long getUserId() {
        JsonNode node = eventJson.get(USER_ID);
        return node == null ? null : node.asLong();
    }

    public Long getGroupId() {
        JsonNode node = eventJson.get(GROUP_ID);
        return node == null ? null : node.asLong();
    }

    public String getMessage() {
        JsonNode node = eventJson.get(MESSAGE);
        return node == null ? null : node.asText();
    }
    public String getRawMessage() {
        JsonNode node = eventJson.get(RAW_MESSAGE);
        return node == null ? null : node.asText();
    }

    public static boolean isEvent(Class<?> event) {
        return Event.class.isAssignableFrom(event);
    }

    public MessageEvent toMessageEvent() {
        return event instanceof MessageEvent ? (MessageEvent) event : null;
    }
}

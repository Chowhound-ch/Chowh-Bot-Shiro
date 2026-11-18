package per.chowh.bot.core.utils;

import per.chowh.bot.core.event.Event;
import per.chowh.bot.core.event.message.GroupMessageEvent;
import per.chowh.bot.core.event.message.MessageEvent;
import per.chowh.bot.core.event.message.PrivateMessageEvent;
import lombok.EqualsAndHashCode;
import tools.jackson.databind.JsonNode;
import lombok.Data;
import per.chowh.bot.utils.JacksonUtil;

import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;

/**
 * @author : Chowhound
 * @since : 2025/9/3 - 14:46
 */
@Data
public class EventWrapper {
    private static final AtomicLong EVENT_SEQUENCE = new AtomicLong();
    @EqualsAndHashCode.Exclude
    private final long sequence = EVENT_SEQUENCE.incrementAndGet();
    private Event event;
    private Class<? extends Event> eventClass;
    private JsonNode eventJson;
    private Matcher matcher;
    public static final String USER_ID = "user_id";
    public static final String GROUP_ID = "group_id";
    public static final String MESSAGE = "message";
    public static final String RAW_MESSAGE = "raw_message";

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
        return node == null || node.isNull() ? null : node.asLong();
    }

    public Long getGroupId() {
        JsonNode node = eventJson.get(GROUP_ID);
        return node == null || node.isNull() ? null : node.asLong();
    }

    public String getMessageSessionKey() {
        Long userId = getUserId();
        if (userId == null) {
            return null;
        }
        return switch (event) {
            case GroupMessageEvent group when group.getGroupId() != null -> group.getGroupId() + "#" + userId;
            case PrivateMessageEvent ignored -> userId.toString();
            default -> null;
        };
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

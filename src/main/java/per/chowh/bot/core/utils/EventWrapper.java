package per.chowh.bot.core.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.mikuac.shiro.dto.event.Event;
import lombok.Data;
import per.chowh.bot.core.exception.EventFiledException;
import per.chowh.bot.utils.JacksonUtil;

/**
 * @author : Chowhound
 * @since : 2025/9/3 - 14:46
 */
@Data
public class EventWrapper {
    private Event event;
    private JsonNode eventJson;
    public static final String USER_ID = "userId";
    public static final String GROUP_ID = "groupId";

    public EventWrapper(Object event) {
        this.event = (Event) event;
        eventJson = JacksonUtil.readTree(event);
    }

    public JsonNode get(String name) throws EventFiledException {
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
}

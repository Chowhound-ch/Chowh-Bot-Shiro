package per.chowh.bot.core.utils;

import com.mikuac.shiro.dto.event.Event;

/**
 * @author : Chowhound
 * @since : 2025/9/3 - 15:19
 */
public class EventUtils {

    public static boolean isEvent(Class<?> event) {
        return event.isAssignableFrom(Event.class);
    }

    public static boolean isEvent(Object event) {
        return event instanceof Event;
    }

}

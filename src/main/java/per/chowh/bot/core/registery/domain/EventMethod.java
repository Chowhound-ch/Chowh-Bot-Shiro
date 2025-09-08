package per.chowh.bot.core.registery.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import per.chowh.bot.core.registery.annotation.EventListener;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : Chowhound
 * @since : 2025/8/14 - 16:52
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventMethod {
    private Class<?> eventClass;

    private String beanName;

    private Method method;

    private Object bean;

    private List<EventParam> params;

    private EventListener eventListener;

    private Map<String, Object> extMap;

    public void putExt(String key, Object value) {
        if (extMap == null) {
            extMap = new HashMap<>();
        }
        extMap.put(key, value);
    }

    public Object getExt(String key) {
        if (extMap == null) {
            return null;
        }
        return extMap.get(key);
    }
}

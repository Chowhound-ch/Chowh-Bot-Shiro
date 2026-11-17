package per.chowh.bot.core.registery.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import per.chowh.bot.core.registery.annotation.EventListener;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    private String methodId; // 监听方法id

    private Method method;

    private Object bean;

    private List<EventParam> params;

    private EventListener eventListener;

    private Map<String, Object> extMap;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private final Map<String, EventInvokeLog> invokeLog = new ConcurrentHashMap<>();

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

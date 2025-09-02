package per.chowh.bot.core.registery.domain;

import com.mikuac.shiro.dto.event.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import per.chowh.bot.core.registery.annotation.EventListener;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author : Chowhound
 * @since : 2025/8/14 - 16:52
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventMethod {
    private Class<? extends Event> eventClass;

    private String beanName;

    private Method method;

    private Object bean;

    private List<EventParam> params;

    private EventListener eventListener;

}

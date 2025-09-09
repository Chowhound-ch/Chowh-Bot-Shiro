package per.chowh.bot.config;

import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.dto.event.message.MessageEvent;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import per.chowh.bot.common.constants.Constants;
import per.chowh.bot.core.registery.EventRegister;
import per.chowh.bot.core.registery.domain.EventMethod;
import per.chowh.bot.ext.filter.annotation.EventFilter;

import java.lang.reflect.Method;

/**
 * @author : Chowhound
 * @since : 2025/9/5 - 9:50
 */
@Component
public class ChowhBotEventRegister extends EventRegister {

    // 支持EventFilter注解
    @Override
    public void beforeRegister(EventMethod eventMethod) {
        Method method = eventMethod.getMethod();
        EventFilter annotation = AnnotationUtils.findAnnotation(method, EventFilter.class);
        if (annotation != null) {
            eventMethod.putExt(Constants.EVENT_FILTER, annotation);
        }
        // 替换AnyMessageEvent
        replaceAnyMessage(eventMethod);
    }

    private void replaceAnyMessage(EventMethod eventMethod) {
        Class<?> eventClass = eventMethod.getEventClass();
        if (AnyMessageEvent.class.isAssignableFrom(eventClass)) {
            eventMethod.setEventClass(MessageEvent.class);
        }
    }
}

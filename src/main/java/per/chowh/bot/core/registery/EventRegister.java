package per.chowh.bot.core.registery;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;
import per.chowh.bot.core.registery.domain.EventMethod;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author : Chowhound
 * @since : 2025/8/14 - 16:52
 */
@SuppressWarnings("unused")
@Configuration
@ConditionalOnMissingBean(EventRegister.class)
public class EventRegister {
    protected final List<EventMethod> eventMethods = new CopyOnWriteArrayList<>();

    public void register(EventMethod eventMethod) {
        beforeRegister(eventMethod);
        eventMethods.add(eventMethod);
        postRegister(eventMethod);
    }

    public void beforeRegister(EventMethod eventMethod) {
    }


    public void postRegister(EventMethod eventMethod) {
    }
    /**
     * 获取eventClass可以触发的EventMethod
     * @param eventClass 待处理事件类型
     * @return 待执行的EventMethod
     */
    public List<EventMethod> getEventMethods(Class<?> eventClass) {
        return eventMethods.stream()
                .filter(eventMethod -> eventClass.isAssignableFrom(eventMethod.getEventClass()))
                .toList();
    }
}

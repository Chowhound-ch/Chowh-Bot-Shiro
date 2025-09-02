package per.chowh.bot.core.registery;

import com.mikuac.shiro.dto.event.Event;
import org.springframework.context.annotation.Configuration;
import per.chowh.bot.core.registery.domain.EventMethod;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author : Chowhound
 * @since : 2025/8/14 - 16:52
 */
@Configuration
public class EventRegister {
    private final List<EventMethod> eventMethods = new CopyOnWriteArrayList<>();

    public void register(EventMethod eventMethod) {
        eventMethods.add(eventMethod);
    }

    /**
     * 获取eventClass可以触发的EventMethod
     * @param eventClass 待处理事件类型
     * @return 待执行的EventMethod
     */
    public List<EventMethod> getEventMethods(Class<? extends Event> eventClass) {
        return eventMethods.parallelStream()
                .filter(eventMethod -> eventMethod.getEventClass().isAssignableFrom(eventClass))
                .toList();
    }
}

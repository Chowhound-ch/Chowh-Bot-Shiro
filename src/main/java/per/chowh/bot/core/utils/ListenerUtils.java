package per.chowh.bot.core.utils;

import per.chowh.bot.core.registery.annotation.EventListener;
import per.chowh.bot.core.registery.domain.EventMethod;

import java.lang.reflect.Method;

/**
 * @author : Chowhound
 * @since : 2025/9/1 - 9:34
 */
public class ListenerUtils {

    public static String getListenerName(EventListener eventListener, Method method) {
        return  (method.getDeclaringClass() + "#" + method.getName()) +( "".equals(eventListener.name()) ?
                 "": ("(" + eventListener.name() + ")")) ;
    }

    public static String getListenerName(EventMethod eventMethod) {
        return  getListenerName(eventMethod.getEventListener(), eventMethod.getMethod());
    }
}

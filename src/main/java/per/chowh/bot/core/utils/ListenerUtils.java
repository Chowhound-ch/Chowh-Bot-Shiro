package per.chowh.bot.core.utils;

import per.chowh.bot.core.registery.annotation.EventListener;

import java.lang.reflect.Method;

/**
 * @author : Chowhound
 * @since : 2025/9/1 - 9:34
 */
public class ListenerUtils {

    public static String getListenerName(EventListener eventListener, Method method) {
        return  "".equals(eventListener.name()) ?
                (method.getDeclaringClass() + "#" + method.getName()) :
                eventListener.name() ;
    }
}

package per.chowh.bot.ext.filter;

import lombok.extern.slf4j.Slf4j;
import per.chowh.bot.common.constants.Constants;
import per.chowh.bot.core.registery.domain.EventMethod;
import per.chowh.bot.core.registery.interceptor.EventHandlerInterceptor;
import per.chowh.bot.ext.filter.annotation.EventFilter;

/**
 * @author : Chowhound
 * @since : 2025/9/5 - 14:35
 */
@Slf4j
public abstract class AbstractEventFilterInterceptor implements EventHandlerInterceptor {
    protected EventFilter getEventFilter(EventMethod method) {
        Object ext = method.getExt(Constants.EVENT_FILTER);
        if (ext == null) {
            return null;
        }
        return (EventFilter) ext;
    }
}

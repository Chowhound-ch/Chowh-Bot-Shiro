package per.chowh.bot.core.registery.interceptor;

import cn.hutool.core.util.StrUtil;
import com.mikuac.shiro.dto.event.message.MessageEvent;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import per.chowh.bot.core.bot.domain.ChowhBot;
import per.chowh.bot.core.registery.annotation.EventListener;
import per.chowh.bot.core.registery.domain.EventMethod;
import per.chowh.bot.core.utils.EventWrapper;

/**
 * 跟据命令判断是否触发监听
 * <p>
 *     最先执行，跟据cmd判断消息命令是否应该触发监听器。
 *     非消息事件直接放行
 * </p>
 * @author : Chowhound
 * @since : 2025/9/3 - 16:14
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class CmdEventHandlerInterceptor implements EventHandlerInterceptor{

    @Override
    public boolean preHandle(ChowhBot bot, EventMethod method, EventWrapper event) {
        EventListener listener = method.getEventListener();
        if (!MessageEvent.class.isAssignableFrom(method.getEventClass()) || StrUtil.isBlank(listener.cmd())) return true;
        return listener.matchType()
                .getMatcher()
                .apply(listener.cmd(), listener.isRaw() ? event.getRawMessage() : event.getMessage());
    }
}

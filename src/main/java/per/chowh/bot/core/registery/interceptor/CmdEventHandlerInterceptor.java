package per.chowh.bot.core.registery.interceptor;

import cn.hutool.core.util.StrUtil;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import per.chowh.bot.core.bot.domain.ChowhBot;
import per.chowh.bot.core.registery.annotation.EventListener;
import per.chowh.bot.core.registery.domain.EventMethod;
import per.chowh.bot.core.utils.EventWrapper;
import per.chowh.bot.utils.StringUtils;

/**
 * 跟据命令判断是否触发监听
 * @author : Chowhound
 * @since : 2025/9/3 - 16:14
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class CmdEventHandlerInterceptor implements EventHandlerInterceptor{

    @Override
    public boolean preHandle(ChowhBot bot, EventMethod method, EventWrapper event) {
        EventListener listener = method.getEventListener();
        if (StrUtil.isBlank(listener.cmd())) return true;
        return listener.matchType()
                .getMatcher()
                .apply(listener.cmd(), listener.isRaw() ? event.getRawMessage() : event.getMessage());
    }
}

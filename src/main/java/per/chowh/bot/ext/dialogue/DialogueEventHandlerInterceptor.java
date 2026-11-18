package per.chowh.bot.ext.dialogue;

import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import per.chowh.bot.common.constants.Constants;
import per.chowh.bot.core.bot.domain.ChowhBot;
import per.chowh.bot.core.registery.EventRegister;
import per.chowh.bot.core.registery.domain.EventInvokeLog;
import per.chowh.bot.core.registery.domain.EventMethod;
import per.chowh.bot.core.registery.interceptor.EventHandlerInterceptor;
import per.chowh.bot.core.utils.EventWrapper;
import per.chowh.bot.ext.dialogue.annotation.SessionDependOn;
import per.chowh.bot.ext.dialogue.enums.SessionType;

import java.time.Instant;

@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class DialogueEventHandlerInterceptor implements EventHandlerInterceptor {
    @Autowired
    private EventRegister register;

    @Override
    public boolean preHandle(ChowhBot bot, EventMethod method, EventWrapper event) {
        SessionDependOn sessionDependOn = (SessionDependOn) method.getExt(Constants.SESSION_DEPEND);
        if (sessionDependOn == null) {
            return true;
        }
        String key = event.getMessageSessionKey();
        if (key == null || sessionDependOn.time() <= 0) {
            return false;
        }
        Instant cutoff = Instant.now().minusNanos(sessionDependOn.unit().toNanos(sessionDependOn.time()));
        EventInvokeLog latestDependency = null;
        for (String depend : sessionDependOn.dependOn()) {
            if (StrUtil.isBlank(depend)) {
                continue;
            }
            EventMethod dependMethod = register.getEventMethods(depend);
            if (dependMethod == null) {
                return false;
            }
            EventInvokeLog invocation = dependMethod.getInvokeLog().get(key);
            if (invocation == null) {
                return false;
            }

            // 开启会话的消息本身不能算作回复。
            if (invocation.getInvokeTime().isBefore(cutoff) || invocation.getEventSequence() >= event.getSequence()) {
                return false;
            }
            if (latestDependency == null || precedes(latestDependency, invocation)) {
                latestDependency = invocation;
            }
        }

        if (latestDependency == null) {
            return false;
        }
        if (sessionDependOn.type() == SessionType.ONCE) {
            EventInvokeLog previous = method.getInvokeLog().get(key); // 获取上一次调用记录
            return previous == null || precedes(previous, latestDependency);
        }
        return true;
    }

    private boolean precedes(EventInvokeLog first, EventInvokeLog second) {
        if (first.getEventSequence() > 0 && second.getEventSequence() > 0) {
            return first.getEventSequence() < second.getEventSequence();
        }
        return first.getInvokeTime().isBefore(second.getInvokeTime());
    }
}

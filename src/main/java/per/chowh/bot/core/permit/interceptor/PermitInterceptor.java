package per.chowh.bot.core.permit.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import per.chowh.bot.core.bot.domain.ChowhBot;
import per.chowh.bot.core.registery.annotation.EventListener;
import per.chowh.bot.core.permit.domain.Group;
import per.chowh.bot.core.permit.domain.User;
import per.chowh.bot.core.permit.enums.GroupStatusEnum;
import per.chowh.bot.core.permit.enums.PermissionEnum;
import per.chowh.bot.core.permit.service.GroupService;
import per.chowh.bot.core.permit.service.UserService;
import per.chowh.bot.core.registery.domain.EventMethod;
import per.chowh.bot.core.registery.interceptor.EventHandlerInterceptor;
import per.chowh.bot.core.utils.EventWrapper;
import per.chowh.bot.core.utils.ListenerUtils;

/**
 * 权限拦截器
 * <p>
 *     优先级次于{@code  CmdEventHandlerInterceptor}，应为触发监听前最后一道拦截器
 * </p>
 * @author : Chowhound
 * @since : 2025/8/1 - 15:39
 */
@Slf4j
@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 100)
public class PermitInterceptor implements EventHandlerInterceptor {
    @Autowired
    private UserService userService;
    @Autowired
    private GroupService groupService;


    @Override
    public boolean preHandle(ChowhBot bot, EventMethod method, EventWrapper event) {
        // 消息类型先判断用户权限
        Long userId = event.getUserId();
        if (userId == null) { // 不拦截
            return true;
        }
        User user = userService.getByUserId(userId);
        PermissionEnum curPermit = user.isNull() ? PermissionEnum.NORMAL : user.getRole();
        EventListener listener = method.getEventListener();
        if (listener.permit().getValue() > curPermit.getValue()) {
            log.info("监听器：[{}]执行失败，用户[{}]权限不足", ListenerUtils.getListenerName(method), userId);
            return false;
        }
        Long groupId = event.getGroupId();
        if (groupId != null) {
            Group group = groupService.getByGroupId(groupId);
            GroupStatusEnum curStatus = group.isNull() ? GroupStatusEnum.CLOSED : group.getGroupStatus();
            if (listener.groupStatus().getValue() > curStatus.getValue()) {
                log.info("监听器：[{}]执行失败，该功能对群聊[{}]暂未开放", ListenerUtils.getListenerName(method), groupId);
                return false;
            }
        }

        return true;
    }
}

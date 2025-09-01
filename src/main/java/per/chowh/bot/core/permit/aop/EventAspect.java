package per.chowh.bot.core.permit.aop;

import cn.hutool.core.util.StrUtil;
import com.mikuac.shiro.dto.event.Event;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.MessageEvent;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import per.chowh.bot.core.registery.annotation.EventListener;
import per.chowh.bot.core.permit.domain.Group;
import per.chowh.bot.core.permit.domain.User;
import per.chowh.bot.core.permit.enums.GroupStatusEnum;
import per.chowh.bot.core.permit.enums.PermissionEnum;
import per.chowh.bot.core.permit.service.GroupService;
import per.chowh.bot.core.permit.service.UserService;

/**
 * @author : Chowhound
 * @since : 2025/8/1 - 15:39
 */
@Slf4j
@Aspect
@Component
public class EventAspect {
    @Autowired
    private UserService userService;
    @Autowired
    private GroupService groupService;


    @Pointcut("@annotation(per.chowh.bot.core.registery.annotation.EventListener)")
    public void pointCut() {
    }

    @Around(value = "pointCut() && @annotation(filter)")
    public Object around(ProceedingJoinPoint joinPoint, EventListener filter) throws Throwable {
        Object[] args = joinPoint.getArgs();
        Event event = null;
        for (Object arg : args) {
            if (arg instanceof Event) {
                event = (Event) arg;
            }
        }
        if (filter == null || event == null) {
            return joinPoint.proceed();
        }
        String listenerName = StrUtil.isBlank(filter.name()) ? joinPoint.toShortString() : filter.name();
        if (isEventEmit(event, filter, listenerName)){
            long start = System.currentTimeMillis();
            Object result = joinPoint.proceed();
            long end = System.currentTimeMillis();
            log.info("监听器：[{}]执行成功，耗时：{}ms", listenerName, end - start);
            return result;
        }
        return null;
    }

    boolean isEventEmit(Event event, EventListener filter, String methodName){
        // 判断Event类型
        if (event instanceof MessageEvent msgEvent) {
            // 消息类型先判断用户权限
            Long userId = msgEvent.getUserId();
            User user = userService.getByUserId(userId);
            PermissionEnum curPermit = user.isNull() ? PermissionEnum.COMMON: user.getRole();
            if (filter.permit().getValue() > curPermit.getValue()) {
                log.info("监听器：[{}]执行失败，用户[{}]权限不足", methodName, userId);
                return false;
            }
            // 群聊消息进一步判断群状态
            if (event instanceof GroupMessageEvent groupMsgEvent) {
                Long groupId = groupMsgEvent.getGroupId();
                Group group = groupService.getByGroupId(groupId);
                GroupStatusEnum curStatus = group.isNull() ? GroupStatusEnum.CLOSED : group.getGroupStatus();
                if (filter.groupStatus().getValue() > curStatus.getValue()) {
                    log.info("监听器：[{}]执行失败，该功能对群聊[{}]暂未开放", methodName, groupId);
                    return false;
                }
            }
        }

        return true;
    }
}

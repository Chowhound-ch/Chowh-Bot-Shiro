package per.chowh.bot.core.permit;

import com.mikuac.shiro.annotation.AnyMessageHandler;
import com.mikuac.shiro.annotation.GroupMessageHandler;
import com.mikuac.shiro.annotation.MessageHandlerFilter;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.common.utils.ShiroUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.model.ArrayMsg;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import per.chowh.bot.core.registery.annotation.EventListener;
import per.chowh.bot.core.permit.domain.User;
import per.chowh.bot.core.permit.enums.GroupStatusEnum;
import per.chowh.bot.core.permit.enums.PermissionEnum;
import per.chowh.bot.core.permit.service.GroupService;
import per.chowh.bot.core.permit.service.UserService;
import per.chowh.bot.core.utils.BotUtils;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;

/**
 * @author : Chowhound
 * @since : 2025/8/1 - 10:01
 */
@Slf4j
@Shiro
@Component
public class PermissionPlugins {
    @Autowired
    private UserService userService;
    @Autowired
    private GroupService groupService;
    @Value("${per.bot.owner}")
    private Long owner;

    @PostConstruct
    public void init(){
        User ownerUser = userService.getByUserId(owner);
        if (ownerUser == null || !Objects.equals(ownerUser.getRole(), PermissionEnum.OWNER)) {
            userService.updateOwner(owner);
            log.info("设置Owner为: {}", owner);
        }
    }

    @AnyMessageHandler
    @MessageHandlerFilter(cmd = "/?(设置|permit)\\s*(?<permit>[A-Za-z]+)\\s*(?<user>\\d{5,12})?.*")
    @EventListener(permit = PermissionEnum.OWNER, name = "用户权限设置")
    public void updatePermission(Bot bot, AnyMessageEvent event, Matcher matcher) {
        List<ArrayMsg> arrayMsg = event.getArrayMsg();
        List<Long> list = ShiroUtils.getAtList(arrayMsg);
        Long userId = list.isEmpty() ? null : list.get(0);

        if (userId == null) {
            String user = matcher.group("user");
            if (user != null) {
                userId = Long.parseLong(user);
            } else {
                // 没有userId，退出
                return;
            }
        }
        String permit = matcher.group("permit");
        try {
            PermissionEnum permission = PermissionEnum.valueOf(permit.toUpperCase());
            // 开始更新
            userService.updateRole(userId, permission);
            bot.sendMsg(event, "成功将" + BotUtils.getUserCard(userId, event.getGroupId())
                    + "[" + userId +  "]的权限设置为" + permit.toUpperCase(), true);
        } catch (IllegalArgumentException e) {
            log.warn("错误的权限标志：{}", permit.toUpperCase());
        }
    }

    @GroupMessageHandler
    @MessageHandlerFilter(cmd = "/?(设置群|state)\\s*(?<state>[A-Za-z]+)\\s*")
    @EventListener(permit = PermissionEnum.ADMIN, groupStatus = GroupStatusEnum.CLOSED, name = "群聊状态设置")
    public void updateGroupState(Bot bot, GroupMessageEvent event, Matcher matcher) {
        Long groupId = event.getGroupId();

        String state = matcher.group("state");
        try {
            GroupStatusEnum statusEnum = GroupStatusEnum.valueOf(state.toUpperCase());
            // 开始更新
            groupService.updateState(groupId, statusEnum);
            bot.sendGroupMsg(event.getGroupId(), "已将群状态设置为" + state.toUpperCase(), true);
        } catch (IllegalArgumentException e) {
            log.warn("错误的群状态标志：{}", state.toUpperCase());
        }
    }
}

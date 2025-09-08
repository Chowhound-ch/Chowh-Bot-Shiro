package per.chowh.bot.core.permit;

import com.mikuac.shiro.annotation.AnyMessageHandler;
import com.mikuac.shiro.annotation.GroupMessageHandler;
import com.mikuac.shiro.annotation.MessageHandlerFilter;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.common.utils.ShiroUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.MessageEvent;
import com.mikuac.shiro.model.ArrayMsg;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import per.chowh.bot.core.bot.domain.ChowhBot;
import per.chowh.bot.core.registery.annotation.EventListener;
import per.chowh.bot.core.permit.domain.User;
import per.chowh.bot.core.permit.enums.GroupStatusEnum;
import per.chowh.bot.core.permit.enums.PermissionEnum;
import per.chowh.bot.core.permit.service.GroupService;
import per.chowh.bot.core.permit.service.UserService;
import per.chowh.bot.core.utils.BotUtils;
import per.chowh.bot.core.utils.EventWrapper;
import per.chowh.bot.core.utils.MsgUtils;

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

    @EventListener(permit = PermissionEnum.OWNER,
            cmd = "/?(设置|permit)\\s*(?<permit>[A-Za-z]+)\\s*(?<user>\\d{5,12})?.*",
            name = "用户权限设置")
    public void updatePermission(ChowhBot bot, MessageEvent event, String permit, Long user) {
        List<ArrayMsg> arrayMsg = event.getArrayMsg();
        List<Long> list = ShiroUtils.getAtList(arrayMsg);
        Long userId = list.isEmpty() ? user : list.get(0);
        try {
            PermissionEnum permission = PermissionEnum.valueOf(permit.toUpperCase());
            // 开始更新
            userService.updateRole(userId, permission);
            bot.sendMsg(event, "成功将" + BotUtils.getUserCard(event)
                    + "[" + userId +  "]的权限设置为" + permit.toUpperCase(), true);
        } catch (IllegalArgumentException e) {
            log.warn("错误的权限标志：{}", permit.toUpperCase());
        }
    }

    @EventListener(permit = PermissionEnum.ADMIN, groupStatus = GroupStatusEnum.CLOSED,
            cmd = "/?(设置群|state)\\s*(?<state>[A-Za-z]+)\\s*",
            name = "群聊状态设置")
    public void updateGroupState(ChowhBot bot, GroupMessageEvent event, String state) {
        Long groupId = event.getGroupId();
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

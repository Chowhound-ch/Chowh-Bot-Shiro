package per.chowh.bot.plugins.core;

import com.mikuac.shiro.annotation.AnyMessageHandler;
import com.mikuac.shiro.annotation.MessageHandlerFilter;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.common.utils.ShiroUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.model.ArrayMsg;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import per.chowh.bot.plugins.core.domain.User;
import per.chowh.bot.plugins.core.enums.PermissionEnum;
import per.chowh.bot.plugins.core.service.UserService;

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
    @MessageHandlerFilter(cmd = "/?permit\\s*(?<permit>[A-Za-z]+)\\s*(?<user>\\d{5,12})?.*")
    public void updatePermission(Bot bot, AnyMessageEvent event, Matcher matcher) {
        List<ArrayMsg> arrayMsg = event.getArrayMsg();
        List<Long> list = ShiroUtils.getAtList(arrayMsg);

        Long userId = null;
        if (!list.isEmpty()) {
            userId = list.get(0);
        }

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
            bot.sendMsg(event, "成功将[" + userId +  "]的权限设置为" + permit.toUpperCase(), true);
        } catch (IllegalArgumentException e) {
            log.warn("错误的权限标志：{}", permit.toUpperCase());
        }

    }


}

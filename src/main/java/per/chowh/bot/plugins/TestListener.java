package per.chowh.bot.plugins;

import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import org.springframework.stereotype.Component;
import per.chowh.bot.core.bot.domain.ChowhBot;
import per.chowh.bot.core.permit.enums.GroupStatusEnum;
import per.chowh.bot.core.permit.enums.PermissionEnum;
import per.chowh.bot.core.registery.annotation.EventListener;

/**
 * @author : Chowhound
 * @since : 2025/9/1 - 11:40
 */
@Component
public class TestListener {

    @EventListener(permit = PermissionEnum.OWNER, groupStatus = GroupStatusEnum.FULL)
    public void test(ChowhBot bot, AnyMessageEvent anyMessageEvent) {

    }
}

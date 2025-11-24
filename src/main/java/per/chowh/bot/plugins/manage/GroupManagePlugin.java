package per.chowh.bot.plugins.manage;

import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import org.springframework.stereotype.Component;
import per.chowh.bot.core.bot.domain.ChowhBot;
import per.chowh.bot.core.permit.domain.Group;
import per.chowh.bot.core.registery.annotation.EventListener;

/**
 * @author : Chowhound
 * @since : 2025/11/24 - 14:05
 */
@Component
public class GroupManagePlugin {

    @EventListener(cmd = "我要头衔(?<title>.+)", name = "群头衔")
    public void getGroupTitle(ChowhBot bot, GroupMessageEvent event, String title) {
        bot.setGroupSpecialTitle(event.getGroupId(), event.getUserId(), title, 0);
    }

}

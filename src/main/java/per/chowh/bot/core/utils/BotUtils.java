package per.chowh.bot.core.utils;

import cn.hutool.core.util.StrUtil;
import com.mikuac.shiro.dto.action.common.ActionData;
import com.mikuac.shiro.dto.action.common.ActionList;
import com.mikuac.shiro.dto.action.response.FriendInfoResp;
import com.mikuac.shiro.dto.action.response.GroupMemberInfoResp;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.MessageEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import per.chowh.bot.core.bot.domain.ChowhBot;
import per.chowh.bot.core.bot.domain.ChowhBotContainer;

import java.util.Objects;

/**
 * @author : Chowhound
 * @since : 2025/8/6 - 10:36
 */
@Component
public class BotUtils implements ApplicationRunner {
    private static final BotWrapper botWrapper = new BotWrapper();
    @Autowired
    private ChowhBotContainer botContainer;

    public static ChowhBot getBot(){
        return botWrapper.getBot();
    }

    public static String getUserCard(AnyMessageEvent event) {
        GroupMessageEvent.GroupSender sender = event.getSender();
        return StrUtil.isBlank(sender.getCard()) ? sender.getNickname() : sender.getCard();
    }

    public static String getUserCard(Long userId) {
        return getUserCard(userId, null);
    }

    public static String getUserCard(Long userId, Long groupId) {
        ChowhBot bot = getBot();
        if (bot == null) return "";
        if (groupId != null) {
            ActionData<GroupMemberInfoResp> memberInfo = bot.getGroupMemberInfo(groupId, userId, true);
            GroupMemberInfoResp memberInfoData = memberInfo.getData();
            return StrUtil.isBlank(memberInfoData.getCard()) ? memberInfoData.getNickname() : memberInfoData.getCard();
        }
        FriendInfoResp resp = getFriend(userId);
        if (resp == null) return "";
        return resp.getRemark() == null ? resp.getNickname() : resp.getRemark();
    }

    public static String getUserCard(MessageEvent event) {
        Long userId = event.getUserId();
        Long groupId = MsgUtils.getGroupId(event);
        return getUserCard(userId, groupId);
    }

    public static FriendInfoResp getFriend(Long userId) {
        ChowhBot bot = BotUtils.getBot();
        if (bot == null) return null;
        ActionList<FriendInfoResp> friendList = bot.getFriendList();
        for (FriendInfoResp resp : friendList.getData()) {
            if (Objects.equals(resp.getUserId(), userId)) {
                return resp;
            }
        }
        return null;
    }


    @Override
    public void run(ApplicationArguments args) {
        botContainer.forEach((k, v) ->{
            botWrapper.setBot(v);
        });
    }
}

package per.chowh.bot.ext.distribute;

import com.mikuac.shiro.model.ArrayMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import per.chowh.bot.core.bot.domain.ChowhBot;
import per.chowh.bot.core.utils.BotUtils;
import per.chowh.bot.ext.distribute.domain.EventPermit;
import per.chowh.bot.ext.distribute.service.EventPermitService;

import java.util.List;

/**
 * 处理消息分发
 * @author : Chowhound
 * @since : 2025/12/1 - 9:31
 */
@Service
public class MsgDistributeService {
    @Autowired
    private EventPermitService eventPermitService;

    public void distributeMsg(String msg, String eventFlag) {
        ChowhBot bot = BotUtils.getBot();
        if (bot == null) return;
        List<EventPermit> eventPermits = eventPermitService.getByEventFlag(eventFlag);
        for (EventPermit permit : eventPermits) {
            sendMsg(bot, permit, msg);
        }
    }
    public void distributeMsg(List<ArrayMsg> msg, String eventFlag) {
        ChowhBot bot = BotUtils.getBot();
        if (bot == null) return;
        List<EventPermit> eventPermits = eventPermitService.getByEventFlag(eventFlag);
        for (EventPermit permit : eventPermits) {
            sendMsg(bot, permit, msg);
        }
    }

    public void distributeForwardMsg(List<String> msg, String eventFlag, String nickName) {
        ChowhBot bot = BotUtils.getBot();
        if (bot == null) return;
        List<EventPermit> eventPermits = eventPermitService.getByEventFlag(eventFlag);
        for (EventPermit permit : eventPermits) {
            sendMsgForward(bot, permit, msg, nickName);
        }
    }

    private void sendMsg(ChowhBot bot, EventPermit permit, String msg) {
        if (permit.getPermitType() == 1) { // 群聊
            bot.sendGroupMsg(permit.getTarget(), msg, false);
        } else {
            bot.sendPrivateMsg(permit.getTarget(), msg, false);
        }
    }

    private void sendMsg(ChowhBot bot, EventPermit permit, List<ArrayMsg> msg) {
        if (permit.getPermitType() == 1) { // 群聊
            bot.sendGroupMsg(permit.getTarget(), msg, false);
        } else {
            bot.sendPrivateMsg(permit.getTarget(), msg, false);
        }
    }
    private void sendMsgForward(ChowhBot bot, EventPermit permit, List<String> msg, String nickName) {
        if (permit.getPermitType() == 1) { // 群聊
            bot.sendGroupForwardMsg(permit.getTarget(), msg, nickName);
        } else {
            bot.sendPrivateForwardMsg(permit.getTarget(), msg, nickName);
        }
    }
}

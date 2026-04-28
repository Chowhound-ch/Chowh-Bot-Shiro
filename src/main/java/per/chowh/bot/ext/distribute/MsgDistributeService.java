package per.chowh.bot.ext.distribute;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.URLUtil;
import com.mikuac.shiro.model.ArrayMsg;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;
import per.chowh.bot.core.bot.domain.ChowhBot;
import per.chowh.bot.core.utils.BotUtils;
import per.chowh.bot.core.utils.MsgBuilder;
import per.chowh.bot.ext.distribute.domain.EventPermit;
import per.chowh.bot.ext.distribute.service.EventPermitService;
import per.chowh.bot.ext.template.utils.MsgParamsUtils;
import per.chowh.bot.utils.FreeMarkerUtils;
import per.chowh.bot.utils.PlaywrightScreenshot;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 处理消息分发
 * @author : Chowhound
 * @since : 2025/12/1 - 9:31
 */
@SuppressWarnings("unused")
@Service
public class MsgDistributeService implements ApplicationRunner {
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

    @Override
    public void run(ApplicationArguments args) {

//        Map<String, Object> map = new HashMap<>();
//        map.put("testId", "测试值");
////        byte[] bytes = FreeMarkerUtils.turnImage("event_flag_list.ftl", map, 500, 500);
//        String uuid = MsgParamsUtils.putParams(map);
//        Map<String, String> query = Map.of("viewName", "msg/event_flag_list", "uuid", uuid);
//        String urlQuery = URLUtil.buildQuery(query, StandardCharsets.UTF_8);
//        byte[] bytes = PlaywrightScreenshot.takeScreenshot("http://localhost:8002/template?" + urlQuery);
//        MsgBuilder builder = new MsgBuilder();
//
//        BotUtils.getBot().sendPrivateMsg(825352674L, builder.img(bytes).build(), false);
    }
}

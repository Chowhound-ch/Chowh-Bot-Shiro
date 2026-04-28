package per.chowh.bot.ext.distribute;

import cn.hutool.core.util.StrUtil;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.MessageEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import per.chowh.bot.core.bot.domain.ChowhBot;
import per.chowh.bot.core.permit.enums.PermissionEnum;
import per.chowh.bot.core.registery.annotation.EventListener;
import per.chowh.bot.core.utils.MsgBuilder;
import per.chowh.bot.ext.dict.domain.DataDict;
import per.chowh.bot.ext.dict.service.DataDictService;
import per.chowh.bot.ext.distribute.domain.EventPermit;
import per.chowh.bot.ext.distribute.service.EventPermitService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : Chowhound
 * @since : 2025/12/1 - 15:13
 */
@Component
public class MsgDistributePlugin {
    @Autowired
    private MsgDistributeService msgDistributeService;
    @Autowired
    private EventPermitService eventPermitService;
    @Autowired
    private DataDictService dictService;
    public static final String EVENT_FLAG = "EVENT_FLAG";

    @EventListener(cmd = "订阅全部", permit = PermissionEnum.ADMIN, name = "订阅全部")
    public void startAllPush(ChowhBot bot, MessageEvent event) {
        List<DataDict> eventFlag = dictService.listByDictCode(EVENT_FLAG);
        List<EventPermit> eventPermits = new ArrayList<>();
        for (DataDict dataDict : eventFlag) {
            eventPermits.add(getEventFlag(event, dataDict));
        }
        eventPermitService.saveEventPermitList(eventPermits);
        if (!eventPermits.isEmpty()) {
            bot.sendMsg(event, getPushListMsg(eventPermits), false);
        } else {
            bot.sendMsg(event, "没有其他可订阅消息", false);
        }
    }

    @EventListener(cmd = "取消订阅全部", permit = PermissionEnum.ADMIN, name = "取消订阅全部")
    public void stopAllPush(ChowhBot bot, MessageEvent event) {
        List<EventPermit> eventPermits;
        if (event instanceof GroupMessageEvent groupMessageEvent) {
            eventPermits = eventPermitService.removeByTargetAndType(groupMessageEvent.getGroupId(), 1);
        } else {
            eventPermits = eventPermitService.removeByTargetAndType(event.getUserId(), 0);
        }
        bot.sendMsg(event, getPushListMsg(eventPermits), false);
    }
    private EventPermit getEventFlag(MessageEvent event, DataDict dataDict) {
        EventPermit eventPermit = new EventPermit();
        eventPermit.setEventFlag(dataDict.getDictValue());
        eventPermit.setPermitType(0);
        eventPermit.setTarget(event.getUserId());
        if (event instanceof GroupMessageEvent groupMessageEvent) {
            eventPermit.setPermitType(1);
            eventPermit.setTarget(groupMessageEvent.getGroupId());
        }
        return eventPermit;
    }



    @EventListener(cmd = "查看全部订阅", permit = PermissionEnum.ADMIN, name = "查看全部订阅")
    public void viewAllPush(ChowhBot bot, MessageEvent event) {
        List<DataDict> eventFlag = dictService.listByDictCode("EVENT_FLAG");
        MsgBuilder builder = new MsgBuilder();
        eventFlag.forEach(dataDict -> builder.text(dataDict.getDictName()).text("\n"));
        bot.sendMsg(event, builder.build(), false);
    }

    @EventListener(cmd = "查看订阅", permit = PermissionEnum.ADMIN, name = "查看订阅")
    public void viewPush(ChowhBot bot, MessageEvent event) {
        List<EventPermit> eventPermits;
        if (event instanceof GroupMessageEvent groupMessageEvent) {
            eventPermits = eventPermitService.listByTargetAndType(groupMessageEvent.getGroupId(), 1);
        } else {
            eventPermits = eventPermitService.listByTargetAndType(event.getUserId(), 0);
        }
        String msg = getPushListMsg(eventPermits);
        if (StrUtil.isBlank(msg)) {
            bot.sendMsg(event, "暂未订阅任何消息", true);
        } else {
            bot.sendMsg(event, msg, false);
        }
    }

    private String getPushListMsg(List<EventPermit> eventPermits) {
        MsgBuilder builder = new MsgBuilder();
        for (EventPermit eventPermit : eventPermits) {
            DataDict dictName = dictService.getDictName(EVENT_FLAG, eventPermit.getEventFlag());
            builder.text(dictName.getDictName()).text("\n");
        }
        return builder.build();
    }
}

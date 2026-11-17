package per.chowh.bot.ext.distribute;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import per.chowh.bot.core.bot.domain.ChowhBot;
import per.chowh.bot.core.event.message.GroupMessageEvent;
import per.chowh.bot.core.event.message.MessageEvent;
import per.chowh.bot.core.permit.enums.PermissionEnum;
import per.chowh.bot.core.registery.annotation.EventListener;
import per.chowh.bot.core.utils.MsgTable;
import per.chowh.bot.ext.dict.domain.DataDictCatDetail;
import per.chowh.bot.ext.dict.service.DataDictService;
import per.chowh.bot.ext.distribute.domain.EventPermit;
import per.chowh.bot.ext.distribute.service.EventPermitService;

import java.util.List;
import java.util.Objects;

/**
 * @author : Chowhound
 * @since : 2025/12/1 - 15:13
 */
@Component
public class MsgDistributePlugin {
    public static final String EVENT_FLAG = "EVENT_FLAG";
    private static final List<String> COLUMNS = List.of("订阅编码", "订阅名称");

    @Autowired
    private EventPermitService eventPermitService;
    @Autowired
    private DataDictService dictService;

    @EventListener(cmd = "订阅\\s*(?<dictCode>(?!全部$)\\S+)", permit = PermissionEnum.ADMIN, name = "订阅指定编码")
    public void startPush(ChowhBot bot, MessageEvent event, String dictCode) {
        DataDictCatDetail detail = dictService.listDetails(EVENT_FLAG).stream()
                .filter(item -> item.getDictCode().equals(dictCode))
                .findFirst()
                .orElse(null);
        if (detail == null) {
            MsgTable.send(bot, event, "订阅失败", COLUMNS, List.of(List.of(dictCode, "—")),
                    "该编码不在可订阅列表中，可发送「查看全部订阅」。");
            return;
        }
        eventPermitService.saveEventPermitList(List.of(newPermit(event, detail.getDictCode())));
        sendDetails(bot, event, "订阅成功", List.of(detail));
    }

    @EventListener(cmd = "订阅全部", permit = PermissionEnum.ADMIN, name = "订阅全部")
    public void startAllPush(ChowhBot bot, MessageEvent event) {
        List<DataDictCatDetail> details = dictService.listDetails(EVENT_FLAG);
        List<EventPermit> eventPermits = details.stream()
                .map(detail -> newPermit(event, detail.getDictCode()))
                .toList();
        eventPermitService.saveEventPermitList(eventPermits);
        sendDetails(bot, event, details.isEmpty() ? "暂无可订阅项目" : "已订阅全部", details);
    }

    @EventListener(cmd = "取消订阅全部", permit = PermissionEnum.ADMIN, name = "取消订阅全部")
    public void stopAllPush(ChowhBot bot, MessageEvent event) {
        List<EventPermit> eventPermits;
        if (event instanceof GroupMessageEvent groupMessageEvent) {
            eventPermits = eventPermitService.removeByTargetAndType(groupMessageEvent.getGroupId(), 1);
        } else {
            eventPermits = eventPermitService.removeByTargetAndType(event.getUserId(), 0);
        }
        sendPermits(bot, event, "已取消的订阅", eventPermits);
    }

    private EventPermit newPermit(MessageEvent event, String dictCode) {
        EventPermit eventPermit = new EventPermit();
        eventPermit.setEventFlag(dictCode);
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
        sendDetails(bot, event, "可订阅项目", dictService.listDetails(EVENT_FLAG));
    }

    @EventListener(cmd = "查看订阅", permit = PermissionEnum.ADMIN, name = "查看订阅")
    public void viewPush(ChowhBot bot, MessageEvent event) {
        List<EventPermit> eventPermits;
        if (event instanceof GroupMessageEvent groupMessageEvent) {
            eventPermits = eventPermitService.listByTargetAndType(groupMessageEvent.getGroupId(), 1);
        } else {
            eventPermits = eventPermitService.listByTargetAndType(event.getUserId(), 0);
        }
        sendPermits(bot, event, "当前订阅", eventPermits);
    }

    private void sendDetails(ChowhBot bot, MessageEvent event, String title, List<DataDictCatDetail> details) {
        List<List<String>> rows = details.stream()
                .map(detail -> List.of(detail.getDictCode(), detail.getDictName()))
                .toList();
        MsgTable.send(bot, event, title, COLUMNS, rows, "共 " + rows.size() + " 项");
    }

    private void sendPermits(ChowhBot bot, MessageEvent event, String title, List<EventPermit> eventPermits) {
        List<List<String>> rows = eventPermits.stream()
                .map(permit -> {
                    String code = permit.getEventFlag();
                    String name = code == null ? null : dictService.getDetailName(EVENT_FLAG, code);
                    return List.of(Objects.toString(code, "—"), Objects.toString(name, "—"));
                })
                .distinct()
                .toList();
        MsgTable.send(bot, event, title, COLUMNS, rows, "共 " + rows.size() + " 项");
    }
}

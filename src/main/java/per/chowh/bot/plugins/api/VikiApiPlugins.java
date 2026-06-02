package per.chowh.bot.plugins.api;

import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import per.chowh.bot.core.bot.domain.ChowhBot;
import per.chowh.bot.core.bot.domain.ChowhBotContainer;
import per.chowh.bot.core.registery.annotation.EventListener;
import per.chowh.bot.core.utils.MsgBuilder;
import per.chowh.bot.ext.distribute.MsgDistributeService;
import per.chowh.bot.plugins.api.entity.EpicFreeGameInfo;
import per.chowh.bot.utils.DateUtils;
import per.chowh.bot.utils.HttpProxyUtils;
import per.chowh.bot.utils.HttpUtils;
import per.chowh.bot.utils.JacksonUtil;
import tools.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author : Chowhound
 * @since : 2025/11/24 - 15:20
 */
@Component
public class VikiApiPlugins {

    public static final String BASE_API = "http://chowh.top:4399/v2";
    public static final String EPIC_FREE = "EPIC_FREE";
    public static final String NEWS = "60S_NEWS";

    @Autowired
    private ChowhBotContainer chowhBotContainer;
    @Autowired
    private MsgDistributeService distributeService;

    @Scheduled(cron ="0 0 9 * * ?")
    public void getGroupTitle() throws IOException {
        distributeService.distributeMsg(getTheNewsMsg(), NEWS);
    }


    @EventListener(cmd = "60s", name = "60s读懂世界")
    public void getGroupTitle(ChowhBot bot, GroupMessageEvent event) throws IOException {
        bot.sendGroupMsg(event.getGroupId(), getTheNewsMsg(), false);
    }

    private String getTheNewsMsg() throws IOException {
        //        byte[] bytes = HttpUtils.doGetBytes(BASE_API + "/60s?encoding=image-proxy");
        JsonNode node = HttpUtils.doGetJson(BASE_API + "/60s");
        byte[] bytes = null;
        if (node != null && !node.isEmpty()) {
            int code = node.get("code").intValue();
            if (code == 200) {
                String url = node.get("data").get("image").asText();
                bytes = HttpProxyUtils.doGetBytes(url);
            }
        }
        if (bytes == null || bytes.length == 0) {
            return "获取失败";
        }
        return new MsgBuilder()
                .img(bytes)
                .build();
    }

    @Scheduled(cron ="0 0 10 * * 5")
    public void getEpicWeekFree() throws IOException {
        distributeService.distributeForwardMsg(getEpicWeekFreeMsg(), EPIC_FREE, "Epic周免");
    }

    @EventListener(cmd = "epic周免", name = "epic周免")
    public void getEpicWeekFree(ChowhBot bot, GroupMessageEvent event) throws IOException {
        List<String> epicWeekFreeMsg = getEpicWeekFreeMsg();
        if (epicWeekFreeMsg.isEmpty()) {
            bot.sendGroupMsg(event.getGroupId(), "查询失败", false);
        }
        bot.sendForwardMsg(event, epicWeekFreeMsg);
    }


    private List<String> getEpicWeekFreeMsg() throws IOException {
        JsonNode node = HttpUtils.doGetJson(BASE_API + "/epic");
        List<String> msgList = new ArrayList<>();
        if (node != null && !node.isEmpty()) {
            int code = node.get("code").intValue();
            if (code == 200) {
                List<EpicFreeGameInfo> epicFrees = JacksonUtil.readListValue(node.get("data"), EpicFreeGameInfo.class);
                if (epicFrees != null && !epicFrees.isEmpty()) {
                    int curWeek = 0, lastWeek = 0;
                    long time = new Date().getTime();
                    for (EpicFreeGameInfo epicFree : epicFrees) {
                        MsgBuilder builder = new MsgBuilder();
                        builder.img(HttpUtils.doGetBytes(epicFree.getCover()))
                                .text("◉ 名称：").text(epicFree.getTitle()).text("\n")
                                .text("◉ 原价：").text(epicFree.getOriginalPriceDesc()).text("\n")
                                .text("◉ 限免开始：").text(DateUtils.formatCN(epicFree.getFreeStart())).text("\n")
                                .text("◉ 限免结束：").text(DateUtils.formatCN(epicFree.getFreeEnd())).text("\n")
                                .text("◉ 发行：").text(epicFree.getSeller()).text("\n")
                                .text("◉ 简介：").text(epicFree.getDescription()).text("\n")
                                .text("◉ 链接：").text(epicFree.getLink());
                        msgList.add(builder.build());
                        // 统计总共限免游戏数量
                        if (time > epicFree.getFreeStart().getTime() && time < epicFree.getFreeEnd().getTime()) {
                            curWeek++;
                        } else {
                            lastWeek++;
                        }
                    }
                    msgList.add(0, "本周Epic限时免费" + curWeek + "款游戏，此外还有" + lastWeek +"款游戏即将限免");
                    return msgList;
                }
            }
        }
        return msgList;
    }
}

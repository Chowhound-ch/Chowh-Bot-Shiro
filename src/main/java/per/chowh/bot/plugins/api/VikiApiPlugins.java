package per.chowh.bot.plugins.api;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import per.chowh.bot.common.constants.Constants;
import per.chowh.bot.core.event.message.GroupMessageEvent;
import per.chowh.bot.utils.*;
import tools.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import per.chowh.bot.core.bot.domain.ChowhBot;
import per.chowh.bot.core.registery.annotation.EventListener;
import per.chowh.bot.core.utils.MsgBuilder;
import per.chowh.bot.ext.distribute.MsgDistributeService;
import per.chowh.bot.plugins.api.entity.EpicFreeGameInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @author : Chowhound
 * @since : 2025/11/24 - 15:20
 */
@Slf4j
@Component
public class VikiApiPlugins {
    public static final String EPIC_FREE = "epic";
    public static final String NEWS = "60s";
    public static final String MOYU = "moyu";
    private static final String MOYU_CALENDAR_API = "https://api.moyujidi.com/api/moyurili";
    private static final int MAX_COVER_BYTES = 2 * 1024 * 1024;

    @Autowired
    private MsgDistributeService distributeService;

    @Scheduled(cron ="0 30 8 * * ?")
    public void getGroupTitle() throws IOException {
        distributeService.distributeMsg(getTheNewsMsg(), NEWS);
    }


    @EventListener(cmd = NEWS, name = "60s读懂世界")
    public void getGroupTitle(ChowhBot bot, GroupMessageEvent event) throws IOException {
        bot.sendGroupMsg(event.getGroupId(), getTheNewsMsg(), false);
    }

    private String getTheNewsMsg() throws IOException {
        //        byte[] bytes = HttpUtils.doGetBytes(BASE_API + "/60s?encoding=image-proxy");
        JsonNode node = HttpProxyUtils.doGetJson(Constants.getVikiUrl() + "/v2/60s");
        byte[] bytes = null;
        if (node != null && !node.isEmpty()) {
            int code = node.get("code").intValue();
            if (code == 200) {
                String url = node.get("data").get("image").asString();
                try {
                    bytes = HttpProxyUtils.doGetBytes(url);
                } catch (Exception e) {
                    log.error("60s读懂世界图片获取失败，使用文字版本", e);
                    return node.get("data").asString();
                }
            }
        }
        if (bytes == null || bytes.length == 0) {
            return "获取失败";
        }
        return new MsgBuilder()
                .img(bytes)
                .build();
    }

    @Scheduled(cron = "0 30 8 * * ?")
    public void pushMoyuCalendar() throws IOException {
        String message = getMoyuCalendarMsg();
        if (!message.isEmpty()) {
            distributeService.distributeMsg(message, MOYU);
        }
    }

    @EventListener(cmd = "摸鱼", name = "摸鱼人日历")
    public void getMoyuCalendar(ChowhBot bot, GroupMessageEvent event) throws IOException {
        String message = getMoyuCalendarMsg();
        bot.sendGroupMsg(event.getGroupId(), message.isEmpty() ? "摸鱼人日历获取失败" : message, false);
    }

    private String getMoyuCalendarMsg() throws IOException {
        JsonNode data = HttpUtils.doGetJson(MOYU_CALENDAR_API);
        if (data == null || data.path("code").asInt(-1) != 0) {
            return "";
        }
        String imageUrl = data.get("data").get("url").asString("");
        if (StrUtil.isBlank(imageUrl)) {
            return "";
        }
        byte[] bytes = HttpUtils.doGetBytes(imageUrl);

        return bytes.length == 0 ? "" : MsgBuilder.builder().img(bytes).build();
    }


    // epic周免
    @Scheduled(cron ="0 0 10 * * 5")
    public void getEpicWeekFree() throws IOException {
        String message = getEpicWeekFreeMsg();
        if (!message.isEmpty()) {
            distributeService.distributeMsg(message, EPIC_FREE);
        }
    }

    @EventListener(cmd = EPIC_FREE, name = "epic周免")
    public void getEpicWeekFree(ChowhBot bot, GroupMessageEvent event) throws IOException {
        String message = getEpicWeekFreeMsg();
        bot.sendGroupMsg(event.getGroupId(), message.isEmpty() ? "查询失败" : message, false);
    }


    private String getEpicWeekFreeMsg() throws IOException {
        JsonNode node = HttpUtils.doGetJson(Constants.getVikiUrl() + "/v2/epic");
        if (node == null || node.path("code").asInt(0) != 200 || !node.path("data").isArray()) {
            return "";
        }
        List<EpicFreeGameInfo> games = JacksonUtil.readListValue(node.get("data"), EpicFreeGameInfo.class);
        if (games == null) {
            return "";
        }

        Date now = new Date();
        List<Map<String, String>> current = new ArrayList<>();
        List<Map<String, String>> upcoming = new ArrayList<>();
        for (EpicFreeGameInfo game : games) {
            if (game == null || (game.getFreeEnd() != null && !now.before(game.getFreeEnd()))) {
                continue;
            }
            boolean isUpcoming = game.getFreeStart() != null
                    ? now.before(game.getFreeStart()) : !Boolean.TRUE.equals(game.getIsFreeNow());
            Map<String, String> card = Map.of(
                    "title", game.getTitle() == null ? "未命名游戏" : game.getTitle(),
                    "cover", loadEpicCover(game.getCover()),
                    "price", game.getOriginalPriceDesc() == null ? "" : game.getOriginalPriceDesc(),
                    "seller", game.getSeller() == null ? "" : game.getSeller(),
                    "description", game.getDescription() == null ? "" : game.getDescription(),
                    "start", game.getFreeStart() == null ? "待公布" : DateUtils.formatCN(game.getFreeStart()),
                    "end", game.getFreeEnd() == null ? "待公布" : DateUtils.formatCN(game.getFreeEnd()),
                    "link", game.getLink() == null ? "" : game.getLink());
            (isUpcoming ? upcoming : current).add(card);
        }

        String html = FreeMarkerUtils.getTemplate("msg/epic_week_free.ftl",
                Map.of("current", current, "upcoming", upcoming));
        if (html.isBlank()) {
            throw new IllegalStateException("Epic 周免模板渲染失败");
        }
        byte[] image = PlaywrightScreenshot.takeHtmlScreenshot(html, ".epic-report");
        MsgBuilder builder = new MsgBuilder();
        builder.img(image);
        Stream.concat(current.stream(), upcoming.stream())
                .filter(card -> !card.get("link").isBlank())
                .forEach(card -> builder.text("\n" + card.get("title") + "\n" + card.get("link")));
        return builder.build();
    }

    private String loadEpicCover(String url) {
        if (url == null || url.isBlank()) {
            return "";
        }
        try (Response response = HttpUtils.doGet(null, url, null)) {
            var body = response.body();
            var type = body.contentType();
            if (!response.isSuccessful() || type == null || !"image".equals(type.type())) {
                return "";
            }
            byte[] bytes = body.byteStream().readNBytes(MAX_COVER_BYTES + 1);
            if (bytes.length > 0 && bytes.length <= MAX_COVER_BYTES) {
                return "data:image/" + type.subtype() + ";base64," + Base64.getEncoder().encodeToString(bytes);
            }
        } catch (IOException | IllegalArgumentException e) {
            log.warn("Epic 游戏封面获取失败：{}", e.getMessage());
        }
        return "";
    }
}

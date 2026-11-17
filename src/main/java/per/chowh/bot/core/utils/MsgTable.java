package per.chowh.bot.core.utils;

import per.chowh.bot.core.bot.domain.ChowhBot;
import per.chowh.bot.core.event.message.MessageEvent;
import per.chowh.bot.utils.FreeMarkerUtils;
import per.chowh.bot.utils.PlaywrightScreenshot;

import java.util.List;
import java.util.Map;

public class MsgTable {
    public static void send(ChowhBot bot, MessageEvent event, String title,
                            List<String> columns, List<List<String>> rows, String summary) {
        String html = FreeMarkerUtils.getTemplate("msg/msg_table.ftl", Map.of(
                "title", title, "columns", columns, "rows", rows, "summary", summary));
        if (html.isBlank()) {
            throw new IllegalStateException("消息表格模板渲染失败");
        }
        byte[] image = PlaywrightScreenshot.takeHtmlScreenshot(html, ".dict-card");
        bot.sendMsg(event, new MsgBuilder().img(image).build(), false);
    }
}

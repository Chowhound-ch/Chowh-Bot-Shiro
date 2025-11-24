package per.chowh.bot.plugins.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.mikuac.shiro.common.utils.OneBotMedia;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import per.chowh.bot.core.bot.domain.ChowhBot;
import per.chowh.bot.core.bot.domain.ChowhBotContainer;
import per.chowh.bot.core.registery.annotation.EventListener;
import per.chowh.bot.core.utils.MsgBuilder;
import per.chowh.bot.utils.HttpProxyUtils;
import per.chowh.bot.utils.HttpUtils;
import per.chowh.bot.utils.JacksonUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : Chowhound
 * @since : 2025/11/24 - 15:20
 */
@Component
public class VikiApiPlugins {

    public static final String BASE_API = "http://chowh.top:4399/v2";

    @Autowired
    private ChowhBotContainer chowhBotContainer;

    @Scheduled(cron ="0 0 9 * * ?")
    public void sayMidRemind() throws IOException {
        ChowhBot bot = chowhBotContainer.getDefaultBot();
        bot.sendGroupMsg(812199522L, getTheNewsMsg(), false);
    }


    @EventListener(cmd = "60s", name = "60s读懂世界")
    public void getGroupTitle(ChowhBot bot, GroupMessageEvent event) throws IOException {
        bot.sendGroupMsg(event.getGroupId(), getTheNewsMsg(), false);
    }

    private String getTheNewsMsg() throws IOException {
        //        byte[] bytes = HttpUtils.doGetBytes(BASE_API + "/60s?encoding=image-proxy");
        String resStr = HttpUtils.doGetStr(BASE_API + "/60s");
        JsonNode node = JacksonUtil.readTree(resStr);
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


}

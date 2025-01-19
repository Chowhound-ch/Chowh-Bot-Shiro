package per.chowhound.bot.plugins;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.mikuac.shiro.annotation.MessageHandlerFilter;
import com.mikuac.shiro.annotation.PrivateMessageHandler;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import com.mikuac.shiro.enums.MsgTypeEnum;
import com.mikuac.shiro.model.ArrayMsg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import per.chowhound.bot.utils.JacksonUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;

@Slf4j
@Shiro
@Component
public class ImagesMsgStorage {
    @Value("${per.file-location}")
    private String IMAGE_PATH;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @PrivateMessageHandler
    @MessageHandlerFilter(types = {MsgTypeEnum.image, MsgTypeEnum.forward})
    public void test(Bot bot, PrivateMessageEvent event, Matcher matcher) {
        List<ArrayMsg> arrayMsg = event.getArrayMsg();

        for (ArrayMsg msg : arrayMsg) {
            if (MsgTypeEnum.image.equals(msg.getType())) {
                bot.sendPrivateMsg(event.getUserId(), msg.getData().toString(), false);
            } else if (MsgTypeEnum.forward.equals(msg.getType())) {
                String content = msg.getData().get("content");
                JsonNode node = JacksonUtil.readTree(content);
                saveImage(node);
            }
        }
    }

    private void saveImage(JsonNode node) {
        for (JsonNode jsonNode : node) {
            for (JsonNode message : jsonNode.get("message")) {
                if (message.get("type").asText().equals("image")) {
                    String url = message.get("data").get("url").asText();
                    HttpUtil.downloadFile(url, getImagePath() + File.separatorChar + message.get("data").get("file_unique").asText() + ".jpeg");
                    log.info("id: {} --- url:{}", message.get("data").get("file_unique"), url);
                } else if (message.get("type").asText().equals("forward")) {
                    saveImage(message.get("data").get("content"));
                }
            }
        }
    }

    private String getImagePath() {
        String format = dateFormat.format(new Date());
        Path path = Path.of(IMAGE_PATH + File.separatorChar + format);
        try {
            if (!Files.exists(path)) {
                Files.createDirectory(path);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return path.toString();
    }

}

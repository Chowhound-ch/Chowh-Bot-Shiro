package per.chowh.bot.plugins.img;

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
import per.chowh.bot.utils.JacksonUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;

@Slf4j
@Shiro
@Component
public class ImagesMsgStorage {
    @Value("${per.file-location}")
    private String IMAGE_PATH;
    @Value("${per.video-location-raw}")
    private String VIDEO_PATH_RAW;
    @Value("${per.video-location}")
    private String VIDEO_PATH;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @PrivateMessageHandler
    @MessageHandlerFilter(types = {MsgTypeEnum.image, MsgTypeEnum.forward, MsgTypeEnum.video})
    public void test(Bot bot, PrivateMessageEvent event, Matcher matcher) {
        List<ArrayMsg> arrayMsg = event.getArrayMsg();
        for (ArrayMsg msg : arrayMsg) {
            JsonNode data = msg.getData();
            if (MsgTypeEnum.image.equals(msg.getType())) {
                saveImage(data.get("url").asText(), data.get("file").asText());
            } else if (MsgTypeEnum.video.equals(msg.getType())) {
                saveVideo(data.get("url").asText(), data.get("file").asText());
            } else if (MsgTypeEnum.forward.equals(msg.getType())) {
                JsonNode node = JacksonUtil.readTree(data.get("content"));
                saveMedia(node);
            }
        }
    }


    private void saveVideo(String url, String fileName) {
        String substring = url.substring(VIDEO_PATH_RAW.length());

        try {
            Files.copy(Path.of(VIDEO_PATH + File.separatorChar + substring), Path.of(getImagePath() + File.separatorChar + fileName));
            log.info("video fileName: {} --- url:{}", fileName, url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void saveMedia(JsonNode node) {
        for (JsonNode jsonNode : node) {
            for (JsonNode message : jsonNode.get("message")) {
                JsonNode data = message.get("data");
                if ("image".equals(message.get("type").asText())) {
                    saveImage(data.get("url").asText(), data.get("file").asText());
                } else if ("video".equals(message.get("type").asText())) {
                    saveVideo(data.get("url").asText(), data.get("file").asText());
                } else if (message.get("type").asText().equals("forward")) {
                    saveMedia(data.get("content"));
                }
            }
        }
    }
    private void saveImage(String url, String fileName) {
        HttpUtil.downloadFile(url, getImagePath() + File.separatorChar + fileName);
        log.info("image: fileName: {} --- url:{}", fileName, url);
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

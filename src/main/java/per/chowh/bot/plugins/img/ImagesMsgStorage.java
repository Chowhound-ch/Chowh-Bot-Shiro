package per.chowh.bot.plugins.img;

import cn.hutool.http.HttpUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import com.mikuac.shiro.enums.MsgTypeEnum;
import com.mikuac.shiro.model.ArrayMsg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import per.chowh.bot.core.registery.annotation.EventListener;
import per.chowh.bot.ext.filter.annotation.EventFilter;
import per.chowh.bot.ext.msgarr.domain.ArrayMsgFlatList;
import per.chowh.bot.utils.FileUtils;

import java.io.File;
import java.util.List;

@Slf4j
@Component
public class ImagesMsgStorage {
    @Value("${per.file-location}")
    private String IMAGE_PATH;
    @Value("${per.video-location}")
    private String VIDEO_PATH;


    @EventListener(name = "批量存图")
    @EventFilter(types = {MsgTypeEnum.image, MsgTypeEnum.video, MsgTypeEnum.forward})
    public void storageImages(PrivateMessageEvent event, ArrayMsgFlatList arrayMsgFlatList) {
        for (List<ArrayMsg> arrayMsgs : arrayMsgFlatList) {
            arrayMsgs.forEach(arrayMsg -> {
                String path = null;
                if (MsgTypeEnum.image.equals(arrayMsg.getType())) {
                    path = IMAGE_PATH;
                } else if (MsgTypeEnum.video.equals(arrayMsg.getType())) {
                    path = VIDEO_PATH;
                } else {
                    return;
                }
                JsonNode data = arrayMsg.getData();
                if (!"[动画表情]".equals(data.get("summary").asText())) {
                    HttpUtil.downloadFile(data.get("url").asText(), FileUtils.getPathWithDate(path) + File.separatorChar + data.get("file").asText());
                }

            });
        }
    }
}

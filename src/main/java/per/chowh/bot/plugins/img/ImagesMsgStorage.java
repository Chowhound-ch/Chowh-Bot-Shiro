package per.chowh.bot.plugins.img;

import cn.hutool.http.HttpUtil;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import com.mikuac.shiro.enums.MsgTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import per.chowh.bot.core.bot.domain.ChowhBot;
import per.chowh.bot.core.registery.annotation.EventListener;
import per.chowh.bot.core.utils.FileInfo;
import per.chowh.bot.core.utils.MsgUtils;
import per.chowh.bot.ext.filter.annotation.EventFilter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class ImagesMsgStorage {
    @Value("${per.file-location}")
    private String IMAGE_PATH;
    @Value("${per.video-location}")
    private String VIDEO_PATH;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @EventListener(name = "批量存图")
    @EventFilter(types = {MsgTypeEnum.image, MsgTypeEnum.video, MsgTypeEnum.forward})
    public void storageImages(ChowhBot bot, PrivateMessageEvent event) {
        List<FileInfo> fileInfos = MsgUtils.getImageUrlFromMsg(bot, event, true);

        fileInfos.forEach(imageInfo -> {
            String desPath = imageInfo.isVideo() ? VIDEO_PATH : IMAGE_PATH;
            HttpUtil.downloadFile(imageInfo.getUrl(), getPathWithDate(desPath) + File.separatorChar + imageInfo.getFileName());
        });
    }





    private String getPathWithDate(String basePath) {
        String format = dateFormat.format(new Date());
        Path path = Path.of(basePath + File.separatorChar + format);
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

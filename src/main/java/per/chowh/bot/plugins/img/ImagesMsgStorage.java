package per.chowh.bot.plugins.img;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.mikuac.shiro.dto.event.message.MessageEvent;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import com.mikuac.shiro.enums.MsgTypeEnum;
import com.mikuac.shiro.model.ArrayMsg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import per.chowh.bot.core.bot.domain.ChowhBot;
import per.chowh.bot.core.permit.enums.GroupStatusEnum;
import per.chowh.bot.core.registery.annotation.EventListener;
import per.chowh.bot.core.utils.MsgBuilder;
import per.chowh.bot.ext.filter.annotation.EventFilter;
import per.chowh.bot.ext.support.msgarr.domain.ArrayMsgFlatList;
import per.chowh.bot.plugins.img.api.PixivApi;
import per.chowh.bot.plugins.img.entity.PixivImg;
import per.chowh.bot.utils.FileUtils;
import per.chowh.bot.utils.HttpProxyUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@SuppressWarnings("unused")
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
                String path;
                switch (arrayMsg.getType()) {
                    case image -> path = IMAGE_PATH;
                    case video -> path = VIDEO_PATH;
                    default -> {
                        return;
                    }
                }
                JsonNode data = arrayMsg.getData();
                if (!"[动画表情]".equals(data.get("summary").asText())) {
                    HttpUtil.downloadFile(data.get("url").asText(), FileUtils.getPathWithDate(path) + File.separatorChar + data.get("file").asText());
                }
            });
        }
    }

    @EventListener(name = "pixiv图r18", cmd = "/?来点\\s*(?<tags>.*)?\\s*(?<type>(涩|色|瑟))图", groupStatus = GroupStatusEnum.FULL)
    public void sendImagesR18(ChowhBot bot, MessageEvent event, String tags) throws IOException {
        List<String> tagList = StrUtil.isBlank(tags) ? Collections.emptyList() : Arrays.asList(tags.split("[,，]"));
        List<PixivImg> image = PixivApi.getImage(tagList, true);
        bot.sendForwardMsg(event, getPixivMsg(image));
    }

    @EventListener(name = "pixiv图", cmd = "/?来点\\s*(?<tags>.*[^(涩|色|瑟)])\\s*图")
    public void sendImages(ChowhBot bot, MessageEvent event, String tags) throws IOException {
        List<String> tagList = StrUtil.isBlank(tags) ? Collections.emptyList() : Arrays.asList(tags.split("[,，]"));
        List<PixivImg> image = PixivApi.getImage(tagList, false);
        bot.sendForwardMsg(event, getPixivMsg(image));
    }

    private List<String> getPixivMsg(List<PixivImg> image) throws IOException {
        if (image != null &&  !image.isEmpty()) {
            List<String> msgList = new ArrayList<>();
            for (PixivImg pixivImg : image) {
                String string = MsgBuilder.builder()
                        .img(HttpProxyUtils.doGetBytes(pixivImg.getUrls().getOriginal()))
                        .text("R18：" + (pixivImg.isR18() ? "是" : "否") + "        AI图：" + (pixivImg.getAiType() == 1 ? "是" : "否"))
                        .text("\n标题：" + pixivImg.getTitle() +
                                "\n标签：" + String.join(",", pixivImg.getTags())+
                                "\npid：" + pixivImg.getPid())
                        .text("\n作者：" + pixivImg.getAuthor() + "\nuid：" +  pixivImg.getUid())
                        .text("\n图片原地址：" + pixivImg.getUrls().getOriginal())
                        .build();
                log.info("title:{},url:{}", pixivImg.getTitle(), pixivImg.getUrls().getOriginal());
                msgList.add(string);
            }
            return msgList;
        }
        return Collections.emptyList();
    }

}

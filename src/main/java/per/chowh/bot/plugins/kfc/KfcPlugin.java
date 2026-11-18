package per.chowh.bot.plugins.kfc;

import com.mikuac.shiro.common.utils.MessageConverser;
import com.mikuac.shiro.common.utils.ShiroUtils;
import com.mikuac.shiro.enums.MsgTypeEnum;
import com.mikuac.shiro.model.ArrayMsg;
import io.minio.errors.MinioException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import per.chowh.bot.core.bot.domain.ChowhBot;
import per.chowh.bot.core.event.message.AnyMessageEvent;
import per.chowh.bot.core.event.message.GroupMessageEvent;
import per.chowh.bot.core.permit.enums.PermissionEnum;
import per.chowh.bot.core.registery.annotation.EventListener;
import per.chowh.bot.core.utils.BasePlugin;
import per.chowh.bot.core.utils.MinioUtils;
import per.chowh.bot.core.utils.MsgBuilder;
import per.chowh.bot.core.utils.MsgUtils;
import per.chowh.bot.ext.dialogue.annotation.SessionDependOn;
import per.chowh.bot.ext.dialogue.enums.SessionType;
import per.chowh.bot.ext.distribute.MsgDistributeService;
import per.chowh.bot.ext.filter.annotation.EventFilter;
import per.chowh.bot.plugins.kfc.domain.KfcData;
import per.chowh.bot.plugins.kfc.service.KfcDataService;
import per.chowh.bot.utils.HttpProxyUtils;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class KfcPlugin extends BasePlugin {
    public static final String KFC = "kfcvme50";

    @Autowired
    private MsgDistributeService distributeService;
    @Autowired
    private KfcDataService kfcDataService;

    @Scheduled(cron ="0 0 10 * * 4")
    public void kfcMsgTimer() {
        distributeService.distributeMsg(getKfcMsg(), KFC);
    }


    @EventListener(cmd = "kfcvme50", name = "随机kfc文案")
    public void getKfcMsg(ChowhBot bot, GroupMessageEvent event) {
        bot.sendGroupMsg(event.getGroupId(), getKfcMsg(), false);
    }

    @EventListener(cmd = "增加kfc", name = "增加kfc文案", permit = PermissionEnum.ADMIN)
    public void addKfcMsg(ChowhBot bot, AnyMessageEvent event) {
        // 给出提示，5分钟内输入
        bot.sendMsg(event, "请在5分钟内发送要增加的kfc文案", true);
    }

    @EventListener(name = "增加kfc文案-会话", permit = PermissionEnum.ADMIN)
    @SessionDependOn(dependOn = "KfcPlugin#addKfcMsg", type = SessionType.ONCE)
    public void addKfcMsgOnce(ChowhBot bot, AnyMessageEvent event) throws IOException, MinioException {
        List<ArrayMsg> segments = event.getArrayMsg();
        if (segments == null || segments.isEmpty()) {
            String message = event.getMessage();
            if (message == null || message.isBlank()) {
                message = event.getRawMessage();
            }
            segments = MessageConverser.stringToArray(message == null ? "" : message);
        }

        MsgBuilder builder = MsgBuilder.builder();
        for (ArrayMsg segment : segments) {
            if (segment.getType() == MsgTypeEnum.text) {
                builder.text(ShiroUtils.escape2(segment.getStringData("text").replace("&", "&amp;")));
            } else if (segment.getType() == MsgTypeEnum.image) {
                String url = segment.getStringData("url");
                if (url == null) {
                    throw new IOException("图片消息缺少 HTTP 下载地址");
                }
                byte[] bytes = HttpProxyUtils.doGetBytes(url, true);
                String objName = "kfc/" + UUID.randomUUID();
                MinioUtils.uploadObject(objName, bytes);
                builder.img(MinioUtils.objectUrlTemplate(objName));
            } else {
                builder.text(segment.toCQCode());
            }
        }


        KfcData data = new KfcData();
        data.setKfcText(builder.build());
        Date now = new Date();
        data.setCreateTime(now);
        data.setModifyTime(now);
        if (!kfcDataService.save(data)) {
            throw new IllegalStateException("KFC 文案未写入数据库");
        }
        bot.sendMsg(event, "KFC 文案已保存", true);
    }



    public String getKfcMsg() {
        KfcData data = kfcDataService.getKfcDataRandom();
        String kfcText = data == null ? null : data.getKfcText();
        if (kfcText == null || kfcText.isBlank()) {
            return "暂无 KFC 文案";
        }
        return MsgUtils.parseTextImageMsg(kfcText);
    }


}

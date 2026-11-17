package per.chowh.bot.plugins.kfc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import per.chowh.bot.core.bot.domain.ChowhBot;
import per.chowh.bot.core.event.message.AnyMessageEvent;
import per.chowh.bot.core.event.message.GroupMessageEvent;
import per.chowh.bot.core.registery.annotation.EventListener;
import per.chowh.bot.core.utils.BasePlugin;
import per.chowh.bot.ext.dialogue.annotation.SessionDependOn;
import per.chowh.bot.ext.dialogue.enums.SessionType;
import per.chowh.bot.ext.dict.service.DataDictService;
import per.chowh.bot.ext.distribute.MsgDistributeService;
import per.chowh.bot.plugins.kfc.domain.KfcData;
import per.chowh.bot.plugins.kfc.service.KfcDataService;

@Slf4j
@Component
public class KfcPlugin extends BasePlugin {
    public static final String KFC = "kfc";

    @Autowired
    private MsgDistributeService distributeService;
    @Autowired
    private KfcDataService kfcDataService;
    @Autowired
    private DataDictService dataDictService;

    @Scheduled(cron ="0 0 9 * * ?")
    public void kfcMsgTimer() {
        distributeService.distributeMsg(getKfcMsg(), KFC);
    }


    @EventListener(cmd = "kfc", name = "随机kfc文案")
    public void getKfcMsg(ChowhBot bot, GroupMessageEvent event) {
        bot.sendGroupMsg(event.getGroupId(), getKfcMsg(), false);
    }

    @EventListener(cmd = "增加kfc", name = "增加kfc文案")
    public void addKfcMsg(ChowhBot bot, GroupMessageEvent event) {

    }

    @EventListener(name = "增加kfc文案-会话")
    @SessionDependOn(dependOn = "KfcPlugin#addKfcMsg", type = SessionType.ONCE)
    public void addKfcMsgOnce(ChowhBot bot, AnyMessageEvent event) {

    }


    public String getKfcMsg() {
        KfcData data = kfcDataService.getKfcDataRandom();
        String kfcText = data == null ? null : data.getKfcText();
        if (kfcText == null || kfcText.isBlank()) {
            return "暂无 KFC 文案";
        }
        return parseTextImageMsg(kfcText);
    }


}

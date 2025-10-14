package per.chowh.bot.plugins.remind;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import per.chowh.bot.core.bot.domain.ChowhBot;
import per.chowh.bot.core.bot.domain.ChowhBotContainer;

/**
 * @author : Chowhound
 * @since : 2025/10/14 - 11:22
 */
@Component
public class MidRemindJob {
    @Autowired
    private ChowhBotContainer chowhBotContainer;

    @Scheduled(cron ="0,30 25,26,27 13 * * ?")
    public void sayMidRemind() {
        ChowhBot bot = chowhBotContainer.getDefaultBot();
        bot.sendPrivateMsg(825352674L, "1", true);
    }

}

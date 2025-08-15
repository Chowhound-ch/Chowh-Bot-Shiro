package per.chowh.bot.core.bot.domain;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotContainer;

import java.util.Map;

/**
 * @author : Chowhound
 * @since : 2025/7/9 - 15:16
 */
public class ChowhBotContainer extends BotContainer {
    /**
     * Bot容器
     */
    public final Map<Long, Bot> robots;

    public ChowhBotContainer(Map<Long, Bot>  robots) {
        this.robots = robots;
    }

    public ChowhBot get(long id){
        return (ChowhBot) robots.get(id);
    }
}

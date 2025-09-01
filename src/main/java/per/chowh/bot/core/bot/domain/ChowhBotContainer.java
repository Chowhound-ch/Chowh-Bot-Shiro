package per.chowh.bot.core.bot.domain;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotContainer;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author : Chowhound
 * @since : 2025/7/9 - 15:16
 */
public class ChowhBotContainer extends BotContainer {
    /**
     * Bot容器
     */
    private final Map<Long, Bot> robots;

    public ChowhBotContainer(Map<Long, Bot>  robots) {
        this.robots = robots;
    }

    public ChowhBot get(long id){
        return (ChowhBot) robots.get(id);
    }

    public void forEach(BiConsumer<Long, ChowhBot> consumer){
        robots.forEach((id, bot) -> consumer.accept(id, (ChowhBot) bot));
    }

}

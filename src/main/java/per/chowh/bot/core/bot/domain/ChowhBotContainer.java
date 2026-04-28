package per.chowh.bot.core.bot.domain;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotContainer;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

/**
 * @author : Chowhound
 * @since : 2025/7/9 - 15:16
 */
@Configuration
public class ChowhBotContainer extends BotContainer {
    /**
     * Bot容器
     */
    private static final Map<Long, Bot> robots = new ConcurrentHashMap<>();

    public ChowhBot get(long id){
        return (ChowhBot) robots.get(id);
    }

    public void put(ChowhBot bot){
        robots.put(bot.getSelfId(), bot);
    }

    public void forEach(BiConsumer<Long, ChowhBot> consumer){
        robots.forEach((id, bot) -> consumer.accept(id, (ChowhBot) bot));
    }

    public static ChowhBot getDefaultBot(){
        return (ChowhBot) robots.values().iterator().next();
    }

}

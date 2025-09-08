package per.chowh.bot.core.bot.config;

import com.mikuac.shiro.core.BotContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import per.chowh.bot.core.bot.domain.ChowhBotContainer;

/**
 * @author : Chowhound
 * @since : 2025/7/9 - 15:19
 */
@Configuration
public class ChowhBotProxyConfig {
    @Bean
    public ChowhBotContainer chowhBotContainer(BotContainer  botContainer) {
        return new ChowhBotContainer(botContainer.robots);
    }
}

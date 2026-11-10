package per.chowh.bot.config;

import org.jspecify.annotations.NonNull;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class ChowhBotConfig implements EnvironmentAware {
    public static Environment ENVIRONMENT;

    @Override
    public void setEnvironment(@NonNull Environment environment) {
        ChowhBotConfig.ENVIRONMENT = environment;
    }
}

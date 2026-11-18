package per.chowh.bot.config;

import ch.qos.logback.core.spi.ContextAware;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class ChowhBotConfig implements EnvironmentAware, ApplicationContextAware {
    public static Environment ENVIRONMENT;
    public static ApplicationContext APPLICATION_CONTEXT;


    @Override
    public void setEnvironment(@NonNull Environment environment) {
        ChowhBotConfig.ENVIRONMENT = environment;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ChowhBotConfig.APPLICATION_CONTEXT = applicationContext;
    }
}

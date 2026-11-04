package per.chowh.bot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableCaching
@MapperScan("per.chowh.bot")
@SpringBootApplication
@EnableAspectJAutoProxy
@EnableScheduling
public class ChowhBotApplication {
    public static Environment environment;
    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(ChowhBotApplication.class, args);
        environment = run.getEnvironment();
    }
}
package per.chowh.bot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableCaching
@MapperScan("per.chowh.bot.core.permit.mapper")
@SpringBootApplication
@EnableAspectJAutoProxy
@EnableScheduling
public class ChowhBotApplication {
    public static void main(String[] args) {
        SpringApplication.run(ChowhBotApplication.class, args);
    }
}
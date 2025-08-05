package per.chowh.bot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableCaching
@MapperScan("per.chowh.bot.plugins.core.mapper")
@SpringBootApplication
@EnableAspectJAutoProxy
public class ChowhBotApplication {
    public static void main(String[] args) {
        SpringApplication.run(ChowhBotApplication.class, args);
    }
}
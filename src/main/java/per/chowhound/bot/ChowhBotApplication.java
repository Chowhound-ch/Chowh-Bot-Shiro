package per.chowhound.bot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@MapperScan("per.chowhound.bot.plugins.core.mapper")
@SpringBootApplication
@EnableAspectJAutoProxy
public class ChowhBotApplication {
    public static void main(String[] args) {
        SpringApplication.run(ChowhBotApplication.class, args);
    }
}
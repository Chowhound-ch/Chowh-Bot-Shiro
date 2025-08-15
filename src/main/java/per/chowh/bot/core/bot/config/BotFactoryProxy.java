package per.chowh.bot.core.bot.config;

import com.mikuac.shiro.core.Bot;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import per.chowh.bot.core.bot.domain.ChowhBot;

/**
 * @author : Chowhound
 * @since : 2025/7/9 - 14:53
 */
@EnableAspectJAutoProxy
@Aspect
@Configuration
public class BotFactoryProxy {

    @Pointcut("execution(com.mikuac.shiro.core.Bot com.mikuac.shiro.core.BotFactory.createBot(*,*))")
    public void pointCut(){
    }

    /**
     * 代理Shiro创建Bot的方法，使其返回ChowhBot
     * @author : Chowhound
     * @since : 2025/07/09 - 15:14
     */
    @Around("per.chowh.bot.core.bot.config.BotFactoryProxy.pointCut()")
    public Object afterReturning(ProceedingJoinPoint joinPoint){
        Object returnVal ;
        try {
            returnVal  = joinPoint.proceed(joinPoint.getArgs());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        if (returnVal  instanceof Bot shiroBot){
            return new ChowhBot(
                    shiroBot.getSelfId(),
                    shiroBot.getSession(),
                    shiroBot.getActionHandler(),
                    shiroBot.getPluginList(),
                    shiroBot.getAnnotationMethodContainer(),
                    shiroBot.getBotMessageEventInterceptor()
            );
        }
        return returnVal;
    }
}

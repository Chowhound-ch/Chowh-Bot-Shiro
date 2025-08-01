package per.chowhound.bot.plugins.core.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import per.chowhound.bot.plugins.core.annotation.EventFilter;

/**
 * @author : Chowhound
 * @since : 2025/8/1 - 15:39
 */
@Aspect
public class EventFilterProxy {
    @Pointcut("@annotation(per.chowhound.bot.plugins.core.annotation.EventFilter)")
    public void pointCut() {
    }

    @Around(value = "pointCut()", argNames = "")
    public Object around(ProceedingJoinPoint joinPoint, EventFilter filter) throws Throwable {

    }
}

package per.chowh.bot.core.registery.annotation;

import org.springframework.core.annotation.AliasFor;
import per.chowh.bot.core.permit.enums.GroupStatusEnum;
import per.chowh.bot.core.permit.enums.PermissionEnum;
import per.chowh.bot.core.registery.domain.enums.MatchType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author : Chowhound
 * @since : 2025/8/1 - 11:31
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface EventListener {
    @AliasFor("cmd")
    String value() default "";
    // 权限
    PermissionEnum permit() default PermissionEnum.NORMAL;
    // 需要的群状态
    GroupStatusEnum groupStatus() default GroupStatusEnum.NORMAL;
    // Name
    String name() default "";
    // 异步
    boolean async() default true;
    // 执行顺序
    int order() default Integer.MAX_VALUE;
    // 待匹配命令
    @AliasFor("value")
    String cmd() default "";

    MatchType  matchType() default MatchType.REGEX;
    // 是否拿rawMessage匹配
    boolean isRaw() default false;

}

package per.chowh.bot.core.registery.annotation;

import per.chowh.bot.core.permit.enums.GroupStatusEnum;
import per.chowh.bot.core.permit.enums.PermissionEnum;

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
    // 权限
    PermissionEnum permit() default PermissionEnum.COMMON;
    // 需要的群状态
    GroupStatusEnum groupStatus() default GroupStatusEnum.NORMAL;
    // Name
    String name() default "";
    // 异步
    boolean async() default true;

    int order() default Integer.MAX_VALUE;
}

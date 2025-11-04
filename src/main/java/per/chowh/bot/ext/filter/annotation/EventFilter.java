package per.chowh.bot.ext.filter.annotation;

import com.mikuac.shiro.enums.MsgTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 一些不常用的过滤
 * @author : Chowhound
 * @since : 2025/9/4 - 17:25
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface EventFilter {

    MsgTypeEnum[] types() default {};
}

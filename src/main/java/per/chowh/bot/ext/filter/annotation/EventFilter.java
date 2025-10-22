package per.chowh.bot.ext.filter.annotation;

import com.mikuac.shiro.enums.MsgTypeEnum;

/**
 * 一些不常用的过滤
 * @author : Chowhound
 * @since : 2025/9/4 - 17:25
 */
public @interface EventFilter {

    MsgTypeEnum[] types() default {};
}

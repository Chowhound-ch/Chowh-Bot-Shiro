package per.chowh.bot.ext.dialogue.annotation;


import org.springframework.core.annotation.AliasFor;
import per.chowh.bot.ext.dialogue.enums.SessionType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface SessionDependOn {

    @AliasFor("dependOn")
    String[] value() default "";

    @AliasFor("value")
    String[] dependOn() default "";

    SessionType type() default SessionType.ONCE;

    long time() default 5;

    TimeUnit unit() default TimeUnit.MINUTES;
}

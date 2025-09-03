package per.chowh.bot.core.registery.cmd.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.BiFunction;

/**
 * @author : Chowhound
 * @since : 2025/9/3 - 16:32
 */
@AllArgsConstructor
@Getter
public enum MatchType {
    START_WITH((cmd, msg) -> msg.startsWith(cmd)),
    EQUAL((cmd, msg) -> msg.equals(cmd)),
    END_WITH((cmd, msg) -> msg.endsWith(cmd)),
    CONTAIN((cmd, msg) -> msg.contains(cmd)),
    REGEX((cmd, msg) -> msg.matches(cmd))
    ;

    final BiFunction<String, String, Boolean> matcher;
}

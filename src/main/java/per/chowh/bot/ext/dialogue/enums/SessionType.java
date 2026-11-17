package per.chowh.bot.ext.dialogue.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SessionType {
    ONCE("once"),
    CONTINUE("continue"),
    ;

    final String type;
}

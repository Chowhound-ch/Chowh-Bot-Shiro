package per.chowh.bot.core.registery.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Parameter;

/**
 * @author : Chowhound
 * @since : 2025/8/28 - 17:54
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventParam {
    private EventMethod method;

    private Parameter parameter;

    private String name;
}

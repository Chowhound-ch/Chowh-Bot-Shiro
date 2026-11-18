package per.chowh.bot.core.registery.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventInvokeLog {
    private Long userId;
    private Long groupId;
    private Instant invokeTime;
    private String command;
    private long eventSequence;

    public EventInvokeLog(Long userId, Long groupId, Instant invokeTime, String command) {
        this(userId, groupId, invokeTime, command, 0);
    }
}

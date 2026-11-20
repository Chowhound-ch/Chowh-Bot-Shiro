package per.chowh.bot.web.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.Instant;

@Data
@TableName("bot_log")
public class BotLog {
    @TableId
    private Long logId;
    private String methodId;
    private String userId;
    private String groupId;
    private Instant invokeTime;
    private String command;
}

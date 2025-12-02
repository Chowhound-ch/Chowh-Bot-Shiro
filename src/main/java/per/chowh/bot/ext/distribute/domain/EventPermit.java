package per.chowh.bot.ext.distribute.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import per.chowh.bot.common.domain.BaseEntity;

/**
 * @TableName event_permit
 */
@TableName(value ="event_permit")
@Data
public class EventPermit extends BaseEntity {
    @TableId
    private Integer permitId;

    private String eventFlag;

    /**
     * 1：群聊，0：用户
     */
    private Integer permitType;

    private Long target;
}
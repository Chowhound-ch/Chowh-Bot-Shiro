package per.chowh.bot.core.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import per.chowh.bot.common.domain.BaseEntity;
import per.chowh.bot.core.enums.GroupStatusEnum;

/**
 * @TableName group
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="`group`")
@Data
public class Group extends BaseEntity {
    @TableId
    private Long id;

    private Long groupId;

    private GroupStatusEnum groupStatus;

    public static final Group NULL = new Group();

    static {
        NULL.setNull(true);
    }
}
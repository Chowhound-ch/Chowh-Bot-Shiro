package per.chowhound.bot.plugins.core.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;
import per.chowhound.bot.common.domain.BaseEntity;
import per.chowhound.bot.plugins.core.enums.GroupStatusEnum;

/**
 * @TableName group
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="group")
@Data
public class Group extends BaseEntity {
    @TableId
    private Long id;

    private Long groupId;

    private GroupStatusEnum groupStatus;
}
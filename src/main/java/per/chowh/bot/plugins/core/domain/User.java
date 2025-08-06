package per.chowh.bot.plugins.core.domain;

import com.baomidou.mybatisplus.annotation.*;

import lombok.Data;
import lombok.EqualsAndHashCode;
import per.chowh.bot.common.domain.BaseEntity;
import per.chowh.bot.plugins.core.enums.PermissionEnum;

/**
 *
 * @TableName user
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="user")
@Data
public class User extends BaseEntity {
    @TableId
    private Long id;

    private Long userId;

    @TableField("role")
    private PermissionEnum role;

    public static final User NULL = new User();
}
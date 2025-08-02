package per.chowhound.bot.plugins.core.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;
import per.chowhound.bot.common.domain.BaseEntity;
import per.chowhound.bot.plugins.core.enums.PermissionEnum;

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

    private PermissionEnum role;
}
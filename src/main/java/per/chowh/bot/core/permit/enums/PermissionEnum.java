package per.chowh.bot.core.permit.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : Chowhound
 * @since : 2025/8/1 - 10:47
 */
@Getter
@AllArgsConstructor
public enum PermissionEnum {
    OWNER(5),
    ADMIN(3),
    NORMAL(0);

    @EnumValue
    private final int value;
}

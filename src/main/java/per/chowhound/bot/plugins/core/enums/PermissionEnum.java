package per.chowhound.bot.plugins.core.enums;

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
    ADMIN(4),
    COMMON(1);

    @EnumValue
    private final int value;
}

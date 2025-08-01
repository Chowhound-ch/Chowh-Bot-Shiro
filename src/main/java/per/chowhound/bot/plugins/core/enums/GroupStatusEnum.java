package per.chowhound.bot.plugins.core.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : Chowhound
 * @since : 2025/8/1 - 13:55
 */
@Getter
@AllArgsConstructor
public enum GroupStatusEnum {
    // 全部功能
    FULL(5),
    // 正常功能
    NORMAL(1),
    // 关闭
    CLOSED(0);
    @EnumValue
    private final int value;
}
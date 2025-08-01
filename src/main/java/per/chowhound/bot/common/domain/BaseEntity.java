package per.chowhound.bot.common.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.Date;

/**
 * @author : Chowhound
 * @since : 2025/8/1 - 13:57
 */
@Data
public class BaseEntity {

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date modifyTime;

    private Integer isDelete;
}

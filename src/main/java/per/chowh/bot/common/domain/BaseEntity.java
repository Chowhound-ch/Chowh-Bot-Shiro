package per.chowh.bot.common.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author : Chowhound
 * @since : 2025/8/1 - 13:57
 */
@NoArgsConstructor
@Data
public class BaseEntity implements Serializable {

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date modifyTime;

    private Integer isDelete;

    @TableField(exist = false)
    private boolean isNull = false;
}

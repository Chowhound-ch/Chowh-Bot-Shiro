package per.chowh.bot.ext.dict.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import per.chowh.bot.common.domain.BaseEntity;

/**
 * @TableName data_dict
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="data_dict")
@Data
public class DataDict extends BaseEntity {
    @TableId
    private Integer dictId;

    private String dictCode;

    private String dictName;

    private String dictValue;
    public static final DataDict NULL = new DataDict();

    static {
        NULL.setNull(true);
    }
}
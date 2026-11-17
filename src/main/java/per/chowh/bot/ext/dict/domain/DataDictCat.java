package per.chowh.bot.ext.dict.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import per.chowh.bot.common.domain.BaseEntity;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("data_dict_cat")
public class DataDictCat extends BaseEntity {
    @TableId(type = IdType.INPUT)
    private String catCode;
    private String catName;
}

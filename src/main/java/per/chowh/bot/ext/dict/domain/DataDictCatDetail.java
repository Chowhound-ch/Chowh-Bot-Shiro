package per.chowh.bot.ext.dict.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import per.chowh.bot.common.domain.BaseEntity;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("data_dict_cat_detail")
public class DataDictCatDetail extends BaseEntity {
    @TableId
    private Long detailId;
    private String catCode;
    private String dictCode;
    private String dictName;
}

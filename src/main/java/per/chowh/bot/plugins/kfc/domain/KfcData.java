package per.chowh.bot.plugins.kfc.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * @TableName kfc_data
 */
@TableName(value ="kfc_data")
@Data
public class KfcData {
    @TableId(type = IdType.AUTO)
    private Integer dataId;

    private String kfcText;

    private Date createTime;

    private Date modifyTime;

    private Integer isDelete;
}

package per.chowh.bot.plugins.kfc.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * @TableName kfc_data
 */
@TableName(value ="kfc_data")
@Data
public class KfcData {
    private Integer dataId;

    private String kfcText;

    private Date createTime;

    private Date modifyTime;

    private Integer isDelete;
}
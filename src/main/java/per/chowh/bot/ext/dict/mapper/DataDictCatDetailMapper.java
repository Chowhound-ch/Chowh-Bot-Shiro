package per.chowh.bot.ext.dict.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import per.chowh.bot.ext.dict.domain.DataDictCatDetail;

import java.util.Date;

public interface DataDictCatDetailMapper extends BaseMapper<DataDictCatDetail> {
    int saveDetail(@Param("catCode") String catCode, @Param("dictCode") String dictCode,
                   @Param("dictName") String dictName, @Param("now") Date now);
}

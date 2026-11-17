package per.chowh.bot.ext.dict.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import per.chowh.bot.ext.dict.domain.DataDictCat;

import java.util.Date;

public interface DataDictCatMapper extends BaseMapper<DataDictCat> {
    int saveCategory(@Param("catCode") String catCode, @Param("catName") String catName,
                     @Param("now") Date now);
}

package per.chowh.bot.ext.dict.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import per.chowh.bot.ext.dict.domain.DataDict;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author hh825
* @description 针对表【data_dict】的数据库操作Mapper
* @createDate 2025-12-01 16:27:58
* @Entity per.chowh.bot.ext.dict.domain.DataDict
*/
public interface DataDictMapper extends BaseMapper<DataDict> {
    IPage<String> listDictNamePage(IPage<DataDict> page);
}





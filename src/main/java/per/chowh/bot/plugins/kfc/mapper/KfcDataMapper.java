package per.chowh.bot.plugins.kfc.mapper;

import per.chowh.bot.plugins.kfc.domain.KfcData;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author hh825
* @description 针对表【kfc_data】的数据库操作Mapper
* @createDate 2026-11-17 11:16:44
* @Entity per.chowh.bot.plugins.kfc.domain.KfcData
*/
public interface KfcDataMapper extends BaseMapper<KfcData> {
    KfcData selectRandom();
}





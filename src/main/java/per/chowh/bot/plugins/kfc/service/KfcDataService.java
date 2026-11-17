package per.chowh.bot.plugins.kfc.service;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import per.chowh.bot.plugins.kfc.domain.KfcData;
import per.chowh.bot.plugins.kfc.mapper.KfcDataMapper;
import org.springframework.stereotype.Service;

/**
* @author hh825
* @description 针对表【kfc_data】的数据库操作Service实现
* @createDate 2026-11-17 11:16:44
*/
@Service
public class KfcDataService extends ServiceImpl<KfcDataMapper, KfcData> {

    public KfcData getKfcDataRandom() {
        return baseMapper.selectRandom();
    }

}





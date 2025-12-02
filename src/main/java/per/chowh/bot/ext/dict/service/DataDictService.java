package per.chowh.bot.ext.dict.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import per.chowh.bot.ext.dict.domain.DataDict;
import per.chowh.bot.ext.dict.mapper.DataDictMapper;
import org.springframework.stereotype.Service;
import per.chowh.bot.ext.distribute.domain.EventPermit;

import java.util.List;

/**
* @author hh825
* @description 针对表【data_dict】的数据库操作Service实现
* @createDate 2025-12-01 16:27:58
*/
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
@CacheConfig(cacheNames = "dataDict")
@Service
public class DataDictService extends ServiceImpl<DataDictMapper, DataDict>{
    @Autowired
    private DataDictService self;

    @CachePut(key = "#entity.dictCode+ '&value&' + #entity.dictName")
    public DataDict saveDataDict(DataDict entity) {
        DataDict dictValue = self.getDictValue(entity.getDictCode(), entity.getDictName());
        if (!dictValue.isNull()) {
            entity.setDictId(dictValue.getDictId());
            this.updateById(entity);
        } else {
            this.save(entity);
        }
        return entity;
    }


    public IPage<String> listPage(Page<DataDict> page) {
        return baseMapper.listDictNamePage(page);
    }

    public List<DataDict> listByDictCode(String dictCode) {
        LambdaQueryWrapper<DataDict> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DataDict::getDictCode, dictCode);
        return list(wrapper);
    }

    @Cacheable(key = "#dictCode+'&value&'+#dictName")
    public DataDict getDictValue(String dictCode, String dictName) {
        LambdaQueryWrapper<DataDict> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DataDict::getDictCode, dictCode);
        wrapper.eq(DataDict::getDictName, dictName);
        DataDict dataDict = getOne(wrapper);
        if (dataDict == null) {
            return DataDict.NULL;
        }
        return dataDict;
    }
    @Cacheable(key = "#dictCode+'&name&'+#dictValue")
    public DataDict getDictName(String dictCode, String dictValue) {
        LambdaQueryWrapper<DataDict> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DataDict::getDictCode, dictCode);
        wrapper.eq(DataDict::getDictValue, dictValue);
        DataDict dataDict = getOne(wrapper);
        if (dataDict == null) {
            return DataDict.NULL;
        }
        return dataDict;
    }
}





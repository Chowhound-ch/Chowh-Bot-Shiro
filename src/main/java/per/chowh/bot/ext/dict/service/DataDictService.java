package per.chowh.bot.ext.dict.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import per.chowh.bot.ext.dict.domain.DataDictCat;
import per.chowh.bot.ext.dict.domain.DataDictCatDetail;
import per.chowh.bot.ext.dict.mapper.DataDictCatDetailMapper;
import per.chowh.bot.ext.dict.mapper.DataDictCatMapper;

import java.util.Date;
import java.util.List;

@Service
@CacheConfig(cacheNames = "dataDictName")
public class DataDictService {
    private final DataDictCatMapper categoryMapper;
    private final DataDictCatDetailMapper detailMapper;

    public DataDictService(DataDictCatMapper categoryMapper, DataDictCatDetailMapper detailMapper) {
        this.categoryMapper = categoryMapper;
        this.detailMapper = detailMapper;
    }

    public IPage<DataDictCat> pageCategories(Page<DataDictCat> page) {
        return categoryMapper.selectPage(page,
                new LambdaQueryWrapper<DataDictCat>().orderByAsc(DataDictCat::getCatCode));
    }

    public void saveCategory(String catCode, String catName) {
        categoryMapper.saveCategory(catCode, catName, new Date());
    }

    @CacheEvict(key = "new org.springframework.cache.interceptor.SimpleKey(#p0, #p1)", condition = "#result")
    public boolean saveDetail(String catCode, String dictCode, String dictName) {
        return detailMapper.saveDetail(catCode, dictCode, dictName, new Date()) > 0;
    }

    public List<DataDictCatDetail> listDetails(String catCode) {
        return detailMapper.selectList(new LambdaQueryWrapper<DataDictCatDetail>()
                .eq(DataDictCatDetail::getCatCode, catCode)
                .orderByAsc(DataDictCatDetail::getDictCode));
    }

    @Cacheable(sync = true, condition = "#p0 != null && #p1 != null")
    public String getDetailName(String catCode, String dictCode) {
        DataDictCatDetail detail = detailMapper.selectOne(new LambdaQueryWrapper<DataDictCatDetail>()
                .select(DataDictCatDetail::getDictName)
                .eq(DataDictCatDetail::getCatCode, catCode)
                .eq(DataDictCatDetail::getDictCode, dictCode));
        return detail == null ? dictCode : detail.getDictName();
    }
}

package per.chowh.bot.ext.distribute.mapper;

import per.chowh.bot.ext.distribute.domain.EventPermit;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

/**
* @author hh825
* @description 针对表【event_permit】的数据库操作Mapper
* @createDate 2025-12-01 13:57:12
* @Entity per.chowh.bot.ext.distribute.domain.EventPermit
*/
public interface EventPermitMapper extends BaseMapper<EventPermit> {
    int insertMissing(@Param("permit") EventPermit permit, @Param("now") Date now);
}





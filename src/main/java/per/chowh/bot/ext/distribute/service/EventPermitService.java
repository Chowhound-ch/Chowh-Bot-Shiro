package per.chowh.bot.ext.distribute.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.transaction.annotation.Transactional;
import per.chowh.bot.ext.distribute.domain.EventPermit;
import per.chowh.bot.ext.distribute.mapper.EventPermitMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
* @author hh825
* @description 针对表【event_permit】的数据库操作Service实现
* @createDate 2025-12-01 13:57:12
*/
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
@CacheConfig(cacheNames = "eventPermit")
@Service
public class EventPermitService extends ServiceImpl<EventPermitMapper, EventPermit>{
    public List<EventPermit> getByEventFlag(String eventFlag) {
        LambdaQueryWrapper<EventPermit> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EventPermit::getEventFlag, eventFlag);
        return list(wrapper);
    }

    @Transactional
    public void saveEventPermitList(List<EventPermit> eventPermitList) {
        Date now = new Date();
        eventPermitList.forEach(eventPermit -> baseMapper.insertMissing(eventPermit, now));
    }

    @Transactional
    public List<EventPermit> removeByTargetAndType(Long targetId, int type) {
        List<EventPermit> eventPermits = listByTargetAndType(targetId, type);
        LambdaQueryWrapper<EventPermit> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EventPermit::getTarget, targetId);
        wrapper.eq(EventPermit::getPermitType, type);
        remove(wrapper);
        return eventPermits;
    }

    public List<EventPermit> listByTargetAndType(Long targetId, int type) {
        LambdaQueryWrapper<EventPermit> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EventPermit::getTarget, targetId);
        wrapper.eq(EventPermit::getPermitType, type);
        return list(wrapper);
    }
}

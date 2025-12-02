package per.chowh.bot.ext.distribute.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import per.chowh.bot.core.permit.service.GroupService;
import per.chowh.bot.ext.distribute.domain.EventPermit;
import per.chowh.bot.ext.distribute.mapper.EventPermitMapper;
import org.springframework.stereotype.Service;

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
    @Autowired
    private EventPermitService self;

    public List<EventPermit> getByEventFlag(String eventFlag) {
        LambdaQueryWrapper<EventPermit> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EventPermit::getEventFlag, eventFlag);
        return list(wrapper);
    }
    public EventPermit getByTargetIdAndEventFlag(Long targetId, String eventFlag) {
        LambdaQueryWrapper<EventPermit> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EventPermit::getEventFlag, eventFlag);
        wrapper.eq(EventPermit::getTarget, targetId);
        return getOne(wrapper);
    }

    public void saveEventPermitList(List<EventPermit> eventPermitList) {
        eventPermitList.forEach(eventPermit -> {
            EventPermit curPermit = self.getByTargetIdAndEventFlag(
                    eventPermit.getTarget(), eventPermit.getEventFlag());
            if (curPermit != null) {
                eventPermit.setPermitId(curPermit.getPermitId());
            }
        });
        self.saveOrUpdateBatch(eventPermitList);
    }

    public List<EventPermit> removeByTargetAndType(Long targetId, int type) {
        List<EventPermit> eventPermits = listByTargetAndType(targetId, type);
        LambdaQueryWrapper<EventPermit> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EventPermit::getTarget, targetId);
        wrapper.eq(EventPermit::getPermitType, type);
        self.remove(wrapper);
        return eventPermits;
    }

    public List<EventPermit> listByTargetAndType(Long targetId, int type) {
        LambdaQueryWrapper<EventPermit> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EventPermit::getTarget, targetId);
        wrapper.eq(EventPermit::getPermitType, type);
        return list(wrapper);
    }
}





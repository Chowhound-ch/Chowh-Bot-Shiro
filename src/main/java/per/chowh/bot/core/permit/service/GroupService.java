package per.chowh.bot.core.permit.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import per.chowh.bot.core.permit.domain.Group;
import per.chowh.bot.core.permit.enums.GroupStatusEnum;
import per.chowh.bot.core.permit.mapper.GroupMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author hh825
* @description 针对表【group】的数据库操作Service实现
* @createDate 2025-08-01 13:56:29
*/
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
@CacheConfig(cacheNames = "group")
@Service
public class GroupService extends ServiceImpl<GroupMapper, Group> {
    @Autowired
    private GroupService self;

    @Cacheable(cacheNames = "groupList", key = "#statusEnum")
    public List<Group> getByGroupStateGe(GroupStatusEnum statusEnum) {
        LambdaQueryWrapper<Group> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(Group::getGroupStatus, statusEnum);
        return baseMapper.selectList(wrapper);
    }

    // TODO: ehcache返回的缓存中空值 hashcode不同于 Group.NULL
    @Cacheable(key = "#groupId")
    public Group getByGroupId(Long groupId) {
        LambdaQueryWrapper<Group> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Group::getGroupId, groupId);
        Group group = baseMapper.selectOne(wrapper);
        if (group == null) {
            return Group.NULL;
        }
        return group;
    }

    @CachePut(key = "#groupId")
    public Group updateState(Long groupId, GroupStatusEnum statusEnum) {
        Group group = self.getByGroupId(groupId);
        if (group.isNull()) {
            group = new Group();
            group.setGroupId(groupId);
        }
        group.setGroupStatus(statusEnum);
        this.saveOrUpdate(group);
        return group;
    }
}





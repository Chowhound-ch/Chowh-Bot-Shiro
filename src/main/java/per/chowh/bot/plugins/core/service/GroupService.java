package per.chowh.bot.plugins.core.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import per.chowh.bot.plugins.core.domain.Group;
import per.chowh.bot.plugins.core.enums.GroupStatusEnum;
import per.chowh.bot.plugins.core.mapper.GroupMapper;
import org.springframework.stereotype.Service;

import javax.cache.annotation.CacheValue;
import java.util.List;

/**
* @author hh825
* @description 针对表【group】的数据库操作Service实现
* @createDate 2025-08-01 13:56:29
*/
@CacheConfig(cacheNames = "group")
@Service
public class GroupService extends ServiceImpl<GroupMapper, Group> {

    @Cacheable(cacheNames = "groupList", key = "#statusEnum")
    public List<Group> getByGroupStateGe(GroupStatusEnum statusEnum) {
        LambdaQueryWrapper<Group> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(Group::getGroupStatus, statusEnum);
        return baseMapper.selectList(wrapper);
    }

    @Cacheable(key = "#groupId")
    public Group getByGroupId(Long groupId) {
        LambdaQueryWrapper<Group> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(Group::getGroupId, groupId);
        return baseMapper.selectOne(wrapper);
    }

}





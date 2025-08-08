package per.chowh.bot.plugins.core.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.transaction.annotation.Transactional;
import per.chowh.bot.plugins.core.domain.User;
import per.chowh.bot.plugins.core.enums.PermissionEnum;
import per.chowh.bot.plugins.core.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author hh825
* @description 针对表【user】的数据库操作Service实现
* @createDate 2025-08-01 10:31:36
*/
@Slf4j
@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
@CacheConfig(cacheNames = "user")
public class UserService extends ServiceImpl<UserMapper, User>{
    @Autowired
    private UserService self;

    @Cacheable(key = "#userId")
    public User getByUserId(Long userId) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUserId, userId);
        User user = baseMapper.selectOne(wrapper);
        return user == null ? User.NULL : user;
    }

    @Transactional
    public void updateOwner(Long userId) {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getRole, PermissionEnum.OWNER)
                .set(User::getRole, PermissionEnum.COMMON);
        baseMapper.update(wrapper);
        self.updateRole(userId, PermissionEnum.OWNER);
    }

    @CachePut(key = "#userId", unless = "#result == null")
    @Transactional
    public User updateRole(Long userId, PermissionEnum role) {
        User user = self.getByUserId(userId);
        if (user.isNull()) {
            user = new User();
            user.setUserId(userId);
        }
        user.setRole(role);

        this.saveOrUpdate(user);
        return user;
    }

    @Cacheable(keyGenerator = "listKeyGenerator")
    public List<User> getByUserIds(List<Long> userIds) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(User::getUserId, userIds);
        return baseMapper.selectList(wrapper);
    }
}





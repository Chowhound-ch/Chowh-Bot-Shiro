package per.chowhound.bot.plugins.core.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import per.chowhound.bot.plugins.core.domain.User;
import per.chowhound.bot.plugins.core.enums.PermissionEnum;
import per.chowhound.bot.plugins.core.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
* @author hh825
* @description 针对表【user】的数据库操作Service实现
* @createDate 2025-08-01 10:31:36
*/
@Slf4j
@Service
public class UserService extends ServiceImpl<UserMapper, User>{
    @Value("${per.bot.owner}")
    private Long owner;

    @PostConstruct
    @Transactional
    public void init(){
        User ownerUser = getOwner();
        if (ownerUser == null || !Objects.equals(ownerUser.getRole(), PermissionEnum.OWNER)) {
            updateOwner(owner);
            log.info("设置Owner为: {}", owner);
        }
    }


    public User getOwner() {
        return getByUserId(owner);
    }

    public User getByUserId(Long userId) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUserId, userId);
        return baseMapper.selectOne(wrapper);
    }

    @Transactional
    public void updateOwner(Long userId) {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getRole, PermissionEnum.OWNER)
                .set(User::getRole, PermissionEnum.COMMON);
        baseMapper.update(wrapper);
        updateRole(userId, PermissionEnum.OWNER);
    }

    @Transactional
    public void updateRole(Long userId, PermissionEnum role) {
        User user = getByUserId(userId);
        if (user == null) {
            user = new User();
            user.setUserId(userId);
        }
        user.setRole(role);

        this.save(user);
    }


}





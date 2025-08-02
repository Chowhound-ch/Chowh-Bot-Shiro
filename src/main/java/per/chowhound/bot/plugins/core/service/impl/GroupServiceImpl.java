package per.chowhound.bot.plugins.core.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import per.chowhound.bot.plugins.core.domain.Group;
import per.chowhound.bot.plugins.core.service.GroupService;
import per.chowhound.bot.plugins.core.mapper.GroupMapper;
import org.springframework.stereotype.Service;

/**
* @author hh825
* @description 针对表【group】的数据库操作Service实现
* @createDate 2025-08-01 13:56:29
*/
@Service
public class GroupServiceImpl extends ServiceImpl<GroupMapper, Group>
    implements GroupService{

}





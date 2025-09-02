package per.chowh.bot.core.registery.support;

import cn.hutool.core.util.ClassUtil;
import com.mikuac.shiro.dto.event.Event;
import org.springframework.core.annotation.Order;
import per.chowh.bot.core.bot.domain.ChowhBot;
import per.chowh.bot.core.registery.domain.EventMethod;
import per.chowh.bot.core.registery.domain.EventParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基础类型的ListenerArgumentResolver
 * @author : Chowhound
 * @since : 2025/8/29 - 13:49
 */
@Order(0)
public class BaseTypeListenerArgumentResolver implements ListenerArgumentResolver{


    @Override
    public boolean supportsParameter(EventParam parameter) {
        return ClassUtil.isBasicType(parameter.getParameter().getType());
    }

    @Override
    public Object resolveArgument(ChowhBot bot, Event event, EventParam parameter) {
        return null;
    }
}

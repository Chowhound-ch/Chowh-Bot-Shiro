package per.chowh.bot.core.registery.support;

import cn.hutool.core.util.ClassUtil;
import org.springframework.core.annotation.Order;
import per.chowh.bot.core.bot.domain.ChowhBot;
import per.chowh.bot.core.registery.domain.EventParam;
import per.chowh.bot.core.utils.EventWrapper;

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
    public Object resolveArgument(ChowhBot bot, EventWrapper eventWrapper, EventParam parameter) {
        return null;
    }
}

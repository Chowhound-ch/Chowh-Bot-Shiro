package per.chowh.bot.core.registery.support;

import cn.hutool.core.util.ClassUtil;
import org.springframework.core.Ordered;
import per.chowh.bot.core.bot.domain.ChowhBot;
import per.chowh.bot.core.model.event.Event;
import per.chowh.bot.core.registery.domain.EventParam;

/**
 * 基础类型的ListenerArgumentResolver
 * @author : Chowhound
 * @since : 2025/8/29 - 13:49
 */
public abstract class AbstractBaseTypeListenerArgumentResolver implements ListenerArgumentResolver, Ordered{



    @Override
    public boolean supportsParameter(EventParam parameter) {
        return ClassUtil.isBasicType(parameter.getParameter().getType());
    }

    @Override
    public Object resolveArgument(EventParam parameter, Event event, ChowhBot bot) throws Exception {
        return null;
    }
}

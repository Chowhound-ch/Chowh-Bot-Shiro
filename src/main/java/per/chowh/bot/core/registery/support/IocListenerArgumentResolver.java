package per.chowh.bot.core.registery.support;

import cn.hutool.core.util.ClassUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import per.chowh.bot.core.bot.domain.ChowhBot;
import per.chowh.bot.core.registery.domain.EventMethod;
import per.chowh.bot.core.registery.domain.EventParam;
import per.chowh.bot.core.utils.EventWrapper;

import java.lang.reflect.Parameter;
import java.util.Map;

/**
 * 默认最低优先级，从IOC获取参数
 * @author : Chowhound
 * @since : 2025/8/29 - 13:49
 */
@Order
@Component
public class IocListenerArgumentResolver implements ListenerArgumentResolver{
    private final ApplicationContext applicationContext;

    public IocListenerArgumentResolver(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public boolean supportsParameter(ChowhBot bot, EventMethod method, EventParam parameter) {
        // 非基础类型再获取
        return !ClassUtil.isBasicType(parameter.getParameter().getType()) && !String.class.equals(parameter.getParameter().getType());
    }

    @Override
    public Object resolveArgument(ChowhBot bot, EventMethod method, EventParam parameter, EventWrapper eventWrapper) {
        Parameter param = parameter.getParameter();
        Map<String, ?> beanMap = applicationContext.getBeansOfType(param.getType());
        if (beanMap.isEmpty()) {
            return null;
        }
        // 跟据Name获取，没有再获取第一个
        Object bean = beanMap.get(param.getName());
        return bean == null ? beanMap.values().stream().findFirst().orElse(null) : bean;
    }
}

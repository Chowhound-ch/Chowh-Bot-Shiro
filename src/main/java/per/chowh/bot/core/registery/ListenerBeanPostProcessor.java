package per.chowh.bot.core.registery;

import com.mikuac.shiro.dto.event.Event;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;
import per.chowh.bot.core.registery.annotation.EventListener;
import per.chowh.bot.core.registery.domain.EventMethod;
import per.chowh.bot.core.registery.domain.EventParam;
import per.chowh.bot.core.utils.ListenerUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : Chowhound
 * @since : 2025/8/28 - 17:33
 */
@Slf4j
@Configuration
public class ListenerBeanPostProcessor implements BeanPostProcessor {
    @Autowired
    private EventRegister eventRegister;

    @Override
    public Object postProcessAfterInitialization(Object bean, @NotNull String beanName) throws BeansException {
        Class<?> aClass = bean.getClass();
        Method[] methods = aClass.getDeclaredMethods();
        for (Method method : methods) {
            method.setAccessible(true);
            EventListener eventListener = method.getDeclaredAnnotation(EventListener.class);
            if (eventListener != null) {
                // 开始注册监听器
                List<EventParam> methodParameters = new ArrayList<>();
                EventMethod eventMethod = null;
                Parameter[] parameters = method.getParameters();
                for (Parameter methodParameter : parameters) {
                    Class<?> type = methodParameter.getType();
                    if (type.isAssignableFrom(Event.class)) {
                        if (eventMethod != null) {
                            throw new IllegalArgumentException("每个监听器仅支持监听一种Event");
                        }
                        // 检测到具体要监听的事件
                        //noinspection unchecked
                        eventMethod = new EventMethod((Class<? extends Event>) type, beanName, method, bean, methodParameters, eventListener);
                    }
                    // 除Event的参数，跟据Name解析，无法识别再从IOC获取
                    EventParam eventParam = new EventParam();
                    eventParam.setName(methodParameter.getName());
                    eventParam.setParameter(methodParameter);
                    methodParameters.add(eventParam);
                }
                if (eventMethod == null) {
                    log.warn("监听器[{}]注册错误：无法识别被监听事件类型", ListenerUtils.getListenerName(eventListener, method));
                    continue;
                }
                EventMethod finalEventMethod = eventMethod;
                methodParameters.forEach(param->param.setMethod(finalEventMethod));
                log.info("监听器[{}]注册成功：{}", ListenerUtils.getListenerName(eventListener, method), eventMethod.getEventClass().getName());
                eventRegister.register(eventMethod);
            }
        }
        return bean;
    }


}

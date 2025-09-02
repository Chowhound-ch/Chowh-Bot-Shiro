package per.chowh.bot.core.registery.config;

import lombok.Getter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import per.chowh.bot.core.registery.domain.EventParam;
import per.chowh.bot.core.registery.support.*;

/**
 * @author : Chowhound
 * @since : 2025/9/2 - 10:52
 */
@Getter
@Configuration
public class EventListenerConfig implements InitializingBean {
    private final ListenerArgumentResolverComposite argumentResolvers = new ListenerArgumentResolverComposite();
    private final ListenerReturnResolverComposite returnResolvers = new ListenerReturnResolverComposite();

    public void initArgumentResolvers() {
        argumentResolvers.addResolverLast(new EventArgumentResolver());
        argumentResolvers.addResolverLast(new ChowhBotArgumentResolver());
        argumentResolvers.addResolverLast(new BaseTypeListenerArgumentResolver());
        argumentResolvers.sort();
    }

    public void initReturnResolvers() {
        // 暂无
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        initArgumentResolvers();

        initReturnResolvers();
    }
}

package per.chowh.bot.core.registery.api.shiro;

import com.mikuac.shiro.annotation.AnyMessageHandler;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import per.chowh.bot.core.bot.domain.ChowhBot;
import per.chowh.bot.core.registery.api.AbstractListenerApi;
import per.chowh.bot.core.utils.EventWrapper;

/**
 * @author : Chowhound
 * @since : 2025/9/1 - 10:09
 */
@Slf4j
@Shiro
@Component
@ConditionalOnProperty()
public class ShiroApi extends AbstractListenerApi {

    @AnyMessageHandler
    public void anyMessageHandler(Bot bot, AnyMessageEvent event) {
        execute((ChowhBot) bot, new EventWrapper(event));
    }
}

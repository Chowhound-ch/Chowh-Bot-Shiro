package per.chowh.bot.ext.filter;

import com.mikuac.shiro.dto.event.message.MessageEvent;
import com.mikuac.shiro.enums.MsgTypeEnum;
import com.mikuac.shiro.model.ArrayMsg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import per.chowh.bot.core.bot.domain.ChowhBot;
import per.chowh.bot.core.registery.domain.EventMethod;
import per.chowh.bot.core.utils.EventWrapper;
import per.chowh.bot.ext.filter.annotation.EventFilter;

import java.util.List;

/**
 * @author : Chowhound
 * @since : 2025/9/5 - 14:42
 */
@Order
@Slf4j
@Component
public class MsgTypeEventFilterInterceptor extends AbstractEventFilterInterceptor {

    @Override
    public boolean preHandle(ChowhBot bot, EventMethod method, EventWrapper event) {
        EventFilter eventFilter = getEventFilter(method);
        if (eventFilter == null) {
            return true;
        }
        // 跟据msgTypes过滤
        MessageEvent messageEvent = event.toMessageEvent();
        List<ArrayMsg> arrayMsg = messageEvent.getArrayMsg();
        MsgTypeEnum[] types = eventFilter.types();
        for (ArrayMsg msg : arrayMsg) {
            for (MsgTypeEnum type : types) {
                if (msg.getType().equals(type)) {
                    return true;
                }
            }
        }
        return false;
    }
}

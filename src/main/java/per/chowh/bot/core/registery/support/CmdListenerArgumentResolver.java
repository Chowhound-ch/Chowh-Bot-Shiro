package per.chowh.bot.core.registery.support;

import cn.hutool.core.util.ClassUtil;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import per.chowh.bot.core.bot.domain.ChowhBot;
import per.chowh.bot.core.registery.annotation.EventListener;
import per.chowh.bot.core.registery.domain.EventMethod;
import per.chowh.bot.core.registery.domain.EventParam;
import per.chowh.bot.core.utils.EventWrapper;
import per.chowh.bot.utils.StringUtils;

import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 从cmd中解析出参数，仅支持基础类型
 * @author : Chowhound
 * @since : 2025/8/29 - 13:49
 */
@Order(10000)
@Component
public class CmdListenerArgumentResolver implements ListenerArgumentResolver{
    private static final Map<String, Pattern> patternCache = new ConcurrentHashMap<>();

    @Override
    public boolean supportsParameter(ChowhBot bot, EventMethod method, EventParam parameter) {
        return ClassUtil.isBasicType(parameter.getParameter().getType())
                || String.class.equals(parameter.getParameter().getType());
    }

    @Override
    public Object resolveArgument(ChowhBot bot, EventMethod method, EventParam parameter, EventWrapper eventWrapper) {
        EventListener listener = method.getEventListener();
        String msg = listener.isRaw() ? eventWrapper.getRawMessage() : eventWrapper.getMessage();
        Pattern pattern = patternCache.computeIfAbsent(listener.cmd(), Pattern::compile);
        Matcher matcher = eventWrapper.getMatcher();
        if (matcher == null) {
            matcher = pattern.matcher(msg);
            eventWrapper.setMatcher(matcher);
            if (!matcher.find()) {
                return null;
            }
        }

        Parameter param = parameter.getParameter();
        String arg = matcher.group(parameter.getName());

        return StringUtils.convertStringToType(arg, param.getType());
    }

}

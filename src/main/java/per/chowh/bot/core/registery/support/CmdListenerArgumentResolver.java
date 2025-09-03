package per.chowh.bot.core.registery.support;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.mikuac.shiro.common.utils.RegexUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import per.chowh.bot.core.bot.domain.ChowhBot;
import per.chowh.bot.core.registery.annotation.EventListener;
import per.chowh.bot.core.registery.domain.EventMethod;
import per.chowh.bot.core.registery.domain.EventParam;
import per.chowh.bot.core.utils.EventWrapper;

import java.lang.reflect.Parameter;
import java.util.List;
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
        return ClassUtil.isBasicType(parameter.getParameter().getType());
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
        }

        Parameter param = parameter.getParameter();
        String arg = matcher.group(parameter.getName());

        return null;
    }

    public static Object convertStringToType(String str, Class<?> targetType) {
        if (str == null) {
            return null;
        }
        if (targetType == Integer.class || targetType == int.class) {
            return Integer.parseInt(str);
        } else if (targetType == Double.class || targetType == double.class) {
            return Double.parseDouble(str);
        } else if (targetType == Boolean.class || targetType == boolean.class) {
            return Boolean.parseBoolean(str);
        } else if (targetType == Long.class || targetType == long.class) {
            return Long.parseLong(str);
        } else if (targetType == Float.class || targetType == float.class) {
            return Float.parseFloat(str);
        } else if (targetType == Short.class || targetType == short.class) {
            return Short.parseShort(str);
        } else if (targetType == Byte.class || targetType == byte.class) {
            return Byte.parseByte(str);
        } else if (targetType == Character.class || targetType == char.class) {
            if (str.length() == 1) {
                return str.charAt(0);
            } else {
                throw new IllegalArgumentException("String must be exactly 1 character long for char conversion");
            }
        } else if (targetType == String.class) {
            return str;
        } else {
            throw new IllegalArgumentException("Unsupported target type: " + targetType);
        }
    }
}

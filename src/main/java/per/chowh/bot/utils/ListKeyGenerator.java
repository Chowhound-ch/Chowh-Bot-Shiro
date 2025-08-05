package per.chowh.bot.utils;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.lang.NonNull;
import org.springframework.lang.NonNullApi;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * @author : Chowhound
 * @since : 2025/8/4 - 10:17
 */
@Component
public class ListKeyGenerator implements KeyGenerator {
    @Override
    @NonNull
    public Object generate(@NonNull Object target, @NonNull Method method, Object... params) {
        List<String> ids = Arrays.stream((Long[]) params[0])
                .sorted()
                .map(String::valueOf)
                .toList();
        return String.join(",", ids);
    }
}

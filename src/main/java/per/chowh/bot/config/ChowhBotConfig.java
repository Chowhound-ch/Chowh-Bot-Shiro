package per.chowh.bot.config;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.stream.StreamUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;
import per.chowh.bot.utils.ConfigUtils;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 读取springboot配置，范围仅限application.yml和application-dev.yml
 * @author : Chowhound
 * @since : 2025/9/5 - 9:49
 */
@Slf4j
public class ChowhBotConfig {
    private static final Map<String, Object> CONFIG = loadYaml(ConfigUtils.CONFIG_PATH + "application.yml");
    private static final Map<String, Object> CONFIG_DEV = loadYaml(ConfigUtils.CONFIG_PATH + "application-dev.yml");

    /**
     * 读取失败时返回空 Map，而不是让静态初始化直接抛异常。
     * 静态初始化抛异常只会得到 ExceptionInInitializerError，真正的失败原因很难定位。
     */
    @SuppressWarnings("unchecked")
    private static Map<String, Object> loadYaml(String name) {
        try (InputStream in = FileUtil.getInputStream(name)) {
            if (in == null) {
                log.error("找不到配置文件：{}", name);
                return Collections.emptyMap();
            }
            Object loaded = new Yaml().load(in);
            if (loaded instanceof Map<?, ?> map) {
                return (Map<String, Object>) map;
            }
            log.error("配置文件格式不正确（顶层不是 Map）：{}", name);
            return Collections.emptyMap();
        } catch (Exception e) {
            log.error("读取配置文件失败：{}", name, e);
            return Collections.emptyMap();
        }
    }

    public static String getConfig(String key){
        if (StrUtil.isBlank(key)) {
            log.warn("无法获取配置项：{}", key);
            return null;
        }

        Object value = getConfigValue(CONFIG, key);
        if (value == null){
            value = getConfigValue(CONFIG_DEV, key);
        }
        return paraseConfigValue(value == null ? null : value.toString());
    }

    private static String paraseConfigValue(String value){
        if (value == null) return null;
        int left = value.indexOf("${");
        if (left > -1) {
            int right = value.indexOf("}");
            if (right > left) {
                String key = value.substring(left + 2, right);
                Object configValue = getConfigValue(CONFIG, key);
                if (configValue == null) {
                    configValue = getConfigValue(CONFIG_DEV, key);
                }
                if (configValue == null) {
                    log.warn("无法获取具体配置项：{}", key);
                }
                value = value.replace("${" + key + "}", configValue == null ? "" : configValue.toString());
                return paraseConfigValue(value);
            }
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    private static Object getConfigValue(Map<String, Object> map, String key){
        if (key == null) {
            log.warn("无法获取配置项：null");
            return null;
        }
        List<String> keyPath = StrUtil.split(key, ".");
        Object target = map;
        for (String s : keyPath) {
            if (! (target instanceof Map<?,?>)) {
                return null;
            }
            target = ((Map<String, Object>)target).get(s);
        }
        return target;
    }
}

package per.chowh.bot.config;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.util.List;
import java.util.Map;

/**
 * 读取springboot配置，范围仅限application.yml和application-dev.yml
 * @author : Chowhound
 * @since : 2025/9/5 - 9:49
 */
@Slf4j
public class ChowhBotConfig {
    private static final Map<String, Object> CONFIG;
    private static final Map<String, Object> CONFIG_DEV;
    static {
        // 读取yml文件
        Yaml yaml = new Yaml();
        CONFIG = yaml.load(ChowhBotConfig.class.getClassLoader().getResourceAsStream("application.yml"));
        CONFIG_DEV = yaml.load(ChowhBotConfig.class.getClassLoader().getResourceAsStream("application-dev.yml"));
    }
    public static String getConfig(String key){
        if (StrUtil.isBlank(key)) {
            log.warn("无法获取配置项：{}", key);
        }

        Object value = getConfigValue(CONFIG, key);
        if (value == null){
            value = getConfigValue(CONFIG_DEV, key);
        }
        return paraseConfigValue((String) value);
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

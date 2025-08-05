package per.chowh.bot.utils;

import cn.hutool.core.util.ClassLoaderUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author : Chowhound
 * @since : 2025/7/31 - 15:16
 */
@Slf4j
public class ConfigUtils {
    public static final String CONFIG_PATH;

    static {
        String path = System.getProperty("user.dir") + File.separator + "config" + File.separator;//先读取config目录的，没有再加载classpath的
        if (Files.exists(Path.of(path))) {
            log.info("命令执行路径下CONFIG：{}", path);
        } else {
            log.info("命令执行路径下CONFIG不存在：{}", path);
            URL url = ClassLoaderUtil.getClassLoader().getResource("/");
            if (url != null) {
                path = url.getPath();
            }
        }
        log.info("CONFIG_PATH: {}", path);
        CONFIG_PATH =  path;
    }
}

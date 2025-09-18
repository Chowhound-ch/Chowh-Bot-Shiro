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
        String path = System.getProperty("user.dir") + File.separator;//先读取config目录的，没有再加载classpath的
        String desPath;
        if (Files.exists(Path.of(desPath = path + "config" + File.separator))) {
            //noinspection LoggingSimilarMessage
            log.info("命令执行路径下CONFIG存在：{}", desPath);
        } else if (Files.exists(Path.of(desPath = path +
                "src" + File.separator +
                "main" + File.separator  +
                "resources" + File.separator))) {
            //noinspection LoggingSimilarMessage
            log.info("命令执行路径下RESOURCE存在：{}", desPath);
        } else {
            log.info("命令执行路径下CONFIG、RESOURCE不存在：{}", desPath);
            URL url = ClassLoaderUtil.getClassLoader().getResource("/");
            if (url != null) {
                desPath = url.getPath();
            }
        }
        log.info("CONFIG_PATH: {}", desPath);
        CONFIG_PATH =  desPath;
    }
}

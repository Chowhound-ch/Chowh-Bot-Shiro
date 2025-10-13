package per.chowh.bot.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author : Chowhound
 * @since : 2025/10/13 - 17:26
 */
public class FileUtils {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static String getPathWithDate(String basePath) {
        String format = dateFormat.format(new Date());
        Path path = Path.of(basePath + File.separatorChar + format);
        try {
            if (!Files.exists(path)) {
                Files.createDirectory(path);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return path.toString();
    }
}

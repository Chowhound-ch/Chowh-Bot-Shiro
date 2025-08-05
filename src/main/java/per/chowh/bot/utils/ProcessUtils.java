package per.chowh.bot.utils;

import cn.hutool.core.io.IoUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : Chowhound
 * @since : 2025/7/31 - 10:51
 */
@Slf4j
public class ProcessUtils {
    public static String exec(ProcessBuilder processBuilder){
        return String.join("", execLines(processBuilder));
    }

    public static List<String> execLines(ProcessBuilder processBuilder){
        List<String> result = new ArrayList<>();
        Process process = null;
        try {
            log.info("Executing command: {}", processBuilder.command());
            process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine())!= null) {
                result.add(line);
            }
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                log.error("Python script exited with code: {}", exitCode);
                throw new IOException("Python script exited with code: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            log.error(e.getMessage(), e);
            if (process != null) {
                log.error(IoUtil.read(process.getErrorStream(), StandardCharsets.UTF_8));
            }
        }
        return result;
    }
}

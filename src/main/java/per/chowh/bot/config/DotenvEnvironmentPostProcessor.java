package per.chowh.bot.config;

import org.apache.commons.logging.Log;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.logging.DeferredLogFactory;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.StandardEnvironment;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Loads {@code .env} files into the Spring {@link ConfigurableEnvironment} before the
 * application context is created.
 *
 * <p>Files are looked up in the working directory and in {@code ./config}, in this order
 * (later files override earlier ones):
 * <ol>
 *     <li>{@code ./config/.env} then {@code ./.env}</li>
 *     <li>{@code ./config/.env.<profile>} then {@code ./.env.<profile>}</li>
 * </ol>
 *
 * <p>Supported syntax: {@code KEY=VALUE}, optional {@code export} prefix, {@code #}
 * comments, single quoted (literal) and double quoted (escaped) values, and
 * {@code ${VAR}} / {@code ${VAR:-default}} expansion.
 *
 * <p>Optional properties: {@code dotenv.enabled=false} disables the loader,
 * {@code dotenv.profile} selects a profile explicitly and {@code dotenv.files} replaces
 * the profile based lookup with an explicit comma separated list of files.
 */
public class DotenvEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    private static final String PROPERTY_SOURCE_NAME = "dotenv";

    private static final String BASE_FILE_NAME = ".env";

    private static final String DEFAULT_PROFILE = "dev";

    private static final String CONFIG_DIRECTORY = "config";

    private static final String ENABLED_PROPERTY = "per.dotenv.enabled";

    private static final String PROFILE_PROPERTY = "per.dotenv.profile";

    private static final String FILES_PROPERTY = "per.dotenv.files";


    private static final Pattern KEY_PATTERN = Pattern.compile("[A-Za-z_][A-Za-z0-9_.]*");

    private final Log log;

    public DotenvEnvironmentPostProcessor(DeferredLogFactory logFactory) {
        this.log = logFactory.getLog(getClass());
    }

    /**
     * 顺序要在ConfigDataEnvironmentPostProcessor（Ordered.HIGHEST_PRECEDENCE + 10）之后，确保配置文件加载顺序
     *
     */
    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 100;
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, @NonNull SpringApplication application) {
        if (!environment.getProperty(ENABLED_PROPERTY, Boolean.class, true)) {
            return;
        }
        List<Path> files = resolveFiles(environment);
        if (files.isEmpty()) {
            log.debug("未找到 dotenv 文件，使用常规 Spring 配置");
            return;
        }
        Map<String, String> values = new LinkedHashMap<>();
        for (Path file : files) {
            load(file, environment, values);
        }
        if (values.isEmpty()) {
            return;
        }
        MapPropertySource source = new MapPropertySource(PROPERTY_SOURCE_NAME, new LinkedHashMap<>(values));
        MutablePropertySources sources = environment.getPropertySources();
        sources.remove(PROPERTY_SOURCE_NAME);
        if (sources.contains(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME)) {
            // Keep command line arguments and system environment variables ahead of the
            // file, so a single run can still override a value from the shell.
            sources.addAfter(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, source);
        } else {
            sources.addLast(source);
        }
        log.info("已从 dotenv 文件加载 " + values.size() + " 个配置项：" + files);
    }

    private List<Path> resolveFiles(ConfigurableEnvironment environment) {
        String configured = environment.getProperty(FILES_PROPERTY);
        if (configured != null && !configured.isBlank()) {
            List<Path> files = new ArrayList<>();
            for (String name : configured.split(",")) {
                if (name.isBlank()) {
                    continue;
                }
                Path path = Paths.get(name.trim()).toAbsolutePath().normalize();
                if (Files.isRegularFile(path)) {
                    files.add(path);
                } else {
                    log.warn("配置的 dotenv 文件不存在：" + path);
                }
            }
            return files;
        }

        List<String> names = new ArrayList<>();
        names.add(BASE_FILE_NAME);
        for (String profile : resolveProfiles(environment)) {
            names.add(BASE_FILE_NAME + "." + profile);
        }

        List<Path> files = new ArrayList<>();
        for (String name : names) {
            files.addAll(findInWorkingDirectory(name));
        }
        return files;
    }

    private List<String> resolveProfiles(ConfigurableEnvironment environment) {
        List<String> profiles = new ArrayList<>();
        String configured = environment.getProperty(PROFILE_PROPERTY);
        if (configured != null && !configured.isBlank()) {
            for (String name : configured.split(",")) {
                if (!name.isBlank()) {
                    profiles.add(name.trim());
                }
            }
            return profiles;
        }
        for (String active : environment.getActiveProfiles()) {
            if (!active.isBlank()) {
                profiles.add(active);
            }
        }
        if (profiles.isEmpty()) {
            profiles.add(DEFAULT_PROFILE);
        }
        return profiles;
    }

    private List<Path> findInWorkingDirectory(String fileName) {
        Path workingDirectory = Paths.get("").toAbsolutePath();
        List<Path> candidates = List.of(workingDirectory.resolve(fileName),
                workingDirectory.resolve(CONFIG_DIRECTORY).resolve(fileName));
        List<Path> found = new ArrayList<>();
        for (Path candidate : candidates) {
            if (Files.isRegularFile(candidate)) {
                found.add(candidate.normalize());
            }
        }
        return found;
    }

    private void load(Path file, ConfigurableEnvironment environment, Map<String, String> values) {
        try {
            List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
            for (int index = 0; index < lines.size(); index++) {
                String line = lines.get(index);
                if (index == 0) {
                    line = stripBom(line);
                }
                parseLine(line, index + 1, file, environment, values);
            }
        } catch (IOException ex) {
            log.warn("读取 dotenv 文件失败：" + file + "，" + ex.getMessage());
        }
    }

    private void parseLine(String rawLine, int lineNumber, Path file,
                           ConfigurableEnvironment environment, Map<String, String> values) {
        String line = rawLine.trim();
        if (line.isEmpty() || line.startsWith("#")) {
            return;
        }
        if (line.startsWith("export ")) {
            line = line.substring("export ".length()).trim();
        }
        int separator = line.indexOf('=');
        if (separator <= 0) {
            log.warn("跳过无效行（第 " + lineNumber + " 行，文件 " + file + "）：" + rawLine);
            return;
        }
        String key = line.substring(0, separator).trim();
        if (!KEY_PATTERN.matcher(key).matches()) {
            log.warn("跳过第 " + lineNumber + " 行（文件 " + file + "）：无效的键名 '" + key + "'");
            return;
        }
        values.put(key, parseValue(line.substring(separator + 1).trim(), environment, values));
    }

    private String parseValue(String rawValue, ConfigurableEnvironment environment, Map<String, String> values) {
        if (rawValue.startsWith("'")) {
            int end = rawValue.lastIndexOf('\'');
            if (end > 0) {
                // Single quotes are literal: no escapes and no variable expansion.
                return rawValue.substring(1, end);
            }
            log.warn("单引号值未闭合：" + rawValue);
            return rawValue;
        }
        if (rawValue.startsWith("\"")) {
            int end = rawValue.lastIndexOf('"');
            if (end > 0) {
                return expand(unescapeDoubleQuoted(rawValue.substring(1, end)), environment, values);
            }
            log.warn("双引号值未闭合：" + rawValue);
            return rawValue;
        }
        return expand(stripInlineComment(rawValue).trim(), environment, values);
    }

    private String stripInlineComment(String value) {
        for (int index = 0; index < value.length(); index++) {
            if (value.charAt(index) == '#' && (index == 0 || Character.isWhitespace(value.charAt(index - 1)))) {
                return value.substring(0, index);
            }
        }
        return value;
    }

    private String unescapeDoubleQuoted(String value) {
        StringBuilder result = new StringBuilder(value.length());
        for (int index = 0; index < value.length(); index++) {
            char current = value.charAt(index);
            if (current != '\\' || index + 1 >= value.length()) {
                result.append(current);
                continue;
            }
            char next = value.charAt(++index);
            switch (next) {
                case 'n' -> result.append('\n');
                case 'r' -> result.append('\r');
                case 't' -> result.append('\t');
                case '"' -> result.append('"');
                case '\\' -> result.append('\\');
                case '$' -> result.append('$');
                default -> result.append('\\').append(next);
            }
        }
        return result.toString();
    }

    private String expand(String value, ConfigurableEnvironment environment, Map<String, String> values) {
        if (!value.contains("${")) {
            return value;
        }
        StringBuilder result = new StringBuilder(value.length());
        int index = 0;
        while (index < value.length()) {
            int start = value.indexOf("${", index);
            if (start < 0) {
                result.append(value, index, value.length());
                break;
            }
            result.append(value, index, start);
            int end = value.indexOf('}', start + 2);
            if (end < 0) {
                result.append(value, start, value.length());
                break;
            }
            String expression = value.substring(start + 2, end);
            String fallback = null;
            int defaultSeparator = expression.indexOf(":-");
            if (defaultSeparator >= 0) {
                fallback = expression.substring(defaultSeparator + 2);
                expression = expression.substring(0, defaultSeparator);
            }
            String replacement = values.get(expression);
            if (replacement == null) {
                replacement = environment.getProperty(expression);
            }
            if (replacement != null) {
                result.append(replacement);
            } else if (fallback != null) {
                result.append(fallback);
            }
            index = end + 1;
        }
        return result.toString();
    }

    private static String stripBom(String line) {
        return line.startsWith("\uFEFF") ? line.substring(1) : line;
    }
}

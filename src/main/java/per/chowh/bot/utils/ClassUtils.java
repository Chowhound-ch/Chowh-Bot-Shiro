package per.chowh.bot.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : Chowhound
 * @since : 2025/8/20 - 11:07
 */
@Slf4j
public class ClassUtils {

    public static List<Class<?>> getClasses(String packageName) {
        // 第一个class类的集合
        List<Class<?>> classes = new ArrayList<>();
        SimpleMetadataReaderFactory readerFactory = new SimpleMetadataReaderFactory();
        // 通过名称加载
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        // 加載資源    classpath*:com/ruoyi/**/*.class : 找环境变量下的 com/ruoyi 所有.class文件
        Resource[] resources = null;
        try {
            resources = resolver.getResources("classpath*:" + packageName + "/**/*.class");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        if (resources == null) {
            log.warn("无法识别到路径下的WsMsgListener：{}",  packageName);
            return classes;
        }
        for(Resource res :resources) {
            try {
                String className = readerFactory.getMetadataReader(res).getClassMetadata().getClassName();
                classes.add(Class.forName(className));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return classes;
    }
}

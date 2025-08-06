package per.chowh.bot.config;

import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.core.EhcacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author : Chowhound
 * @since : 2025/8/6 - 13:53
 */
@Configuration
public class EhcacheConfig {

    @Bean
    public CacheManager ehcacheManager(){
        return CacheManagerBuilder.newCacheManagerBuilder()
                .build(true);
    }
}

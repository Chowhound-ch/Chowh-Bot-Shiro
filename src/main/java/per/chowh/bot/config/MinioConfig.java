package per.chowh.bot.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "per.minio", name = "enabled", havingValue = "true")
public class MinioConfig {
    @Bean
    MinioClient minioClient(@Value("${per.minio.endpoint}") String endpoint,
                            @Value("${per.minio.access-key}") String accessKey,
                            @Value("${per.minio.secret-key}") String secretKey) {
        Assert.hasText(endpoint, "BOT_MINIO_ENDPOINT must be configured when MinIO is enabled");
        Assert.hasText(accessKey, "BOT_MINIO_ACCESS_KEY must be configured when MinIO is enabled");
        Assert.hasText(secretKey, "BOT_MINIO_SECRET_KEY must be configured when MinIO is enabled");
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
}

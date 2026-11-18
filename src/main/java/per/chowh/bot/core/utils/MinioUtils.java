package per.chowh.bot.core.utils;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.MinioException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.web.util.UriUtils;
import per.chowh.bot.utils.ConfigUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
public class MinioUtils {
    private static final String ENDPOINT_PLACEHOLDER = "#{BOT_MINIO_ENDPOINT}";
    private static String CUR_ENDPOINT_PLACEHOLDER;
    public static final String DEFAULT_BUCKET_NAME = "chowh-bot";

    private static final long UPLOAD_PART_SIZE = 5L * 1024 * 1024;

    private static MinioClient client;

    static {
        init();
    }

    protected static boolean init() {
        CUR_ENDPOINT_PLACEHOLDER = ConfigUtils.getConfig("per.minio.endpoint");
        String CUR_ACCESS_KEY = ConfigUtils.getConfig("per.minio.access-key");
        String CUR_SECRET_KEY = ConfigUtils.getConfig("per.minio.secret-key");
        if (CUR_ENDPOINT_PLACEHOLDER != null && CUR_ACCESS_KEY != null && CUR_SECRET_KEY != null) {
            client = MinioClient.builder()
                    .endpoint(CUR_ENDPOINT_PLACEHOLDER) // MinIO 服务地址
                    .credentials(CUR_ACCESS_KEY,
                            CUR_SECRET_KEY) // 账号和密码
                    .build();
            return true;
        }
        return false;
    }


    public static void uploadObject(String bucket, String objectName, InputStream stream, long size, String contentType)
            throws MinioException {
        getClient().putObject(PutObjectArgs.builder()
                .bucket(bucket)
                .object(objectName)
                .contentType(contentType)
                .stream(stream, size, UPLOAD_PART_SIZE)
                .build());
    }

    public static String uploadObject(String objectName, byte[] bytes)
            throws MinioException {
        getClient().putObject(PutObjectArgs.builder()
                .bucket(DEFAULT_BUCKET_NAME)
                .object(objectName)
                .data(bytes, bytes.length)
                .build());
        return objectUrl(objectName);
    }


    public static void deleteObject(String bucket, String objectName) throws MinioException {
        getClient().removeObject(RemoveObjectArgs.builder().bucket(bucket).object(objectName).build());
    }

    public static void deleteObject(String objectName) throws MinioException {
        getClient().removeObject(RemoveObjectArgs.builder().bucket(DEFAULT_BUCKET_NAME).object(objectName).build());
    }

    public static String objectUrlTemplate(String bucket, String objectName) {
        String path = UriUtils.encodePath("/" + bucket + "/" + objectName, StandardCharsets.UTF_8);
        return ENDPOINT_PLACEHOLDER + path;
    }

    public static String objectUrlTemplate(String objectName) {
        String path = UriUtils.encodePath("/" + DEFAULT_BUCKET_NAME + "/" + objectName, StandardCharsets.UTF_8);
        return ENDPOINT_PLACEHOLDER + path;
    }

    public static String objectUrl(String bucket, String objectName) {
        String path = UriUtils.encodePath("/" + bucket + "/" + objectName, StandardCharsets.UTF_8);
        return CUR_ENDPOINT_PLACEHOLDER + path;
    }

    public static String objectUrl(String objectName) {
        return objectUrl(DEFAULT_BUCKET_NAME, objectName);
    }

    public static MinioClient getClient() {
        if (client == null) {
            init();
        }
        return client;
    }
}

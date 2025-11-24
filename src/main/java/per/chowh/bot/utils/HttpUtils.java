package per.chowh.bot.utils;

import cn.hutool.core.util.URLUtil;
import com.fasterxml.jackson.databind.JavaType;
import okhttp3.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.Map;

/**
 * @author : Chowhound
 * @since : 2025/8/8 - 11:33
 */
@SuppressWarnings("unused")
public class HttpUtils {
    private static final OkHttpClient BASE_CLIENT;
    public static final MediaType JSON = MediaType.get("application/json");
    static {
        BASE_CLIENT = new OkHttpClient.Builder()
                .callTimeout(Duration.ofSeconds(120))
                .connectTimeout(Duration.ofSeconds(120))
                .readTimeout(Duration.ofSeconds(120))
                .writeTimeout(Duration.ofSeconds(120)).build();
    }


    public static String doGetStr(String url) throws IOException {
        return doGetStr(BASE_CLIENT, url, null);
    }
    public static byte[] doGetBytes(String url) throws IOException {
        return doGetBytes(BASE_CLIENT, url, null);
    }
    public static String doGetStr(OkHttpClient client, String url) throws IOException {
        return doGetStr(client, url, null);
    }
    public static String doGetStr(OkHttpClient client, String url, Map<String, ?> params) throws IOException {
        try (Response resp = doGet(client, url, params)) {
            return resp.body().string();
        }
    }

    public static byte[] doGetBytes(OkHttpClient client, String url, Map<String, ?> params) throws IOException {
        try (Response resp = doGet(client, url, params)) {
            return resp.body().bytes();
        }
    }

    public static Response doGet(OkHttpClient client, String url, Map<String, ?> params) throws IOException {
        Request request = new Request.Builder()
                .url(getUrl(url, params))
                .build();
        return (client == null? BASE_CLIENT: client).newCall(request).execute();
    }

    public static String doPostStr(String url, String jsonBody) throws IOException {
        return doPostStr(BASE_CLIENT, url, jsonBody);
    }
    public static String doPostStr(OkHttpClient client, String url, String jsonBody) throws IOException {
        return doPostStr(client, url, null, jsonBody);
    }
    public static String doPostStr(OkHttpClient client, String url, Map<String, ?> params, String jsonBody) throws IOException {
        RequestBody body = RequestBody.create(jsonBody, JSON);
        Request request = new Request.Builder()
                .url(getUrl(url, params))
                .post(body)
                .build();
        try (Response response = (client == null? BASE_CLIENT: client).newCall(request).execute()) {
            return response.body().string();
        }
    }

    public static <T> T doPost(OkHttpClient client, String url, String jsonBody, Class<T> tClass, Class<?> ... genericClasses) throws IOException {
        return  doPost(client, url, null, jsonBody, tClass, genericClasses);
    }
    public static <T> T doPost(OkHttpClient client, String url, Map<String, ?> params, String jsonBody, Class<T> tClass, Class<?> ... genericClasses) throws IOException {
        return JacksonUtil.readValue(doPostStr(client, url, params, jsonBody), JacksonUtil.getGenericJavaType(tClass, genericClasses));
    }

    public static <T> T doPost(OkHttpClient client, String url, Map<String, ?> params, String jsonBody, JavaType javaType) throws IOException {
        return JacksonUtil.readValue(doPostStr(client, url, params, jsonBody), javaType);
    }

    /**
     * 获取url
     */
    private static String getUrl(String url, Map<String, ?> params){
        if (params != null && !params.isEmpty()) {
            url = url + "?" + URLUtil.buildQuery(params, Charset.defaultCharset());
        }
        return url;
    }
}

package per.chowh.bot.config;

import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import org.springframework.ai.mcp.customizer.McpClientCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;

@Configuration(proxyBeanMethods = false)
public class McpClientConfig {

    @Bean
    McpClientCustomizer<HttpClientStreamableHttpTransport.Builder> mcpApiKeyCustomizer(
            @Value("${BOT_MCP_API_KEY:}") String apiKey,
            @Value("${BOT_MCP_USE_PROXY:false}") boolean useProxy,
            Environment environment) {
        return (connectionName, builder) -> {
            if (!"primary".equals(connectionName)) {
                return;
            }
            if (!apiKey.isBlank()) {
                builder.requestBuilder(HttpRequest.newBuilder().header("X-API-Key", apiKey));
            }
            URI serverUrl = environment.getProperty(
                    "spring.ai.mcp.client.streamable-http.connections.primary.url", URI.class);
            if (serverUrl != null && "http".equalsIgnoreCase(serverUrl.getScheme())) {
                // 避免明文 HTTP 的 h2c 升级触发服务端丢失 POST 请求体。
                builder.customizeClient(client -> client.version(HttpClient.Version.HTTP_1_1));
            }
            if (useProxy) {
                String host = environment.getRequiredProperty("BOT_PROXY_HOST");
                int port = environment.getRequiredProperty("BOT_PROXY_PORT", Integer.class);
                ProxySelector proxy = ProxySelector.of(new InetSocketAddress(host, port));
                builder.customizeClient(client -> client.proxy(proxy));
            }
        };
    }
}

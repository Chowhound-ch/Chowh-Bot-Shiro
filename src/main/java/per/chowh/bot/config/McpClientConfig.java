package per.chowh.bot.config;

import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import org.springframework.ai.mcp.customizer.McpClientCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.net.InetSocketAddress;
import java.net.ProxySelector;
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
            if (useProxy) {
                String host = environment.getRequiredProperty("BOT_PROXY_HOST");
                int port = environment.getRequiredProperty("BOT_PROXY_PORT", Integer.class);
                ProxySelector proxy = ProxySelector.of(new InetSocketAddress(host, port));
                builder.customizeClient(client -> client.proxy(proxy));
            }
        };
    }
}

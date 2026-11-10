package per.chowh.bot.config;

import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import org.springframework.ai.mcp.customizer.McpClientCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpRequest;

@Configuration(proxyBeanMethods = false)
public class McpClientConfig {

    @Bean
    McpClientCustomizer<HttpClientStreamableHttpTransport.Builder> mcpApiKeyCustomizer(
            @Value("${BOT_MCP_API_KEY:}") String apiKey) {
        return (connectionName, builder) -> {
            if ("primary".equals(connectionName) && !apiKey.isBlank()) {
                builder.requestBuilder(HttpRequest.newBuilder().header("X-API-Key", apiKey));
            }
        };
    }
}

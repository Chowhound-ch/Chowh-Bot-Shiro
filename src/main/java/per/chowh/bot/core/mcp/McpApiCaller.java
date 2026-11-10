package per.chowh.bot.core.mcp;

import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import per.chowh.bot.utils.JacksonUtil;
import per.chowh.bot.utils.StringUtils;

import java.util.List;
import java.util.Map;

@Component
@ConditionalOnProperty(prefix = "spring.ai.mcp.client", name = "enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnProperty(prefix = "spring.ai.mcp.client", name = "type", havingValue = "SYNC", matchIfMissing = true)
public class McpApiCaller implements ApplicationRunner {
    // Spring Bean 内
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Resource(name = "mcpSyncClients")
    private List<McpSyncClient> mcpClients;

    public static McpSyncClient DEFAULT_MCP_CLIENT;

    @PostConstruct
    public void init() {
        DEFAULT_MCP_CLIENT = mcpClients.getFirst();
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {
        McpSchema.CallToolResult tool = mcpClients.getFirst().callTool(
                new McpSchema.CallToolRequest(
                        "api_fixed_api",
                        Map.of("kwargs", "{}")
                )
        );
        System.out.println(JacksonUtil.toJsonString(tool));
    }

}

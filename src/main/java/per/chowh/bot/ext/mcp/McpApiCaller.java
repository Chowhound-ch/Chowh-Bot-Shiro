package per.chowh.bot.ext.mcp;

import io.modelcontextprotocol.client.McpSyncClient;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 由于会把接口调用拆解到mcp服务器，不方便维护，暂时放弃
 */

@Component
@ConditionalOnProperty(prefix = "spring.ai.mcp.client", name = "enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnProperty(prefix = "spring.ai.mcp.client", name = "type", havingValue = "SYNC", matchIfMissing = true)
public class McpApiCaller {
    // Spring Bean 内
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Resource(name = "mcpSyncClients")
    private List<McpSyncClient> mcpClients;

    public static McpSyncClient DEFAULT_MCP_CLIENT;

    @PostConstruct
    public void init() {
        DEFAULT_MCP_CLIENT = mcpClients.getFirst();
    }


//    @Override
//    public void run(ApplicationArguments args) throws Exception {
////        McpSchema.CallToolResult tool = mcpClients.getFirst().callTool(
////                new McpSchema.CallToolRequest(
////                        "api_fixed_api",
////                        Map.of("kwargs", "{}")
////                )
////        );
////        System.out.println(JacksonUtil.toJsonString(tool));
//    }

}

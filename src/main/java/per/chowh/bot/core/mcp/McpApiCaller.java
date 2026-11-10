package per.chowh.bot.core.mcp;

import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
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
        mcpClients.getFirst().callTool(
                new McpSchema.CallToolRequest(
                        "api_fixed_api",
                        Map.of("kwargs", "{}")
                )
        );
    }

}

//import io.modelcontextprotocol.client.McpClient;
//import io.modelcontextprotocol.client.McpSyncClient;
//import org.springframework.ai.chat.client.ChatClient;
//import org.springframework.ai.mcp.AsyncMcpToolCallback;
//import org.springframework.ai.tool.ToolCallback;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.client.RestClient;
//
//import java.util.List;
//
//@Configuration
//public class McpClientConfig {
//
//    @Bean
//    public McpSyncClient timeMcpClient(RestClient.Builder restClientBuilder) {
//
//        var transport = new HttpMcpTransport(   // ← 不再是 ClientHttpTransport
//                restClientBuilder.baseUrl("http://localhost:8081").build(),
//                "/mcp/message"
//        );
//
//        var client = McpClient.sync(transport).build();
//        client.initialize();
//        return client;
//    }
//
//    @Bean
//    public ChatClient chatClient(ChatClient.Builder builder, McpSyncClient client) {
//
//        var toolList = client.listTools();
//
//        List<ToolCallback> callbacks = toolList.tools().stream()
//                .map(t -> new AsyncMcpToolCallback(client, t))
//                .map(cb -> (ToolCallback) cb)
//                .toList();
//
//        return builder.defaultTools(callbacks).build();
//    }
//}

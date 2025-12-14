package com.test.mcpserver.tool_config;

import com.test.mcpserver.tool.CurrentTime;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

@Configuration
public class ToolsConfig {

    @Bean
    ToolCallbackProvider toolCallbackProvider(CurrentTime currentTime) {
        return MethodToolCallbackProvider.builder().toolObjects(currentTime).build();
    }
}
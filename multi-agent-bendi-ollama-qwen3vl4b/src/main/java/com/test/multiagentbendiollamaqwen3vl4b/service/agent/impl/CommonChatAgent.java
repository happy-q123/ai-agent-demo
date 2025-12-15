package com.test.multiagentbendiollamaqwen3vl4b.service.agent.impl;

import com.test.multiagentbendiollamaqwen3vl4b.service.agent.AbstractAgentService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
/**
 * description 普通无记忆对话agent
 * author zzq
 * date 2025/12/14 22:17
 */
@Service
public class CommonChatAgent extends AbstractAgentService {

    public CommonChatAgent(ChatClient.Builder chatClientBuilder) {
        super(chatClientBuilder);
    }

    @Override
    protected void initProperties() {
        agentDescription="用于简单对话的agent";
        agentName="CommonChatAgent";
        systemPrompt="你是一个简单的对话助手，请尽量减少思考时间，并直接回复用户问题，无需说明答案来源。";
    }

    @Override
    protected void initChatClient(ChatClient.Builder chatClientBuilder) {
        chatClient=chatClientBuilder
                .defaultSystem(systemPrompt)
                .build();
    }

    @Override
    public Object execute(String input) {
        return chatClient.prompt(input).call().content();
    }

}

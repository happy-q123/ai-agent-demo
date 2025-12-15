package com.test.multiagentbendiollamaqwen3vl4b.service.agent.impl;

import com.test.customrerank.service.ZhiPuRerankService;
import com.test.multiagentbendiollamaqwen3vl4b.advisor.ConversationIdAdvisor;
import com.test.multiagentbendiollamaqwen3vl4b.advisor.HistorySearchAdvisor;
import com.test.multiagentbendiollamaqwen3vl4b.advisor.InformationAdvisor;
import com.test.multiagentbendiollamaqwen3vl4b.advisor.ReRankAdvisor;
import com.test.multiagentbendiollamaqwen3vl4b.service.agent.AbstractAgentService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatMemoryAgent extends AbstractAgentService {
    @Resource
    VectorStore vectorStore;

    @Resource
    ZhiPuRerankService zhiPuRerankService;

    //如果参数名字为ollamaChatClientBuilder，则可以删掉@Qualifier("ollamaChatClientBuilder")的声明
    public ChatMemoryAgent(@Qualifier("ollamaChatClientBuilder") ChatClient.Builder ollamaChatClientBuilder) {
        super(ollamaChatClientBuilder);
    }

    @Override
    protected void initAdvisors() {
        if (advisors==null)
            advisors=new ArrayList<>();

        //会话ID advisor
        ConversationIdAdvisor conversationIdAdvisor = new ConversationIdAdvisor(0);
        advisors.add(conversationIdAdvisor);

        //历史搜索advisor
        HistorySearchAdvisor historySearchAdvisor = HistorySearchAdvisor.builder(vectorStore)
                .defaultTopK(10)
                .order(2)
                .persistAndFilter("conversationId", "messageSource")
                .build();
        advisors.add(historySearchAdvisor);

        //本地知识库搜索，并上下文重新排序advisor
        ReRankAdvisor reRankAdvisor = new ReRankAdvisor(zhiPuRerankService, vectorStore, 4);
        advisors.add(reRankAdvisor);

        //信息打印advisor
        InformationAdvisor informationAdvisor = new InformationAdvisor(5);
        advisors.add(informationAdvisor);
    }

    @Override
    protected void initProperties() {
        agentName="ChatMemoryAgent";
        agentDescription="能够记忆的的agent";
        systemPrompt="请尽量减少思考用时，回答时直接给出答案即可，不要回复如答案来源等无关的内容。";
    }

    @Override
    public Object execute(String query) {
        return chatClient.prompt(query).call().chatClientResponse();
    }

    @Override
    public Object execute(String query, ChatOptions chatOptions) {
        return chatClient.prompt(query).options(chatOptions).call().chatClientResponse();
    }
}

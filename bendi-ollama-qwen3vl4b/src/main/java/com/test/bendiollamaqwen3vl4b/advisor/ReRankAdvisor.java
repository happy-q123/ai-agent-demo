package com.test.bendiollamaqwen3vl4b.advisor;

import com.test.customrerank.dto.RerankResponse;
import com.test.customrerank.dto.Result;
import com.test.customrerank.service.ZhiPuRerankService;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReRankAdvisor implements BaseAdvisor {

    private final int order;

    //向量数据库对象，封装了向量数据库的搜索、增删改查
    private final VectorStore vectorStore;

    //封装了对zhipu的rank模型调用
    private final ZhiPuRerankService rerankService;

    //定义搜索配置
    private SearchRequest searchRequest;

    private PromptTemplate rerankPromptTemplate=PromptTemplate.builder().
                template("下面是排好序的上下文以及对应的得分，请根据用户问题和上下文来回答。\n上下文列表：\n{context}")
                .build();

    public ReRankAdvisor(ZhiPuRerankService rerankService, VectorStore vectorStore, int order,
                         SearchRequest searchRequest,PromptTemplate rerankPromptTemplate) {
        this.order = order;
        this.rerankService = rerankService;
        this.vectorStore = vectorStore;
        this.searchRequest = searchRequest;
        this.rerankPromptTemplate=rerankPromptTemplate;
    }

    public ReRankAdvisor(ZhiPuRerankService rerankService, VectorStore vectorStore, int order,SearchRequest searchRequest) {
        this.order = order;
        this.rerankService = rerankService;
        this.vectorStore = vectorStore;
        this.searchRequest = searchRequest;
    }

    public ReRankAdvisor(ZhiPuRerankService rerankService, VectorStore vectorStore,
                         int order,PromptTemplate rerankPromptTemplate) {
        this.order = order;
        this.rerankService = rerankService;
        this.vectorStore = vectorStore;
        this.rerankPromptTemplate=rerankPromptTemplate;
    }

    public ReRankAdvisor(ZhiPuRerankService rerankService, VectorStore vectorStore,int order) {
        this.order = order;
        this.rerankService = rerankService;
        this.vectorStore = vectorStore;
    }

    @Override
    public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
        String query = chatClientRequest.prompt().getUserMessage().getText();
        if(query.isBlank())
            return chatClientRequest;

        //若自始至终没有定义搜索配置，则进行初始化
        processNullSearchRequest(query);

//        if (searchRequest==null){
//            processNullSearchRequest(query);
//        }
        List<Document> documents = searchFromDatabase(searchRequest);
        filterDocument();
        if (documents==null||documents.isEmpty())
            return chatClientRequest;

        List<Result> rerankedContext = rerank(query, documents);
        chatClientRequest.context().put("reranked-context", rerankedContext);
        String context=composeContext(rerankedContext);

        String finalUserMessage = "用户问题："+query+"\n\n"+context;
        ChatClientRequest processedChatClientRequest = chatClientRequest.mutate().
                prompt(chatClientRequest.prompt().augmentUserMessage(finalUserMessage))
                .build();

        return processedChatClientRequest;
    }

    public String composeContext( List<Result> results) {
        String contextString = results.stream()
                .map(r -> String.format("""
        [得分: %.4f\n内容: %s]
        ---""", r.score(), r.document()))
                .collect(Collectors.joining("\n"));
        Map<String, Object> map = Map.of("context", contextString);
        return this.rerankPromptTemplate.render(map);
    }

    private void processNullSearchRequest(String query) {
        FilterExpressionBuilder b = new FilterExpressionBuilder();
        Filter.Expression filter = b.eq("messageSource", "knowledge").build();
        searchRequest = SearchRequest.builder()
                .query(query)
                .topK(5)
                .filterExpression(filter)
                .build();
    }

    public void filterDocument() {
        //TODO: 根据元数据等，筛选文档. 其实可以在SearchRequest中添加过滤条件
    }
    public List<Result> rerank(String query, List<Document> documents) {
        List<String> documentTexts = documents.stream().map(Document::getText).toList();
        return rerankService.rerankAndResult(query, documentTexts, documents.size());
    }

    public List<Document> searchFromDatabase(SearchRequest searchRequest) {
        return vectorStore.similaritySearch(searchRequest);
    }

    @Override
    public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {
        return chatClientResponse;
    }

    @Override
    public int getOrder() {
        return order;
    }

    public static class Builder {

        private ZhiPuRerankService rerankService;
        private VectorStore vectorStore;
        private int order;
        private PromptTemplate rerankPromptTemplate;
        private SearchRequest searchRequest;

        public Builder rerankService(ZhiPuRerankService rerankService) {
            this.rerankService = rerankService;
            return this;
        }
        public Builder vectorStore(VectorStore vectorStore) {
            this.vectorStore = vectorStore;
            return this;
        }

        public Builder order(int order) {
            this.order = order;
            return this;
        }

        public Builder rerankPromptTemplate(PromptTemplate rerankPromptTemplate) {
            this.rerankPromptTemplate = rerankPromptTemplate;
            return this;
        }

        public Builder searchRequest(SearchRequest searchRequest) {
            this.searchRequest = searchRequest;
            return this;
        }
        public ReRankAdvisor build() {

            if (rerankService == null||vectorStore==null||order<0)
                throw new IllegalArgumentException("参数不合法");

            if (rerankPromptTemplate!=null&&searchRequest!=null)
                return new ReRankAdvisor(rerankService, vectorStore,order,searchRequest,rerankPromptTemplate);

            else if (rerankPromptTemplate!=null)
                return new ReRankAdvisor(rerankService, vectorStore,order,rerankPromptTemplate);

            else if (searchRequest!=null)
                return new ReRankAdvisor(rerankService, vectorStore,order,searchRequest);

            return new ReRankAdvisor(rerankService, vectorStore, order);
        }
    }
}

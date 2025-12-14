package com.test.chatmemoryexample.service;

import com.test.chatmemoryexample.advisor.ConversationIdAdvisor;
import jakarta.annotation.PostConstruct;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ChatMemoryServiceImpl {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    // 1. 使用 Spring 的 @Value 注解直接注入资源对象
    // 注意：QA.csv 必须放在 src/main/resources 目录下
    @Value("classpath:QA.csv")
    private Resource csvResource;

    public ChatMemoryServiceImpl(ChatClient.Builder chatClientBuilder, VectorStore vectorStore, ChatMemory chatMemory) {
        this.vectorStore = vectorStore; // 先赋值，防止下面调用时空指针

        // 2. RAG 配置
        SearchRequest searchRequest=SearchRequest.builder()
                .topK(50)
                .similarityThreshold(0.001f)
                .build();
        QuestionAnswerAdvisor questionAnswerAdvisor = QuestionAnswerAdvisor.builder(vectorStore)
                .order(1)
                .searchRequest(searchRequest)
                .build();

        // 3. 历史记忆配置
        PromptChatMemoryAdvisor promptChatMemoryAdvisor = PromptChatMemoryAdvisor.builder(chatMemory)
                .conversationId("default-store")
                .order(2).build();

        ConversationIdAdvisor conversationIdAdvisor = new ConversationIdAdvisor(0);

        this.chatClient = chatClientBuilder
                .defaultSystem("你是我的专属小助手！若我提出问题，请直接回复答案。")
                .defaultAdvisors(conversationIdAdvisor, promptChatMemoryAdvisor, questionAnswerAdvisor)
                .build();
    }

    // 建议添加此注解，确保在依赖注入完成后再加载数据
    @PostConstruct
    public void init() {
        loadCsvToVectorStore();
    }

    public String memoryChat(String question, String userId) {

        return chatClient.prompt(question).call().content();
    }

    /**
     * 解析 QA.csv 并存入向量数据库
     */
    public void loadCsvToVectorStore() {
        // 检查资源是否存在
        if (!csvResource.exists()) {
            System.out.println("⚠️ 警告：QA.csv 文件未找到，跳过加载数据。");
            return;
        }

        List<Document> documents = new ArrayList<>();

        // 4. 修改读取方式：使用 InputStreamReader 读取 Resource 的流
        try (Reader reader = new InputStreamReader(csvResource.getInputStream(), StandardCharsets.UTF_8);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

            for (CSVRecord csvRecord : csvParser) {
                String question = csvRecord.get("问题");
                String answer = csvRecord.get("回答");

                String content = "问题: " + question + "\n回答: " + answer;

                Map<String, Object> metadata = Map.of(
                        "origin_question", question,
                        "origin_answer", answer
                );

                Document doc = new Document(content, metadata);

                documents.add(doc);
            }

            if (!documents.isEmpty()) {

                // 这行代码会自动：
                // 1. 调用 Embedding 模型生成向量
                // 2. 将 内容+元数据+向量 保存到 Redis (JSON格式)
                vectorStore.add(documents);
                System.out.println("✅ 成功加载 " + documents.size() + " 条 QA 数据到向量数据库。");
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("CSV 解析失败", e);
        }
    }
}
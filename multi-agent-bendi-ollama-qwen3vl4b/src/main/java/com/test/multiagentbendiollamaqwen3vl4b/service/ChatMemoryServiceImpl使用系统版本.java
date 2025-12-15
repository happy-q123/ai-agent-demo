package com.test.multiagentbendiollamaqwen3vl4b.service;//package com.test.bendiollamaqwen3vl4b.service;
//
//import com.test.bendiollamaqwen3vl4b.advisor.ConversationIdAdvisor;
//import com.test.bendiollamaqwen3vl4b.advisor.InformationAdvisor;
//import com.test.bendiollamaqwen3vl4b.advisor.ReRankAdvisor;
//import com.test.customrerank.service.ZhiPuRerankService;
//import jakarta.annotation.PostConstruct;
//import org.apache.commons.csv.CSVFormat;
//import org.apache.commons.csv.CSVParser;
//import org.apache.commons.csv.CSVRecord;
//import org.springframework.ai.chat.client.ChatClient;
//import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
//import org.springframework.ai.chat.client.advisor.vectorstore.VectorStoreChatMemoryAdvisor;
//import org.springframework.ai.chat.memory.ChatMemory;
//import org.springframework.ai.chat.model.ChatResponse;
//import org.springframework.ai.document.Document;
//import org.springframework.ai.embedding.EmbeddingModel;
//import org.springframework.ai.tool.ToolCallbackProvider;
//import org.springframework.ai.vectorstore.SearchRequest;
//import org.springframework.ai.vectorstore.redis.RedisVectorStore;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.core.io.Resource;
//import org.springframework.stereotype.Service;
//import org.springframework.util.StringUtils;
//import reactor.core.publisher.Flux;
//
//import java.io.InputStreamReader;
//import java.io.Reader;
//import java.nio.charset.StandardCharsets;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//
//@Service
//public class ChatMemoryServiceImpl使用系统版本 {
//
//    private final ChatClient chatClient;
//    private final RedisVectorStore customRedisVectorStore;
//    private final EmbeddingModel embeddingModel;
//    private final ZhiPuRerankService zhiPuRerankService;
//
//    // 1. 使用 Spring 的 @Value 注解直接注入资源对象
//    // 注意：QA.csv 必须放在 src/main/resources 目录下
//    @Value("classpath:QAFull.csv")
//    private Resource csvResource;
//
//    @Value("classpath:西游记utf8.txt")
//    private Resource journeyToWestResource;
//
//    public ChatMemoryServiceImpl使用系统版本(ChatClient.Builder chatClientBuilder, ChatMemory chatMemory,
//                                             RedisVectorStore vectorStore, EmbeddingModel embeddingModel,
//                                             ToolCallbackProvider toolCallbackProvider, ZhiPuRerankService zhiPuRerankService) {
//        this.customRedisVectorStore = vectorStore;
//        this.embeddingModel = embeddingModel;
//        this.zhiPuRerankService = zhiPuRerankService;
////        OllamaApi
////    在这个类查看最终发送给模型的消息。
//
//        // 2. RAG 配置
//        SearchRequest searchRequest=SearchRequest.builder()
//                .topK(3)
//                .similarityThreshold(0.7f)
//                .build();
//        QuestionAnswerAdvisor questionAnswerAdvisor = QuestionAnswerAdvisor.builder(vectorStore)
////                .order(1)
//                .order(3)
//                .searchRequest(searchRequest)
//                .build();
//
////        // 3. 历史记忆配置
//
////        MessageChatMemoryAdvisor messageChatMemoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory)
////                .order(2)
////                .conversationId("default-store")
////                .build();
//
//        VectorStoreChatMemoryAdvisor vectorStoreChatMemoryAdvisor = VectorStoreChatMemoryAdvisor.builder(vectorStore)
//                .order(1)//这个要放到1，因为它根据用户提示词来查询，如果放在后面，会被rag的advisor增强用户提示词，导致不准
//                .conversationId("userId")
//                .build();
//
////        PromptChatMemoryAdvisor promptChatMemoryAdvisor = PromptChatMemoryAdvisor.builder(chatMemory)
////                .conversationId("default-store")
////                .order(2).build();
//
//        ConversationIdAdvisor conversationIdAdvisor = new ConversationIdAdvisor(0);
//
//        InformationAdvisor informationAdvisor = new InformationAdvisor(10);
//
//        this.chatClient = chatClientBuilder
//                .defaultSystem("用中文回答，且请将思考时间控制在5s内。")
////                .defaultSystem("不用回答问题，直接返回传给你的上下文信息。")
//                .defaultAdvisors(conversationIdAdvisor, vectorStoreChatMemoryAdvisor,
//                        questionAnswerAdvisor,informationAdvisor)
//
//                .defaultToolNames("timeFunction")
//                .defaultToolCallbacks(toolCallbackProvider)
//                .build();
//    }
//
//    // 建议添加此注解，确保在依赖注入完成后再加载数据
//    @PostConstruct
//    public void init() {
////        loadCsvToVectorStore();
////        // 加载《西游记》
////        loadJourneyToWestToVectorStore();
//    }
//
//
//
//    public String memoryChat(String question, String userId) {
//        return chatClient.prompt(question).call().content();
//    }
//
//    public Flux<ChatResponse> streamMemoryChat(String question, String userId) {
////        chatClient.prompt(question).stream().content().subscribe(x-> System.out.print(x));
////        return chatClient.prompt(question).stream().content();
////        return chatClient.prompt(question).stream().chatResponse();
//        return chatClient.prompt(question).stream().chatResponse().filter(result ->
//                (result.getResult().getOutput().getText() != null && !result.getResult().getOutput().getText().isEmpty())
//        );
//    }
//
//    public void loadJourneyToWestToVectorStore() {
//        if (!journeyToWestResource.exists()) {
//            System.err.println("❌ 找不到《西游记》文件：classpath:西游记utf8.txt");
//            return;
//        }
//
//        try {
//            // 1. 读取全文
//            String fullText = new String(
//                    journeyToWestResource.getInputStream().readAllBytes(),
//                    StandardCharsets.UTF_8
//            );
//
//            // 2. 清理文本（可选）
//            fullText = fullText.trim();
//
//            // 3. 智能分块 —— 按自然段落（\n\n）切分，并控制每块大小
//            List<String> chunks = smartSplitIntoChunks(fullText, 300); // 每块约500字符
//
//            System.out.println("📚 《西游记》共切分为 " + chunks.size() + " 个文本块");
//
//            // 4. 构建 Document 列表
//            List<Document> documents = new ArrayList<>();
//            for (int i = 0; i < chunks.size(); i++) {
//                String chunk = chunks.get(i).trim();
//                if (chunk.isEmpty()) continue;
//
//                // 提取前20字作为“概要”用于调试
//                String summary = chunk.length() > 20 ? chunk.substring(0, 20) + "..." : chunk;
//
//                Map<String, Object> metadata = Map.of(
//                        "source", "西游记utf8.txt",
//                        "chunk_index", i,
//                        "summary", summary, // 方便后续查看哪段被检索到
//                        "book", "西游记"
//                );
//
//                // 使用内容哈希作为 ID（幂等）
//                String id = UUID.nameUUIDFromBytes(chunk.getBytes(StandardCharsets.UTF_8)).toString();
//                documents.add(new Document(id, chunk, metadata));
//            }
//
//            // 5. 写入 Redis 向量库
//            if (!documents.isEmpty()) {
//                System.out.println("🚀 开始嵌入并写入 Redis...");
//                customRedisVectorStore.add(documents);
//                System.out.println("✅ 《西游记》已成功加载到 Redis 向量库！");
//            } else {
//                System.err.println("⚠️ 未生成任何有效文本块。");
//            }
//
//        } catch (Exception e) {
//            System.err.println("❌ 加载《西游记》失败：");
//            e.printStackTrace();
//        }
//    }
//
//
//
//    /**
//     * 按自然段落分块，并确保每块不超过 maxChars（尽量不分割句子）
//     */
//    private List<String> smartSplitIntoChunks(String text, int maxChars) {
//        List<String> chunks = new ArrayList<>();
//        String[] paragraphs = text.split("\n\n"); // 按双换行分段落
//
//        StringBuilder currentChunk = new StringBuilder();
//
//        for (String para : paragraphs) {
//            para = para.trim();
//            if (para.isEmpty()) continue;
//
//            // 如果当前段落太大（比如一整章），再按句号细分
//            if (para.length() > maxChars) {
//                if (currentChunk.length() > 0) {
//                    chunks.add(currentChunk.toString());
//                    currentChunk = new StringBuilder();
//                }
//
//                // 按句号/问号/感叹号切分长段落
//                String[] sentences = para.split("(?<=[。？！])");
//                for (String sentence : sentences) {
//                    if (currentChunk.length() + sentence.length() <= maxChars) {
//                        currentChunk.append(sentence);
//                    } else {
//                        if (currentChunk.length() > 0) {
//                            chunks.add(currentChunk.toString());
//                            currentChunk = new StringBuilder();
//                        }
//                        currentChunk.append(sentence);
//                    }
//                }
//            } else {
//                // 段落不大，尝试加入当前块
//                if (currentChunk.length() + para.length() <= maxChars) {
//                    if (currentChunk.length() > 0) currentChunk.append("\n\n");
//                    currentChunk.append(para);
//                } else {
//                    // 当前块满了，新开一块
//                    chunks.add(currentChunk.toString());
//                    currentChunk = new StringBuilder(para);
//                }
//            }
//        }
//
//        // 添加最后一块
//        if (currentChunk.length() > 0) {
//            chunks.add(currentChunk.toString());
//        }
//
//        return chunks;
//    }
//
//    /**
//     * 解析 QA.csv 并存入向量数据库 (完整健壮版)
//     */
//    public void loadCsvToVectorStore() {
//        // 1. 检查资源文件是否存在
//        if (!csvResource.exists()) {
//            System.err.println("❌ 错误：找不到文件 classpath:QAFull.csv");
//            System.err.println("   请检查：文件是否位于 src/main/resources 目录下？");
//            System.err.println("   请检查：Maven/Gradle 构建后，target/classes 目录下是否有该文件？");
//            return;
//        }
//
//        List<Document> documents = new ArrayList<>();
//
//        // 2. 读取并解析 CSV
//        try (Reader reader = new InputStreamReader(csvResource.getInputStream(), StandardCharsets.UTF_8);
//             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.builder()
//                     .setHeader()             // 自动读取第一行作为表头
//                     .setSkipHeaderRecord(true) // 跳过表头行
//                     .setIgnoreHeaderCase(true) // 忽略表头大小写
//                     .setTrim(true)             // 去除内容首尾空格
//                     .setIgnoreEmptyLines(true) // 忽略空行
//                     .build())) {
//
//            System.out.println("🔍 开始解析 CSV 文件...");
//            System.out.println("📋 检测到的原始表头: " + csvParser.getHeaderMap().keySet());
//
//            for (CSVRecord csvRecord : csvParser) {
//                // 3. 安全获取数据 (处理 BOM 头和列名不匹配问题)
//                String question = getSafeValue(csvRecord, "问题");
//                String answer = getSafeValue(csvRecord, "回答");
//
//                // 4. 校验数据有效性
//                if (!StringUtils.hasText(question) || !StringUtils.hasText(answer)) {
//                    System.out.println("⚠️ 跳过无效行 (行号 " + csvRecord.getRecordNumber() + "): 问题或回答为空");
//                    continue;
//                }
//
//                // 5. 构建 Document 对象
//                String content =  question + "\n" + answer;
////                String content = "问题: " + question + "\n回答: " + answer;
//
//                // 元数据：保留原始问答，方便后续检索时提取
//                Map<String, Object> metadata = Map.of(
//                        "origin_question", question,
//                        "origin_answer", answer,
//                        "source", "csv_import"
//                );
//
//                // 6. 生成确定性 ID (幂等性设计)
//                // 只要内容不变，ID 就不变，这样重复启动项目时会执行 Update 而不是重复 Insert
//                String id = UUID.nameUUIDFromBytes(content.getBytes(StandardCharsets.UTF_8)).toString();
//                Document doc = new Document(id, content, metadata);
//                documents.add(doc);
//            }
//
//            // 7. 写入向量数据库 (最关键的一步)
//            if (!documents.isEmpty()) {
//                System.out.println("🚀 解析完成，准备将 " + documents.size() + " 条数据嵌入并存入 Redis...");
//                System.out.println("ℹ️ 当前使用的 Embedding 模型: nomic-embed-text (理论维度: 768)");
//
//                try {
//                    // 【核心操作】调用 Embedding 模型向量化并写入 Redis
//                    customRedisVectorStore.add(documents);
//                    System.out.println("✅ 成功！数据已加载到 Redis 向量索引中。");
//                } catch (Exception e) {
//                    System.err.println("❌ 向量数据库写入失败！");
//                    System.err.println("🔴 可能原因 1: Redis 不是 Redis Stack 版本 (不支持向量搜索)");
//                    System.err.println("🔴 可能原因 2: application.yml 中未配置 embedding-dimension: 768");
//                    System.err.println("🔴 错误详情: " + e.getMessage());
//                    e.printStackTrace();
//                }
//            } else {
//                System.err.println("⚠️ CSV 解析完成，但未获取到任何有效数据 (Documents 列表为空)。请检查 CSV 内容或表头名称。");
//            }
//
//        } catch (Exception e) {
//            System.err.println("❌ 读取 CSV 文件发生异常");
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 辅助方法：安全获取 CSV 列值
//     * 解决 Excel 保存 CSV 时自带 BOM (\uFEFF) 导致第一列无法识别的问题
//     */
//    private String getSafeValue(CSVRecord record, String targetHeader) {
//        // 1. 直接匹配
//        if (record.isMapped(targetHeader)) {
//            return record.get(targetHeader);
//        }
//
//        // 2. 模糊匹配 (处理 BOM 或 空格)
//        // 遍历真实表头，看是否包含目标字段名 (例如 "\uFEFF问题" 包含 "问题")
//        Map<String, Integer> headerMap = record.getParser().getHeaderMap();
//        for (String actualHeader : headerMap.keySet()) {
//            // 移除不可见字符和空格后比较
//            String cleanHeader = actualHeader.replaceAll("[\\p{Cf}\\s]", "");
//            if (cleanHeader.contains(targetHeader) || targetHeader.contains(cleanHeader)) {
//                return record.get(actualHeader);
//            }
//        }
//
//        return null; // 未找到
//    }
//
//    // 添加在 ChatMemoryServiceImpl 类中
//
//    /**
//     * 直接检索向量库，返回原始文档列表
//     * @param query 用户的问题
//     * @return 匹配的文档列表
//     */
//    public List<Document> searchKnowledgeBase(String query) {
//        // 1. 构建搜索请求
//        // 这里建议和你的 RAG 配置保持一致（topK=10, threshold=0.7），以便通过接口排查由于检索导致的回答问题
//        SearchRequest searchRequest = SearchRequest.builder()
//                .query(query)
//                .topK(10)             // 返回最相似的 10 条
//                .similarityThreshold(0.7f) // 相似度阈值
//                .build();
//
//        // 2. 执行搜索
//        return customRedisVectorStore.similaritySearch(searchRequest);
//    }
//
//}
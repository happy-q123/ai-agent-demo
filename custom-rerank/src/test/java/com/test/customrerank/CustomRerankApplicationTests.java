package com.test.customrerank;

import com.test.customrerank.dto.RerankResponse;
import com.test.customrerank.dto.Result;
import com.test.customrerank.service.ZhiPuRerankService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class CustomRerankApplicationTests {
    @Autowired
    private ZhiPuRerankService rerankService;
    @Test
    void contextLoads() {
    }

    @Test
    @DisplayName("真实调用：测试智谱Rerank接口是否连通并返回正确排序")
    void testRerankRealCall() {
        // 1. 准备真实的测试数据
        // 场景：用户搜“机器学习”，我们需要找出哪个文档最相关
        String query = "机器学习";
        List<String> documents = List.of(
                "机器学习是人工智能的一个子集，专注于让计算机从数据中学习。", // 强相关
                "今天天气真不错，适合出去野餐。",                         // 不相关
                "深度学习是机器学习的一种特殊技术。",                     // 中等相关
                "面条煮得太久容易烂。"                                  // 完全不相关
        );

        System.out.println(">>> 开始发起 Rerank 请求...");

        // 2. 调用服务 (取 Top 2)
        RerankResponse response = rerankService.rerank(query, documents, 2);

        // 3. 打印结果 (方便在控制台肉眼确认)
        System.out.println(">>> 请求成功，模型返回 ID: " + response.id());
        System.out.println(">>> Token 消耗: " + response.usage().totalTokens());

        System.out.println("--- 排序结果 ---");
        for (Result result : response.results()) {
            String originalText = documents.get(result.index());
            System.out.printf("[排名: %d] 分数: %.4f | 内容: %s%n",
                    result.index(),
                    result.score(),
                    originalText
            );
        }

        // 4. 断言验证 (自动化判断)
        // 验证返回不为空
        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.results().isEmpty());

        // 验证第一名必须是和“机器学习”定义最相关的那个文档 (原列表索引 0)
        // 注意：分数是浮动的，但相对顺序通常是稳定的
        Integer top1Index = response.results().get(0).index();
        Assertions.assertEquals(0, top1Index, "预期最相关的文档应该是索引0（机器学习定义）");

        // 验证分数范围 (0到1之间)
        Assertions.assertTrue(response.results().get(0).score() > 0.0);
        Assertions.assertTrue(response.results().get(0).score() <= 1.0);
    }
}

package com.test.multiagentbendiollamaqwen3vl4b;

import com.test.multiagentbendiollamaqwen3vl4b.service.agent.AgentManager;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
@Slf4j
@SpringBootTest
public class ApplicationTest {
    @Resource
    private AgentManager agentManager;

    @Test
    public void test() {
        String result = agentManager.doService("请用中文回答：你叫什么名字？");
        log.warn(result);
    }

    @Test
    public void test2() {
        String result = agentManager.doService("ChatMemoryAgent","孙悟空的武器是什么？");
        log.warn(result);
    }
}

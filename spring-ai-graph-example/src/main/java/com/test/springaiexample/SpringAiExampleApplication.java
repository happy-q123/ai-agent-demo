package com.test.springaiexample;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.ai.zhipuai.api.ZhiPuAiApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringAiExampleApplication {
    @Autowired
    ZhiPuAiChatModel chatModel;


    public static void main(String[] args) {
        SpringApplication.run(SpringAiExampleApplication.class, args);

    }

}

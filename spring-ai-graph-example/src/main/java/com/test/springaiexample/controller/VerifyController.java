package com.test.springaiexample.controller;

import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.ai.zhipuai.api.ZhiPuAiApi;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VerifyController {

    //他俩的关系:
    //SpringAi 提供了抽象的接口ChatModel，而具体实现则由第三方提供，比如这里使用的是ZhiPuAiApiChatModel
    //ZhiPuAiApiChatModel实现了ChatModel接口,
    // 又因为是在Spring容器中,所以下面zhiPuAiChatModel和chatModel其实都是指ZhiPuAiChatModel对象
    private final ZhiPuAiChatModel zhiPuAiChatModel;
    private final ChatModel chatModel;

    public VerifyController(ZhiPuAiChatModel zhiPuAiChatModel, ChatModel chatModel) {
        this.zhiPuAiChatModel = zhiPuAiChatModel;
        this.chatModel = chatModel;
    }

    @GetMapping("/commonQuery")
    public String verify(@RequestParam("query") String query) {
//        通过SpringAi封装的对象调用质谱模型
//        String result=chatModel.call(query);//普通查询

        //设置用户提示词和系统提示词等
        UserMessage userMessage = new UserMessage(query);
        SystemMessage systemMessage = new SystemMessage("你是强哥的专属助手.");
        ChatOptions chatOptions = ChatOptions.builder()
                .temperature(0.5)
                .build();
        Prompt prompt = Prompt.builder()
                .messages(systemMessage, userMessage)
                .chatOptions(chatOptions)
                .build();
        ChatResponse response = chatModel.call(prompt);
        return response.getResult().getOutput().getText();

//        通过质谱模型实现的对象调用质谱模型
//        String result2=zhiPuAiChatModel.call(query);
//        return result;
    }
}

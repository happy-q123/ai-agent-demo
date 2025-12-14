package com.test.springaiexample.service;

import com.test.springaiexample.entity.EnglishEntity;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class WordTranslationServiceImpl {
    private final ChatClient chatClient;

    private PromptTemplate p= PromptTemplate.builder()
            .template("你是一个英语专家").build();


    public WordTranslationServiceImpl(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();

////        也可以用在Builder中添加设置，如下所示
//        this.chatClient = chatClientBuilder
//                .defaultAdvisors()
//                .defaultSystem()
//                .defaultOptions().build();

////        一旦build了，chatClient就是不可变对象了，只能通过mutate()拿到builder再创建一个。
////        通过mutate()拿到builder会先复制当前chatClient已有的配置，然后再返回builder。
////        build方法会返回一个新的chatClient对象，记得接收。
//        chatClient = chatClientBuilder.mutate().defaultAdvisors().build();
    }

    public EnglishEntity translateWord(EnglishEntity entity) {
        PromptTemplate p_linshi= PromptTemplate.builder()
                .template("请将单词{word}翻译成英文。请直接返回结果，如果是已经是英文则返回英文。").build();
        String message = p_linshi.render(Map.of("word",entity.getOriginalWord()));

        UserMessage userMessage = new UserMessage(message);
        Prompt prompt = Prompt.builder()
                .messages(p.createMessage(), userMessage)
                .build();
        String result = chatClient.prompt(prompt).call().content();
        entity.setWordTranslation(result);
        return entity;
    }

    public EnglishEntity composeJuzi(EnglishEntity entity) {
        String message = "请用单词"+entity.getOriginalWord()+"造一个英文句子。请直接返回结果";
        UserMessage userMessage = new UserMessage(message);
        Prompt prompt = Prompt.builder()
                .messages(p.createMessage(), userMessage)
                .build();
        String result = chatClient.prompt(prompt).call().content();
        entity.setJuZi(result);
        return entity;
    }

    public EnglishEntity translateJuzi(EnglishEntity entity) {
        String message = "请用将句子"+entity.getJuZi()+"翻译成简体汉语。请直接返回结果";
        UserMessage userMessage = new UserMessage(message);
        Prompt prompt = Prompt.builder()
                .messages(p.createMessage(), userMessage)
                .build();
        String result = chatClient.prompt(prompt).call().content();
        entity.setJuZiTranslation(result);
        return entity;
    }


}

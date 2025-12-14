package com.test.chatmemoryexample.controller;

import com.test.chatmemoryexample.service.ChatMemoryServiceImpl;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatMemoryController {
    private final ChatMemoryServiceImpl chatMemoryService;


    public ChatMemoryController(ChatMemoryServiceImpl chatMemoryService) {
        this.chatMemoryService = chatMemoryService;
    }

    @GetMapping("/memoryChat")
    public String memoryChat(@RequestParam("query") String query) {
        return chatMemoryService.memoryChat(query,"1");
    }
}

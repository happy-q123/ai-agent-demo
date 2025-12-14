package com.test.springaiexample.graph.node;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.AsyncNodeAction;
import com.test.springaiexample.entity.EnglishEntity;
import com.test.springaiexample.service.WordTranslationServiceImpl;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
public class ComposeJuziNode implements AsyncNodeAction {

    private final WordTranslationServiceImpl wordTranslationService;

    public ComposeJuziNode(WordTranslationServiceImpl wordTranslationService) {
        this.wordTranslationService = wordTranslationService;
    }

    @Override
    public CompletableFuture<Map<String, Object>> apply(OverAllState state) {
        EnglishEntity entity=(EnglishEntity)state.data().get("EnglishEntity");
        wordTranslationService.composeJuzi(entity);
        System.out.println("*****ComposeJuzi***");
        return CompletableFuture.completedFuture(state.data());
    }
}

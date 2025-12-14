package com.test.springaiexample.controller;

import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.test.springaiexample.entity.EnglishEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
public class GraphController {

    private final CompiledGraph compiledGraph;

    public GraphController(CompiledGraph compiledGraph) {
        this.compiledGraph = compiledGraph;
    }

    @GetMapping("/graph")
    public String graph(@RequestParam("query") String query) {
        EnglishEntity englishEntity = EnglishEntity.builder()
                .originalWord(query)
                .build();

        Optional<OverAllState>optionalResult = compiledGraph.invoke( Map.of("EnglishEntity", englishEntity));
        EnglishEntity entity=(EnglishEntity)optionalResult.get().data().get("EnglishEntity");


        return entity.toString();
    }
}

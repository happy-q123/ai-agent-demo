package com.test.springaiexample.graph;

import com.test.springaiexample.graph.node.ComposeJuziNode;
import com.test.springaiexample.graph.node.TranslateJuziNode;
import com.test.springaiexample.graph.node.TranslateWordNode;
import com.test.springaiexample.service.WordTranslationServiceImpl;
import lombok.Data;
import lombok.Generated;
import lombok.Getter;
import org.springframework.stereotype.Component;
@Getter
@Component
public class EnglishTranslateGraphNodes {
    private final TranslateWordNode translateWordNode;
    private final TranslateJuziNode translateJuziNode;
    private final ComposeJuziNode composeJuziNode;

    public EnglishTranslateGraphNodes(WordTranslationServiceImpl wordTranslationService) {
        this.translateWordNode = new TranslateWordNode(wordTranslationService);
        this.translateJuziNode = new TranslateJuziNode(wordTranslationService);
        this.composeJuziNode = new ComposeJuziNode(wordTranslationService);
    }
}

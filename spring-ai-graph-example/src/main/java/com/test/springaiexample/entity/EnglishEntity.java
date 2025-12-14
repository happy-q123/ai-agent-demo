package com.test.springaiexample.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnglishEntity {

    //原始英文单词
    private String originalWord;

    //单词翻译
    private String wordTranslation;

    //单词造的句子
    private String juZi;

    //句子的翻译
    private String juZiTranslation;
}

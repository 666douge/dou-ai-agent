package com.dou.douaiagent.rag.transformer;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.KeywordMetadataEnricher;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 基于 AI 的文档元信息增强器
 * 通过AI来提取元信息，便于后续提高检索的准确性
 */
@Component
public class MyKeywordsEnricher {


    @Resource
    private ChatModel dashscopeChatModel;

    public List<Document> keywordsEnricherList(List<Document> documentList){
        KeywordMetadataEnricher enricher = new KeywordMetadataEnricher(dashscopeChatModel, 5);
        List<Document> apply = enricher.apply(documentList);
        return apply;
    }
}

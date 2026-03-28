package com.dou.douaiagent.rag;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;


@SpringBootTest
class LoveAppPgVectorVectorStoreConfigTest {

    @Resource
    private VectorStore loveAppPgVectorVectorStore;

    @Test
    void loveAppPgVectorStore() {
        List<Document> documents = List.of(
                new Document("豆哥耍还是逗哥耍？", Map.of("meta1", "meta1")),
                new Document("豆哥喜欢吃橘子！"),
                new Document("谁喜欢吃橘子？", Map.of("meta2", "meta2")));
        loveAppPgVectorVectorStore.add(documents);

        List<Document> results = loveAppPgVectorVectorStore
                .similaritySearch(SearchRequest.builder().query("我还是单身，我想恋爱了").topK(3).build());

    }
}
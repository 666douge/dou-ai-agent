package com.dou.douaiagent.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 将恋爱大师的数据存入向量数据（初始化基于内存的向量数据库）
 */
@Configuration
public class LoveAppVectorStoreConfig {

    @Resource
    private LoveAppDocFileLoader loveAppDocFileLoader;

    @Bean
    VectorStore loveAppVectorStore(EmbeddingModel dashscopeEmbeddingModel){
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(dashscopeEmbeddingModel).build();
        List<Document> list = loveAppDocFileLoader.loadDocFiles();
        simpleVectorStore.add(list);
        return simpleVectorStore;
    }



}

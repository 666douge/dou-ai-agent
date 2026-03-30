package com.dou.douaiagent.rag;

import com.dou.douaiagent.rag.transformer.MyKeywordsEnricher;
import com.dou.douaiagent.rag.transformer.MyTokenTextSplitter;
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
    @Resource
    private MyTokenTextSplitter myTokenTextSplitter;

    @Resource
    private MyKeywordsEnricher myKeywordsEnricher;

    @Bean
    VectorStore loveAppVectorStore(EmbeddingModel dashscopeEmbeddingModel){
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(dashscopeEmbeddingModel).build();
        //加载文档
        List<Document> list = loveAppDocFileLoader.loadDocFiles();
        //自主切分文档
        //List<Document> splitDocumentlist = myTokenTextSplitter.splitCustomized(list);
        //自动补充文档的元信息（通过AI补充，这一步可能有点慢）
        List<Document> enrichdocuments = myKeywordsEnricher.keywordsEnricherList(list);
        simpleVectorStore.add(enrichdocuments);
        return simpleVectorStore;
    }



}

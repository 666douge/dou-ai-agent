package com.dou.douaiagent.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;


import static org.springframework.ai.vectorstore.pgvector.PgVectorStore.PgDistanceType.COSINE_DISTANCE;
import static org.springframework.ai.vectorstore.pgvector.PgVectorStore.PgIndexType.HNSW;

/**
 * 应用 postgresql数据库库，初始化表及数据
 * 注：postgre需要安装PGVector插件后才能支持存储和检索向量数据
 * */
@Configuration
public class LoveAppPgVectorVectorStoreConfig {

    @Resource
    private LoveAppDocFileLoader loveAppDocFileLoader;

    @Bean
    VectorStore loveAppPgVectorVectorStore(JdbcTemplate jdbcTemplate, EmbeddingModel dashscopeEmbeddingModel) {
        //系统初始化的时候，PgVectorStoreAutoConfiguration类中会默认记载EmbeddingModel，所以环境中如果注入了多个EmbeddingModel时
        //就会报错。因此，这时我们就要在启动时不要让他去默认加载，就要在启动类中奖这个类排除（写法如下）
        // @SpringBootApplication(exclude = PgVectorStoreAutoConfiguration.class)
        //这样后续就会使用我们指定EmbeddingModel，如下面的指定的dashscopeEmbeddingModel
        PgVectorStore pgVectorStore = PgVectorStore.builder(jdbcTemplate, dashscopeEmbeddingModel)
                .dimensions(1536)                    // Optional: 设置向量维度
                .distanceType(COSINE_DISTANCE)       // Optional: defaults to COSINE_DISTANCE
                .indexType(HNSW)                     // Optional: defaults to HNSW
                .initializeSchema(true)              // Optional: defaults to false
                .schemaName("public")                // Optional: defaults to "public"
                .vectorTableName("vector_store")     // Optional: 创建的向量数据库表
                .maxDocumentBatchSize(10000)         // Optional: defaults to 10000
                .build();

        //加载数据
        pgVectorStore.add(loveAppDocFileLoader.loadDocFiles());
        return pgVectorStore;
    }
}

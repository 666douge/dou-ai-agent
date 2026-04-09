package com.dou.douaiagent.rag;

import cn.hutool.core.collection.CollUtil;
import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;


import java.util.List;

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
    @Resource
    private JdbcTemplate jdbcTemplate;

    @Bean
    VectorStore loveAppPgVectorVectorStore(JdbcTemplate jdbcTemplate, EmbeddingModel dashscopeEmbeddingModel) {
        //系统初始化的时候，PgVectorStoreAutoConfiguration类中会默认记载EmbeddingModel，所以环境中如果注入了多个EmbeddingModel时
        //就会报错。因此，这时我们就要在启动时不要让他去默认加载，就要在启动类中奖这个类排除（写法如下）
        // @SpringBootApplication(exclude = PgVectorStoreAutoConfiguration.class)
        //这样后续就会使用我们指定EmbeddingModel，如下面的指定的dashscopeEmbeddingModel
        PgVectorStore pgVectorStore = PgVectorStore.builder(jdbcTemplate, dashscopeEmbeddingModel)
                .dimensions(1024)                    // Optional: 设置向量维度
                .distanceType(COSINE_DISTANCE)       // Optional: defaults to COSINE_DISTANCE
                .indexType(HNSW)                     // Optional: defaults to HNSW
                .initializeSchema(true)              // Optional: defaults to false
                .schemaName("public")                // Optional: defaults to "public"
                .vectorTableName("vector_store")     // Optional: 创建的向量数据库表
                .maxDocumentBatchSize(10000)         // Optional: defaults to 10000
                .build();

        //查询向量数据库中是否有数据
        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM vector_store",
                Long.class
        );
        if(count > 0){
            return pgVectorStore;
        }
        //加载数据
        List<Document> result = loveAppDocFileLoader.loadDocFiles();
        if(result != null && !result.isEmpty()){
            //新版本做了限制，批次只能最多插入10条（可以通过写代码解除限制，这里先不解除限制），所以拆分list，进行分批插入
            List<List<Document>> split = CollUtil.split(result, 10);
            //调用PGVectorStore的保存方法
            split.stream().filter(curlist ->
                !curlist.isEmpty()
            ).forEach(pgVectorStore::add);
        }
        return pgVectorStore;
    }


}

package com.dou.douaiagent.rag;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetriever;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetrieverOptions;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 自定义基于阿里云的RAG知识库
 */
@Configuration
public class LoveAppRagCloudAdvisorConfig {

    @Value("${spring.ai.dashscope.api-key}")
    private String apikey;


    @Bean
    public Advisor loveAppRagCloudAdvisor(){
        DashScopeApi dashScopeApi = DashScopeApi.builder().apiKey(apikey).build();
        //百炼云平台中知识库的名称
        final String KOWNLEGE_INDEX = "恋爱大师";
        DashScopeDocumentRetriever dashScopeDocumentRetriever = new DashScopeDocumentRetriever(dashScopeApi,
                DashScopeDocumentRetrieverOptions.builder()
                        .withIndexName(KOWNLEGE_INDEX)
                        .build());


        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(dashScopeDocumentRetriever)
                .build();
    }

}

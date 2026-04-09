package com.dou.douaiagent.rag;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.ai.rag.preretrieval.query.expansion.QueryExpander;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;

/**
 * 自定义 检索增强顾问
 */
public class LoveAppRagCustomAdvisorFactory {

    /**
     * 创建自定义 检索增强器
     * 支持多扩展查询 或  重写查询 两种方式
     * @param vectorStore
     * @param filterString
     * @param chatClient
     * @return
     */
    public static Advisor createLoveAppRagCustomAdvisor(VectorStore vectorStore, String filterString, ChatClient chatClient){
        //查询重写，将用户输入的信息进行重写，得到更精确的查询问题
        RewriteQueryTransformer rewriteQueryTransformer = RewriteQueryTransformer.builder()
                //.chatClientBuilder(ChatClient.builder(dashscopeChatModel))
                .chatClientBuilder(chatClient.mutate())
                .build();
        //多扩展查询，将问题进行明确细致化话为多个，一般不建议这样使用
        QueryExpander expender = MultiQueryExpander.builder()
//                .chatClientBuilder(ChatClient.builder(dashscopeChatModel))
                .chatClientBuilder(chatClient.mutate())
                .numberOfQueries(3)
                .build();

        //设置 检索过滤条件
        Filter.Expression filerExpression = new FilterExpressionBuilder()
                //这里自定义一个状态查询的条件（注：这个参数key不是固定，按照实际业务定义的）
                .eq("status", filterString)
                .build();
        //创建文档检索器  VectorStoreDocumentRetriever是spring原生的文档检索器
        VectorStoreDocumentRetriever vectorStoreDocumentRetriever = VectorStoreDocumentRetriever.builder()
                //设置过滤条件
                .filterExpression(filerExpression)
                //相似度阈值
                .similarityThreshold(0.5)
                .topK(3)  //返回文档数量
                .vectorStore(vectorStore)
                .build();
        //创建并返回 检索增强顾问
        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(vectorStoreDocumentRetriever)
                //.queryExpander(expender)//多扩展查询
                .queryTransformers(rewriteQueryTransformer)//查询重写
                //创建自定义的上下文查询增强器，定义了异常情况（查不到数据时）下的回复方式
                .queryAugmenter( LoveAppContextualQueryAugmenterFactory.createInstance())
                .build();
    }




    public static Advisor createLoveAppRagCustomAdvisor(VectorStore vectorStore, String filterString){
        //设置 检索过滤条件
        Filter.Expression filerExpression = new FilterExpressionBuilder()
                //这里自定义一个状态查询的条件（注：这个参数key不是固定，按照实际业务定义的）
                .eq("status", filterString)
                .build();
        //创建文档检索器  VectorStoreDocumentRetriever是spring原生的文档检索器
        VectorStoreDocumentRetriever vectorStoreDocumentRetriever = VectorStoreDocumentRetriever.builder()
                //设置过滤条件
                .filterExpression(filerExpression)
                //相似度阈值
                .similarityThreshold(0.5)
                .topK(3)  //返回文档数量
                .vectorStore(vectorStore)
                .build();
        //创建并返回 检索增强顾问
        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(vectorStoreDocumentRetriever)
                //创建自定义的上下文查询增强器，定义了异常情况（查不到数据时）下的回复方式
                .queryAugmenter( LoveAppContextualQueryAugmenterFactory.createInstance())
                .build();
    }
}

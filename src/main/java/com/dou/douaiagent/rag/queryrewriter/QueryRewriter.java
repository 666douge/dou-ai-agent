package com.dou.douaiagent.rag.queryrewriter;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.stereotype.Component;

/**
 * 查询重写器
 * 将用户输入的信息 通过大模型 进行重写
 * 注意：如果想AI回答的问题质量高，可以这个重写器，可能会将用户提出的问题精准化，从而使得AI返回的答案质量相对较高
 * 如果想AI回答时召回的答案多一些，建议可以不要重写
 */
@Component
public class QueryRewriter {

    private final QueryTransformer queryTransformer;

    public QueryRewriter(ChatModel dashscopeChatModel){
        ChatClient.Builder chatclientbuider = ChatClient.builder(dashscopeChatModel);
        //创建重写查询转换器
        this.queryTransformer = RewriteQueryTransformer.builder()
                .chatClientBuilder(chatclientbuider)
                .build();
    }


    /**
     * 执行查询重写
     * @param queryMessage
     * @return
     */
    public String doQueryRewrite(String queryMessage){
        Query query = new Query(queryMessage);
        //执行查询重写
        Query transform = queryTransformer.transform(query);
        //输出重写后的查询问题
        return transform.text();
    }

}

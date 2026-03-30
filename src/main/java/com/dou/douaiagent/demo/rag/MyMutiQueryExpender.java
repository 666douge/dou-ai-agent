package com.dou.douaiagent.demo.rag;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 基于用户输入的问题 进行多扩展查询
 * 场景：将用户提出的不太明确的问题进行扩展，即尝试将问题明确化，不那么模糊，或语义清晰化
 * 注：因为要引用大模型进行处理扩展消息，所以涉及到成本。此外，这里的扩展出来的问题需要挨个调用模型进行回单，然后通过文档合成器，将重复的消息进行过滤然后，
 * 再返回最终结果。总之整体成本很高，而且存在很高的不确定性。
 */
@Component
public class MyMutiQueryExpender {

    private final ChatClient.Builder clientbuiler;

    public MyMutiQueryExpender(ChatModel dashscopeChatModel) {
        this.clientbuiler = ChatClient.builder(dashscopeChatModel);
    }

    public List<Query> expende(String query){
        MultiQueryExpander build = MultiQueryExpander.builder()
                .chatClientBuilder(clientbuiler)
                .numberOfQueries(5)
                .build();

        //根据用户输入的问题扩展后，给出几个新的扩展后的问题
        List<Query> expandedList = build.expand(new Query(query));
        return expandedList;
    }


}

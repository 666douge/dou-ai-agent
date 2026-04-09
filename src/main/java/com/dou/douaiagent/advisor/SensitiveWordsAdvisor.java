package com.dou.douaiagent.advisor;

import lombok.extern.slf4j.Slf4j;
import opennlp.tools.util.StringUtil;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class SensitiveWordsAdvisor implements CallAdvisor, StreamAdvisor {

    private ChatClientRequest before(ChatClientRequest advisedRequest){
        List<String> forbiddenWords = Arrays.asList("死", "违禁词1", "违禁词2");
        String userText = advisedRequest.prompt().getContents();
        if (StringUtil.isEmpty(userText) || forbiddenWords.stream().anyMatch(userText::contains)){
            log.info("您输入的内容中包含违禁词或为空，已被系统拦截！");
            return null;
        }
        return advisedRequest;
    }

    @Override
    public String getName() {
        return "违禁词校验advisor";
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        chatClientRequest = before(chatClientRequest);
        if(chatClientRequest != null){
            ChatClientResponse chatClientResponse = callAdvisorChain.nextCall(chatClientRequest);
            return chatClientResponse;
        }
        String content = "您输入的内容中包含违禁词或为空，已被系统拦截！";
        throw new RuntimeException(content);
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest, StreamAdvisorChain streamAdvisorChain) {
        chatClientRequest = before(chatClientRequest);
        if(chatClientRequest != null){
            Flux<ChatClientResponse> chatClientResponseFlux = streamAdvisorChain.nextStream(chatClientRequest);
            return chatClientResponseFlux;
        }
        return null;
    }
}

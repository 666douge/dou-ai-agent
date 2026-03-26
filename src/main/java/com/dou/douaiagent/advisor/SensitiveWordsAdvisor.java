package com.dou.douaiagent.advisor;

import lombok.extern.slf4j.Slf4j;
import opennlp.tools.util.StringUtil;
import org.springframework.ai.chat.client.advisor.api.*;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class SensitiveWordsAdvisor implements CallAroundAdvisor, StreamAroundAdvisor {

    private AdvisedRequest before(AdvisedRequest advisedRequest){
        List<String> forbiddenWords = Arrays.asList("死", "违禁词1", "违禁词2");
        String userText = advisedRequest.userText();
        if (StringUtil.isEmpty(userText) || forbiddenWords.stream().anyMatch(userText::contains)){
            log.info("您输入的内容中包含违禁词或为空，已被系统拦截！");
            return null;
        }
        return advisedRequest;
    }

    @Override
    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        advisedRequest = before(advisedRequest);
        if(advisedRequest != null){
            AdvisedResponse advisedResponse = chain.nextAroundCall(advisedRequest);
            return advisedResponse;
        }
        String content = "您输入的内容中包含违禁词或为空，已被系统拦截！";
        throw new RuntimeException(content);
    }

    @Override
    public Flux<AdvisedResponse> aroundStream(AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {
        advisedRequest = before(advisedRequest);
        if(advisedRequest != null){
            Flux<AdvisedResponse> advisedResponseFlux = chain.nextAroundStream(advisedRequest);
            return advisedResponseFlux;
        }
        return null;
    }

    @Override
    public String getName() {
        return "违禁词校验advisor";
    }

    @Override
    public int getOrder() {
        return 0;
    }
}

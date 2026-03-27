package com.dou.douaiagent.prompt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

@Component
@Slf4j
public class UserPrompt {

    private final ResourcePatternResolver resourcePatternResolver;

    public UserPrompt(ResourcePatternResolver resourcePatternResolver) {
        this.resourcePatternResolver = resourcePatternResolver;
    }


    /**
     * PromptTemplate 提示词模版
     * @param userMessage
     * @return
     */
    public String getUserPrompt(String userMessage){
        try {
            Resource[] resources = resourcePatternResolver
                    .getResources("classpath:/prompts/userFixPrompt");

            if(resources != null && resources.length >= 1){
                String content = resources[0].getContentAsString(Charset.defaultCharset());
                PromptTemplate promptTemplate = new PromptTemplate(content);
                //return promptTemplate.render(Map.of("question", userMessage,"userName","逗哥"));
                return promptTemplate.render(Map.of("question", userMessage));
            }
            throw new RuntimeException("用户提示词模板中的内容为空，请检查内容是否正确配置！");
        } catch (IOException e) {
            throw new RuntimeException("加载用户提示词模板失败：" + e);
        }
    }
}

package com.dou.douaiagent.prompt;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
@Slf4j
public class UserPrompt {

    @Value("classpath:/prompts/userFixPrompt")
    private Resource userResouce;

    private String userPromptString;

    @PostConstruct
    void init() throws IOException {
        if (userResouce == null) {
            log.error("userResouce is null! Check: 1) file exists at src/main/resources/prompts/userFixPrompt  2) no 'new UserPrompt()' call bypassing Spring");
            throw new IllegalStateException("userResouce is null");
        }
        if (!userResouce.exists()) {
            log.error("userResouce exists but file not found on classpath: {}", userResouce);
            throw new IllegalStateException("resource file not found: " + userResouce);
        }
        this.userPromptString = userResouce.getContentAsString(StandardCharsets.UTF_8);
        log.info("user prompt loaded, length={}", userPromptString.length());
    }

    /**
     * PromptTemplate 提示词模版
     * @param userMessage
     * @return
     */
    public String getUserPrompt(String userMessage){
        PromptTemplate promptTemplate = new PromptTemplate(userPromptString);
        //return promptTemplate.render(Map.of("question", userMessage,"userName","逗哥"));
        return promptTemplate.render(Map.of("question", userMessage));
    }
}

package com.dou.douaiagent.prompt;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class SystemPrompt {

    @Value("classpath:/prompts/systemPromptTemplate")
    private Resource systemResource;

    private String systemPromptText;

    @PostConstruct
    void init() throws IOException {
        if (systemResource == null) {
            log.error("systemResource is null! Check: 1) file exists at src/main/resources/prompts/systemPromptTemplate  2) no 'new SystemPrompt()' call bypassing Spring");
            throw new IllegalStateException("systemResource is null");
        }
        if (!systemResource.exists()) {
            log.error("systemResource exists but file not found on classpath: {}", systemResource);
            throw new IllegalStateException("resource file not found: " + systemResource);
        }
        this.systemPromptText = systemResource.getContentAsString(StandardCharsets.UTF_8);
        log.info("System prompt loaded, length={}", systemPromptText.length());
    }

    public String getSystemPromptText() {
        return systemPromptText;
    }
}

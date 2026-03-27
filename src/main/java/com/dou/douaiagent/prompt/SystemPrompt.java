package com.dou.douaiagent.prompt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.Charset;

@Component
@Slf4j
public class SystemPrompt {

    private final ResourcePatternResolver resourcePatternResolver;

    public SystemPrompt(ResourcePatternResolver resourcePatternResolver){
        this.resourcePatternResolver = resourcePatternResolver;
    }


    public String getSystemPromptText() {
        try {
            Resource[] resources = resourcePatternResolver
                    .getResources("classpath:/prompts/systemPromptTemplate");

            if(resources != null && resources.length >=1){
                return resources[0].getContentAsString(Charset.defaultCharset());
            }
            throw new RuntimeException("未配置系统模板提示词，请检查文件内容！");
        } catch (IOException e) {
            throw new RuntimeException("读取系统模板提示词失败：" + e);
        }
    }
}

package com.dou.douaiagent.rag;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;

/**
 * 创建上下文查询增强器的工厂
 */
public class LoveAppContextualQueryAugmenterFactory {

    public static ContextualQueryAugmenter createInstance(){
        PromptTemplate promptTemplate = new PromptTemplate("你应该输出以下内容:\n" +
                "抱歉，我只能回答恋爱相关的问题，别的问题暂时无法回答您哦，您有问题可以联系客户218-2132546。");

        ContextualQueryAugmenter build = ContextualQueryAugmenter.builder()
                .allowEmptyContext(false)
                //如果上下文为空时，给出固定回复信息
                .emptyContextPromptTemplate(promptTemplate)
                .build();

        return build;

    }
}

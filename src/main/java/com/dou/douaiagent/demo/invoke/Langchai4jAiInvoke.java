package com.dou.douaiagent.demo.invoke;


import dev.langchain4j.community.model.dashscope.QwenChatModel;

public class Langchai4jAiInvoke {
    public static void main(String[] args) {
        QwenChatModel chatmodel = QwenChatModel.builder().apiKey(TestAPIKey.api_key).modelName("qwen-plus").build();
        String answer = chatmodel.chat("你好，我是开发逗哥！");
        System.out.println(answer);
    }
}

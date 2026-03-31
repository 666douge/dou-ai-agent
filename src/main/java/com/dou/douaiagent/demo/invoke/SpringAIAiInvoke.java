package com.dou.douaiagent.demo.invoke;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Spring AI 框架调用 AI 大模型
 * 由于每次启动服务都会执行这个类，先把调用模型这块给注释掉
 */
@Component
public class SpringAIAiInvoke implements CommandLineRunner {

    @Resource
    private ChatModel dashScopeChatModel;

    @Override
    public void run(String... args) throws Exception {
        /*AssistantMessage assistantMessage = dashScopeChatModel.call(new Prompt("你好，我是逗哥！"))
                .getResult().getOutput();
        System.out.println(assistantMessage.getText());*/

    }
}

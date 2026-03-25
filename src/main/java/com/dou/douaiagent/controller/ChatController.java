package com.dou.douaiagent.controller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Spring AI coze 请求入口
 * 但是coze的依赖包coze-spring-ai-starter 无法通过maven下载，所以有报错，先注释掉
 */
@RestController
@RequestMapping("/chat")
public class ChatController {

   /* private final CozeChatClient cozeChatClient;

    // 自动注入
    public ChatController(CozeChatClient cozeChatClient) {
        this.cozeChatClient = cozeChatClient;
    }

    @GetMapping
    public String chat(@RequestParam String message) {
        // 直接调用，和 Spring AI 用法完全一致
        return cozeChatClient.call(message);
    }*/
}
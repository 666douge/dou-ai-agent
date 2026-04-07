package com.dou.douaiagent.controller;

import com.dou.douaiagent.agent.DouManus;
import com.dou.douaiagent.app.LoveApp;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;

@RestController
@RequestMapping("/ai")
public class AiController {

    @Resource
    private LoveApp loveApp;

    @Resource
    private ToolCallback[] alltools;

    @Resource
    private ChatModel dashscopeChatModel;

    /**
     * 同步调用 AI 应用大师 返回
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping("/love_app/chat/sync")
    public String doChatWhitSync(String message, String chatId){
        return loveApp.doChat(message, chatId);
    }

    /**
     * SSE 流式输出  AI应用大师
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping(value = "/love_app/chat/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> doChatWhitSSE(String message, String chatId){
        return loveApp.doChatWhitStream(message, chatId);
    }

    /**
     * SSE 流式输出  AI应用大师
     * @param message
     * @param chatId
     * @return SseEmitter
     */
    @GetMapping("/love_app/chat/server_sent_sevent")
    public Flux<ServerSentEvent<String>> doChatWhitServerSentEvent(String message, String chatId){
        return loveApp.doChatWhitStream(message, chatId)
                .map(chunk -> ServerSentEvent.<String>builder()
                        .data(chunk)
                        .build());

    }

    /**
     * SSE 流式输出  AI应用大师
     * @param message
     * @param chatId
     * @return SseEmitter
     */
    @GetMapping("/love_app/chat/sse_emitter")
    public SseEmitter doChatWhitSseEmitter(String message, String chatId){
        //创建一个超时时间长的 sseEmitter
        SseEmitter sseEmitter = new SseEmitter(300000L);
        loveApp.doChatWhitStream(message, chatId)
                .subscribe(chunk -> {
                    try {
                        sseEmitter.send(chunk);
                    } catch (IOException e) {
                        sseEmitter.completeWithError(e);
                    }
                }, sseEmitter::completeWithError, sseEmitter::complete);

        return sseEmitter;
    }


    @GetMapping("/love_app/chat/manus")
    public SseEmitter doChatWithManus(String message){
        DouManus douManus = new DouManus(alltools, dashscopeChatModel);
        return douManus.runStream(message);
    }

}

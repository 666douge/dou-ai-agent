package com.dou.douaiagent.demo.cozez.invoke;


import com.coze.openapi.client.chat.CreateChatReq;
import com.coze.openapi.client.chat.model.ChatEvent;
import com.coze.openapi.client.chat.model.ChatEventType;
import com.coze.openapi.client.connversations.message.model.Message;
import com.coze.openapi.service.auth.TokenAuth;
import com.coze.openapi.service.config.Consts;
import com.coze.openapi.service.service.CozeAPI;

import io.reactivex.Flowable;
import okhttp3.OkHttpClient;

import java.util.Arrays;
import java.util.Collections;

public class SDKAiInvoke {
    public static void main(String[] args) {
        String token = CodeAPIKey.api_key;
        TokenAuth authCli = new TokenAuth(token);
//        String botID = System.getenv("PUBLISHED_BOT_ID");//
//        String userID = System.getenv("USER_ID");//

        String botID = "7617792077614694463";// PUBLISHED_BOT_ID
        String userID = "RootUser_2119901742";// USER_ID

        CozeAPI coze =
                new CozeAPI.Builder()
                        .baseURL(Consts.COZE_CN_BASE_URL)
                        .auth(authCli)
                        .client(new OkHttpClient.Builder().build())
                        .build();

        CreateChatReq req =
                CreateChatReq.builder()
                        .botID(botID)
                        .userID(userID)
                        .messages(Collections.singletonList(Message.buildUserQuestionText("你是谁?")))
                        .build();

        Flowable<ChatEvent> resp = coze.chat().stream(req);
        resp.blockingForEach(
                event -> {
                    if (ChatEventType.CONVERSATION_MESSAGE_DELTA.equals(event.getEvent())) {
                        System.out.print(event.getMessage().getContent());
                    }
                    if (ChatEventType.CONVERSATION_CHAT_COMPLETED.equals(event.getEvent())) {
                        System.out.println("Token usage:" + event.getChat().getUsage().getTokenCount());
                    }
                });
        System.out.println("done");
        coze.shutdownExecutor();

    }
}

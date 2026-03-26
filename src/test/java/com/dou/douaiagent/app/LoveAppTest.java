package com.dou.douaiagent.app;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;


@SpringBootTest
class LoveAppTest {

    @Resource
    private LoveApp loveApp;

    @Test
    void testChat() {
        String chat_id = UUID.randomUUID().toString();
        System.out.println("第一轮对话：");
        String message = "你好，我是程序员逗哥！";
        String anwser = loveApp.doChat(message, chat_id);

        System.out.println("第二轮对话：");
        message = "我的另一半是“踏雪无双”，我很喜欢她！";
        anwser = loveApp.doChat(message, chat_id);
        Assertions.assertNotNull(anwser);

        System.out.println("第三轮对话：");
        message = "我刚刚 给你说过我的另一半叫什么名字来着？";
        anwser = loveApp.doChat(message, chat_id);
        Assertions.assertNotNull(anwser);

    }

    @Test
    void doChatWhitReport(){
        String chat_id = UUID.randomUUID().toString();
        String message = "你好，我是程序员逗哥,我的另一半是“踏雪无双”，我可爱死她了，但是我不知道怎么追求她";
        try {
            LoveApp.LoveReport loveReport = loveApp.doChatWhitReport(message, chat_id);
            Assertions.assertNotNull(loveReport);
        }catch(Exception e){
            System.out.println(e.getMessage());
        }

    }
}
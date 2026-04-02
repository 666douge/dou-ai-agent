package com.dou.douaiagent.app;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;


@SpringBootTest
class LoveAppTest {

    @Resource
    private LoveApp loveApp;

    @Resource
    private ChatModel dashscopeChatModel;

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
        String message = "你好，我是程序员逗哥,我的另一半是“踏雪无双”，我可爱她了，但是我不知道怎么追求她";
        try {
            LoveApp.LoveReport loveReport = loveApp.doChatWhitReport(message, chat_id);
            Assertions.assertNotNull(loveReport);
        }catch(Exception e){
            System.out.println(e.getMessage());
        }

    }


    @Test
    void testChatWithRag(){
        String chat_id = UUID.randomUUID().toString();
        String message = "我已经结婚了，但是婚后关系不太亲密，怎么办？";
        String anwser = loveApp.doChatWithRag(message, chat_id);
    }


    @Test
    void doChatWithTools() {
        // 测试联网搜索问题的答案
        //testMessage("家住成都，周末想带孩子出去玩，找一下有哪些好玩的地方？");

        // 测试网页抓取：恋爱案例分析
        //testMessage("最近和对象吵架了，看看编程导航网站（codefather.cn）的其他情侣是怎么解决矛盾的？");

        // 测试资源下载：图片下载
        //testMessage("直接下载一张适合做手机壁纸的星空情侣图片为文件");

        // 测试终端操作：执行代码
       //testMessage("执行 Python3 脚本来生成数据分析报告");

        // 测试文件操作：保存用户档案
        //testMessage("保存我的恋爱档案为文件，不用参考模板，直接保存为txt格式的文件即可");

        // 测试 PDF 生成
       //testMessage("生成一份‘七夕约会计划’PDF，包含餐厅预订、活动流程和礼物清单");
        testMessage("查找王文京的个人信息，并保存到数据库中（不是保存到文件）");
    }

    private void testMessage(String message) {
        String chatId = UUID.randomUUID().toString();
        String answer = loveApp.doChatWithTools(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void doChatWithMCP() {
        String chatId = UUID.randomUUID().toString();
        String message = "我的另一半成都新津区上班，给我找一下5公里内适合约会的地方";
        String answer = loveApp.doChatWithMCP(message, chatId);
        Assertions.assertNotNull(answer);
    }


}
package com.dou.douaiagent.app;



import com.dou.douaiagent.advisor.MyLoggerAdvisor;
import com.dou.douaiagent.advisor.SensitiveWordsAdvisor;
import com.dou.douaiagent.chatmemory.InRedisChatMemory;
import com.dou.douaiagent.prompt.SystemPrompt;
import com.dou.douaiagent.prompt.UserPrompt;
import com.dou.douaiagent.rag.LoveAppRagCustomAdvisorFactory;
import com.dou.douaiagent.rag.queryrewriter.QueryRewriter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Component
@Slf4j
public class LoveApp {

    private final ChatClient chatClient;
    private final SystemPrompt systemPrompt;

    private final UserPrompt userPrompt;

    /**
     * 初始话 chatClient  使用框架中的InMemoeryChatMemory
     * 2、也可以指定模型创建chatclient，下面就是这种方式。
     * 以阿里云百炼 的dashscopeChatModel为例
     * @param dashscopeChatModel 或千问 qwenChatModel
     */
//    public LoveApp(ChatModel dashscopeChatModel) {
//        //初始化基于内存的对话记忆
//        ChatMemory chatMemory = new InMemoryChatMemory();
//        chatClient = ChatClient.builder(dashscopeChatModel)//初始化指定的chatModel
//                .defaultSystem(SYSTEM_PROMPT)//设置系统提示词
//                .defaultAdvisors(//增加advisor拦截器
//                        MessageChatMemoryAdvisor.builder(chatMemory).order(2).build(),
//                        new MyLoggerAdvisor(),
//                        new SensitiveWordsAdvisor()
//                )
//                .build();
//    }


    /**
     * 初始话 chatClient，使用redis持久化对话消息
     * 2、也可以指定模型创建chatclient，下面就是这种方式。
     * 以阿里云百炼 的dashscopeChatModel为例
     * @param dashscopeChatModel 或千问 qwenChatModel
     * @param redisTemplate Redis模板
     * @param objectMapper JSON工具
     */
//    public LoveApp(ChatModel dashscopeChatModel, RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper,
//                   @Value("classpath:/prompts/systemPromptTemplate") Resource systemResource) throws IOException {
//        this.systemPromptText = systemResource.getContentAsString(java.nio.charset.StandardCharsets.UTF_8);
//        log.info("System prompt loaded, length={}", systemPromptText.length());
//
//        //初始化基于内存的对话记忆
//        ChatMemory chatMemory = new InRedisChatMemory(redisTemplate, objectMapper);
//        chatClient = ChatClient.builder(dashscopeChatModel)//初始化指定的chatModel
//                .defaultSystem(systemPromptText)//设置系统提示词
//                .defaultAdvisors(//增加advisor拦截器
//                        MessageChatMemoryAdvisor.builder(chatMemory).order(2).build(),
//                        new MyLoggerAdvisor(),
//                        new SensitiveWordsAdvisor()
//                )
//                .build();
//    }

    public LoveApp(ChatModel dashscopeChatModel, RedisTemplate<String, String> redisTemplate,
                   ObjectMapper objectMapper, SystemPrompt systemPrompt, UserPrompt userPrompt) {
        this.systemPrompt = systemPrompt;
        this.userPrompt = userPrompt;

        //初始化基于内存的对话记忆
        ChatMemory chatMemory = new InRedisChatMemory(redisTemplate, objectMapper);
        chatClient = ChatClient.builder(dashscopeChatModel)//初始化指定的chatModel
                .defaultSystem(systemPrompt.getSystemPromptText())//设置系统提示词
                .defaultAdvisors(//增加advisor拦截器
                        MessageChatMemoryAdvisor.builder(chatMemory).order(2).build(),
                        new MyLoggerAdvisor(),
                        new SensitiveWordsAdvisor()
                )
                .build();
    }


    /**
     * 初始话 chatClient
     * Spring AI 提供 Spring Boot 自动配置，创建原型 ChatClient.Builder Bean供你注入你的类
     * @param builder
     */
    /*public LoveApp(ChatClient.Builder builder) {
        //初始化基于内存的记忆对话
        ChatMemory chatMemory = new InMemoryChatMemory();
        chatClient = builder
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory)
                )
                .build();
    }*/


    /**
     * AI 基础对话（支持多轮对话记忆）
     * @param message
     * @param chatId
     * @return
     */
    public String doChat(String message, String chatId){
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)//设置用户提示词
                .advisors(advisor -> advisor.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))//这里的10条是关联上下文的会话条数
                .call()
                .chatResponse();

        String content = chatResponse.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }


    record LoveReport(String title, List<String> suggestions){

    }

    /**
     * AI 恋爱报告功能（实战结构化输出）
     * @param message
     * @param chatId
     * @return
     */
    public LoveReport doChatWhitReport(String message, String chatId){

        LoveReport loveReport = chatClient
                .prompt()
                .system(systemPrompt.getSystemPromptText())
                .user(userPrompt.getUserPrompt(message))//设置用户提示词
                .advisors(advisor -> advisor.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))//这里的10条是关联上下文的会话条数
                .call()
                .entity(LoveReport.class);

        log.info("loveReport: {}", loveReport);
        return loveReport;
    }


    /**
     * 引用向量数据库
     */
    @Resource
    private VectorStore loveAppVectorStore;

    @Resource
    private Advisor loveAppRagCloudAdvisor;

    @Resource
    private VectorStore loveAppPgVectorVectorStore;

    @Resource
    private QueryRewriter queryRewriter;

    /**
     * AI 基础对话（支持多轮对话记忆）
     * @param message
     * @param chatId
     * @return
     */
    public String doChatWithRag(String message, String chatId){

        String rewriteenMessage = queryRewriter.doQueryRewrite(message);

        ChatResponse chatResponse = chatClient
                .prompt()
                //使用改写后的查询
                .user(rewriteenMessage)//设置用户提示词
                .advisors(advisor -> advisor.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))//这里的10条是关联上下文的会话条数
                .advisors(new MyLoggerAdvisor())
                //应用 RAG 知识库问答，基于本地知识库（内存中加载了文档）
                //.advisors(new QuestionAnswerAdvisor(loveAppVectorStore))
                //应用 RAG 检索增强服务
                //.advisors(loveAppRagCloudAdvisor)
                //应用pg数据库检索增加服务，使用到了pgvector插件
                //.advisors(new QuestionAnswerAdvisor(loveAppPgVectorVectorStore))
                //调用自定义的 检索增强顾问，可以通过增加过滤条件提高返回准确度
                .advisors(
                        LoveAppRagCustomAdvisorFactory.createLoveAppRagCustomAdvisor(loveAppPgVectorVectorStore, "单身")
                )
                .call()
                .chatResponse();

        String content = chatResponse.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }
}

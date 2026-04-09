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
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;

import static com.alibaba.dashscope.app.AppKeywords.TOP_K;

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
        ChatMemory chatMemory = new InRedisChatMemory(redisTemplate, objectMapper, 10);
//        ChatMemory chatMemory = MessageWindowChatMemory.builder()
//                .chatMemoryRepository(new InMemoryChatMemoryRepository())
//                .maxMessages(10)//控制了历史对话记录
//                .build();
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
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(new InMemoryChatMemoryRepository())
                .maxMessages(10)
                .build();
        chatClient = builder
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
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
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, chatId))
//                        .param(TOP_K, 10))//这里的10条是关联上下文的会话条,这个参数貌似已失效
                .call()
                .chatResponse();

        String content = chatResponse.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }


    /**
     * 使用流式输出方式
     * @param message
     * @param chatId
     * @return Flux<String>
     */
    public Flux<String> doChatWhitStream(String message, String chatId){
       return chatClient
                .prompt()
                .user(message)//设置用户提示词
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, chatId))
                        //.param(TOP_K, 10))//这里的10条是关联上下文的会话条数。升级spring AI版本后，这个参数貌似没用了，在其他地方控制的历史会话次数
                        //如：MessageWindowChatMemory 初始化基于内存的对话记忆，记忆的对话条数是在这个类的maxMessages参数中设置的
                        //自定义的InRedisChatMemory，是基于redis缓存实现的历史对话记忆，该对象中的top_k是控制记忆对话条数的参数
                .stream()
                .content();
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
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, chatId))
                        //.param(TOP_K, 10))//这里的10条是关联上下文的会话条数
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
//                .user(message)//设置用户提示词
                .user(rewriteenMessage)
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, chatId))
                        //.param(TOP_K, 10))//这里的10条是关联上下文的会话条数
                .advisors(new MyLoggerAdvisor())
                //应用 RAG 知识库问答，基于本地知识库（内存中加载了文档）
                //.advisors(QuestionAnswerAdvisor.builder(loveAppVectorStore).build())
                //应用 RAG 知识库问答，基于本地知识库（内存中加载了文档），设置了返回条数，相似度阈值
//                .advisors(QuestionAnswerAdvisor.builder(loveAppVectorStore)
//                        .searchRequest(SearchRequest.builder()
//                                .topK(5)//从知识库中检索出来的文档条数
//                                .similarityThreshold(0.7)
//                                .build())
//                        .build())
                //应用 RAG 检索增强服务
                //.advisors(loveAppRagCloudAdvisor)
                //应用pg数据库检索增加服务，使用到了pgvector插件
                //.advisors(QuestionAnswerAdvisor.builder(loveAppPgVectorVectorStore).build())
                //调用自定义的 检索增强顾问，可以通过增加过滤条件提高返回准确度
                .advisors(
                        //支持多扩展查询 或 查询重写
                        //LoveAppRagCustomAdvisorFactory.createLoveAppRagCustomAdvisor(loveAppPgVectorVectorStore, "已婚", chatClient)
                        //未处理多扩展查询 或 查询重写
                        LoveAppRagCustomAdvisorFactory.createLoveAppRagCustomAdvisor(loveAppPgVectorVectorStore, "已婚")
                )
                .call()
                .chatResponse();

        String content = chatResponse.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }


    @Resource
    private ToolCallback[] callbacks;
    public String doChatWithTools(String message, String chatId){
        ChatResponse chatResponse = chatClient
                            .prompt()
                            .user(message)
                            .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, chatId))
                                    //.param(TOP_K, 10))
                            .advisors(new MyLoggerAdvisor())
                            //设置可以调用的工具
                            .tools(callbacks)
                            .call()
                            .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;

    }



    @Resource
    private ToolCallbackProvider toolCallbackProvider;
    public String doChatWithMCP(String message, String chatId){
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, chatId))
                        //.param(TOP_K, 10))
                .advisors(new MyLoggerAdvisor())
                //设置可以调用的工具
                .tools(toolCallbackProvider)
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;

    }
}

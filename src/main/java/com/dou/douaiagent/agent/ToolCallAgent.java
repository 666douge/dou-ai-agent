package com.dou.douaiagent.agent;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.dou.douaiagent.agent.model.AgentState;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.tool.ToolCallback;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * 处理工具调用的基础代理类，具体实现了think 和 act 方法，可以用作创建实例的父类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class ToolCallAgent extends ReActAgent{

    //所有可用的工具
    private ToolCallback[] avaliabletools;

    //保存调用工具信息的响应结果（要调用哪些工具）
    private ChatResponse toolCallChatResponse;

    //工具调用管理者
    private final ToolCallingManager toolCallingManager;

    //禁用spring AI 内置的工具调用机制，自己维护选项和消息上下文
    private final ChatOptions chatOptions;

    public ToolCallAgent(ToolCallback[] avaliabletools){
        super();
        this.avaliabletools = avaliabletools;
        this.toolCallingManager = ToolCallingManager.builder().build();
        //禁用spring AI 的内部调用机制，自己维护选项和消息上下文
        //因为阿里云（DashScope）大模型对spring AI框架兼容性有些差异，不能直接使用spring AI的官方写法
        this.chatOptions = DashScopeChatOptions.builder()
                .withProxyToolCalls(true)
                .build();

          //下面spring AI官方 的 禁用内部调用机制 的写法
//        ChatOptions chatOptions = ToolCallingChatOptions.builder()
//                .toolCallbacks(new CustomerTools())
//                .internalToolExecutionEnabled(false) //spring AI官方的写法，设置为false就是手动控制方法调用，spring AI就不会自动调用工具了
//                .build();
//        Prompt prompt = new Prompt("Tell me more about the customer with ID 42", chatOptions);

    }

    /**
     * 处理当前状态，并判断是否需要执行下一步行动
     * @return 思考结果：是否需要执行行动
     */
    @Override
    public boolean think() {
        //1、校验提示词，拼接用户提示词
        if(StrUtil.isNotBlank(getNextStepPrompt())){
            UserMessage userMessage = new UserMessage(getNextStepPrompt());
            getMessageList().add(userMessage);
        }

        //2、调用AI大模型，获取模型调用结果
        List<Message> messageList = getMessageList();
        //检查是否存在有需要用户介入回答的问题
        checkIfAskHuman(messageList);

        //传入chatOptions的作用是告诉Spring AI由我们来手工执行工具
        Prompt prompt = new Prompt(messageList, chatOptions);
        try {
            ChatResponse chatResponse = getChatClient().prompt(prompt)
                    .system(getSystenPrompt())
                    .tools(avaliabletools)
                    .call()
                    .chatResponse();

            //记录响应，用于等下 Act
            this.toolCallChatResponse = chatResponse;

            //3、解析模型调用结果，获取要调用的工具
            //助手消息，调用大模型返回的消息，返回了大模型输出的提示信息和要调用的工具
            AssistantMessage assistantMessage = chatResponse.getResult().getOutput();
            //获取要调用的工具列表
            List<AssistantMessage.ToolCall> toolCallList = assistantMessage.getToolCalls();
            //本次调用模型输出提示信息
            String assitansText = assistantMessage.getText();

            if(isStuck(assitansText)){
                log.info("==观察到重复响应。考虑新策略，避免重复已尝试过的无效路径。==");
                handleStuckState();
                return false;
            }

            log.info(getName() + "的思考： " + assitansText);
            log.info(getName() + "选择了" + toolCallList.size() + "个工具来使用");
            String toolcallinfo = toolCallList.stream().map(toolCall -> String.format("工具名称：%s，参数%s", toolCall.name(), toolCall.arguments()))
                    .collect(Collectors.joining("\n"));

            log.info("调用的工具： \n" + toolcallinfo);
            //如果不需要调用工具，返回false
            if(toolCallList.isEmpty()){
                //只有不调用工具的时候，才需要手工记录助手消息
                getMessageList().add(assistantMessage);
                return false;
            } else {
                //需要调用工具时，无需手工记录助手消息，因为调用工具是会自动记录
                return true;
            }
        } catch (Exception e) {
            log.error(getName() + "的思考过程中遇到了问题：" + e.getMessage());
            getMessageList().add(new AssistantMessage("处理时遇到了错误: " + e.getMessage()));
            return false;
        }
    }

    /**
     * 增加人工介入 ask_human
     * @param messageList
     * @return
     */
    protected boolean checkIfAskHuman(List<Message> messageList){
        Message message = CollUtil.getLast(messageList);
        for(Message curMessage : messageList){
            if(curMessage instanceof AssistantMessage){
                message = curMessage;
            }
        }
        if(message instanceof AssistantMessage){
            String context = message.getText();
            if(StrUtil.isNotBlank(context) && context.contains("[ASK_USER]")){
                //提取问题
                String question = context.substring(context.indexOf("[ASK_USER]") + 10);
                //向用户输出问题
                System.out.println("智能体需要你的帮助：" + question);

                //获取用户输入
                Scanner scanner = new Scanner(System.in);
                String userAnswer = scanner.nextLine();

                //添加用户回答到消息列表
                UserMessage userMessage = new UserMessage("用户回答：" + userAnswer);
                getMessageList().add(userMessage);
                //继续思考
                return true;
            }
        }
        return true;
    }

    /**
     * 检查代理是否进入死循环，通过大模型返回的消息和 历史上下文判断是否出现重复的提示词（一定次数限制）
     *
     * @param currenResultAssistantMessageText
     * @return
     */
    protected boolean isStuck(String currenResultAssistantMessageText){
        List<Message> messageList = getMessageList();
        //如果上下文消息中少于设定的最大可重复条数的阈值，说明任务才刚开始，不用校验
        if(messageList.size() < this.getMaxDuplicateCounts()){
            return false;
        }
        //校验当前提示词在历史上下文中出现的次数
        int duplicateCount = 0;
        log.info("==========currenResultAssistantMessageText======: " + currenResultAssistantMessageText);
        for(Message message : messageList){
            if(message instanceof AssistantMessage){
                log.info("==========message.getText======: " + message.getText());
                if(message.getText().equals(currenResultAssistantMessageText)){
                    duplicateCount++;
                }

            }
        }
        return duplicateCount >= this.getMaxDuplicateCounts();
    }

    /**
     * 检查到循环调佣后的处理方法
     */
    protected void handleStuckState(){
        String stuckPrompt = "观察到重复响应。考虑新策略，避免重复已尝试过的无效路径。";
        String nextPrompt = stuckPrompt + "\n" + (StrUtil.isBlank(this.getNextStepPrompt()) ? "" : this.getNextStepPrompt());
        getMessageList().add(new AssistantMessage(nextPrompt));
    }

    /**
     * 执行调用工具并处理结果
     * @return 工具调用结果
     */
    @Override
    public String act() {
        if(!toolCallChatResponse.hasToolCalls()){
            return "没有工具需要调用";
        }
        Prompt prompt = new Prompt(getMessageList(), this.chatOptions);
        //调用工具（实现手动工具调用）， 根据 对话上下文 和 要调用的工具的响应对象 解析出要调用的工具
        ToolExecutionResult toolExecutionResult = toolCallingManager.executeToolCalls(prompt, toolCallChatResponse);

        //记录消息上下文，conversationHistory 已经包含了助手消息和工具调用返回的结果
        setMessageList(toolExecutionResult.conversationHistory());
        ToolResponseMessage toolResponseMessage = (ToolResponseMessage)CollUtil.getLast(toolExecutionResult.conversationHistory());
        //是否调用了终止工具
        boolean terminateToolCalled = toolResponseMessage.getResponses().stream()
                .anyMatch(toolResponse -> toolResponse.name().equals("doTerminate"));
        //如果调用了终止工具，就需要把状态改为finished
        if(terminateToolCalled){
            //任务结束，更新任务状态
            setState(AgentState.FINISHED);
        }
        //解析调用工具返回的结果
        String results = toolResponseMessage.getResponses().stream()
                .map(toolResponse -> "调用工具 " + toolResponse.name() + ", 返回结果：" + toolResponse.responseData())
                .collect(Collectors.joining("\n"));


        log.info(results);
        return results;
    }
}

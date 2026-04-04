package com.dou.douaiagent.agent;

import com.dou.douaiagent.advisor.MyLoggerAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Component;

/**
 * Dou 的 AI 超级智能体（拥有自主规划能力）
 */
@Component
public class DouManus extends  ToolCallAgent{


    public DouManus(ToolCallback[] avaliabletools, ChatModel dashscopeChatModel) {
        super(avaliabletools);
        this.setName("DouAgent");
        String  SYSTEM_PROMPT =
                "You are OpenManus, an all-capable AI assistant, aimed at solving any task presented by the user. " +
                "You have various tools at your disposal that you can call upon to efficiently complete complex requests. ";
        this.setSystenPrompt(SYSTEM_PROMPT);

        String NEXT_STEP_PROMPT = "Based on user needs, proactively select the most appropriate tool " +
                "or combination of tools. For complex tasks, you can break down the problem and use " +
                "different tools step by step to solve it. After using each tool, clearly explain the " +
                "execution results and suggest the next steps." +
                "If you want to stop the interaction at any point, use the `terminate` tool/function call.";
        this.setNextStepPrompt(NEXT_STEP_PROMPT);
        this.setMaxSteps(15);
        //初始化ChatClient
        ChatClient chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultAdvisors(new MyLoggerAdvisor())
                .build();
        this.setChatClient(chatClient);
        this.setMaxDuplicateCounts(2);
    }
}

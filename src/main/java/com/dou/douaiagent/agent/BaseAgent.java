package com.dou.douaiagent.agent;

import cn.hutool.core.util.StrUtil;
import com.dou.douaiagent.agent.model.AgentState;
import io.netty.util.concurrent.CompleteFuture;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 抽象基础代理类，用于管理代理状态和执行流程
 *
 * 提供状态转换，内存管理和基于步骤的执行循环的基础功能
 * 子类必须实现step方法
 */

@Data
@Slf4j
public abstract class BaseAgent {

    //核心属性
    private String name;

    //提示词
    private String systenPrompt;
    private String nextStepPrompt;

    //代理状态
    private AgentState state = AgentState.IDLE;

    //执行步骤控制
    private int currentStep;
    private int maxSteps;

    //LLM 大模型
    private ChatClient chatClient;

    //重复请求次数阈值（控制死循环）
    private int maxDuplicateCounts;


    //memory 记忆（需要自主维护会话上下文）
    private List<Message> messageList = new ArrayList<>();

    /**
     * 运行代理
     * @param userPrompt
     * @return 运行结果
     */
    public String run(String userPrompt){
        SseEmitter sseEmitter = new SseEmitter(300000L);
        //1、基础校验
        //判断代理状态是否为空闲，如果不是空闲状态，不可执行
        if(state !=  AgentState.IDLE){
            throw new RuntimeException("Cannot run from state:" + state);
        }
        //判断用户提示词是否为空，为空则不运行agent
        if(StrUtil.isBlank(userPrompt)){
            throw new RuntimeException("Cannot run agent with empty userPrompt");
        }

        //2、执行，更改状态
        //更改代理状态为running
        this.state = AgentState.RUNNING;
        //添加用户提示词，管理会话上下文
        messageList.add(new UserMessage(userPrompt));
        //保存结果列表
        List<String> results = new ArrayList<>();

        try {
            for(int i=0; i < maxSteps && state != AgentState.FINISHED; i++){
                int stepNumber = i + 1;
                currentStep = stepNumber;
                log.info("Executing step {}/{} " , currentStep, maxSteps);
                //单步执行结果
                String stepResult = step();
                String result = "Step " + stepNumber + ": " + stepResult;
                results.add(result);
            }
            //检查是否超过了最大执行次数
            if(currentStep >= maxSteps){
                this.state = AgentState.FINISHED;
                results.add("Terminated: Reache max steps (" + maxSteps + ")");
            }
            return String.join("\n", results);
        } catch (Exception e) {
            this.state = AgentState.ERROR;
            log.error("Error executing agent", e);
            return "执行错误：" +  e.getMessage();
        } finally {
            //3、清理资源
            this.cleanup();
        }
    }


    /**
     * 运行代理(流式输出)
     * @param userPrompt
     * @return 运行结果
     */
    public SseEmitter runStream(String userPrompt){
        SseEmitter sseEmitter = new SseEmitter(300000L);//5分钟超时
        //使用线程异步处理，避免阻塞主线程
        CompletableFuture.runAsync(() -> {
            //1、基础校验
            //判断代理状态是否为空闲，如果不是空闲状态，不可执行
            try {
                if(state !=  AgentState.IDLE){
                    sseEmitter.send("错误：不能从当前状态运行代理：" + state);
                    sseEmitter.complete();
                    return;
                }
                //判断用户提示词是否为空，为空则不运行agent
                if(StrUtil.isBlank(userPrompt)){
                    sseEmitter.send("错误：提示词为空，不能运行代理");
                    sseEmitter.complete();
                    return;
                }
            } catch (IOException e) {
                sseEmitter.completeWithError(e);
            }

            //2、执行，更改状态
            //更改代理状态为running
            this.state = AgentState.RUNNING;
            //添加用户提示词，管理会话上下文
            messageList.add(new UserMessage(userPrompt));
            //保存结果列表
            List<String> results = new ArrayList<>();

            try {
                for(int i=0; i < maxSteps && state != AgentState.FINISHED; i++){
                    int stepNumber = i + 1;
                    currentStep = stepNumber;
                    log.info("Executing step {}/{} " , currentStep, maxSteps);
                    //单步执行结果
                    String stepResult = step();
                    String result = "Step " + stepNumber + ": " + stepResult;
                    results.add(result);
                    //输出当前每一步的结果到 SSE
                    sseEmitter.send(result);
                }
                //检查是否超过了最大执行次数
                if(currentStep >= maxSteps){
                    this.state = AgentState.FINISHED;
                    results.add("Terminated: Reache max steps (" + maxSteps + ")");
                    sseEmitter.send("执行结束，执行已达到执行次数（"+maxSteps+"）");
                }
                sseEmitter.complete();
            } catch (Exception e) {
                this.state = AgentState.ERROR;
                log.error("Error executing agent", e);
                try {
                    sseEmitter.send("执行错误：" + e.getMessage());
                    sseEmitter.complete();
                } catch (IOException ex) {
                    sseEmitter.completeWithError(e);
                }
                //return "执行错误：" +  e.getMessage();
            } finally {
                //3、清理资源
                this.cleanup();
            }
         });

        //设置超时输出
        sseEmitter.onTimeout(() -> {
            this.state = AgentState.ERROR;
            this.cleanup();
            log.error("SSE connection timeout");
        });

        //这是完成回调
        sseEmitter.onCompletion(() ->{
            this.state = AgentState.FINISHED;
            this.cleanup();
            log.info("SSE connection completed");
        });

        return sseEmitter;
    }


    /**
     * 定义单个步骤
     */
    public abstract String step();


    /**
     * 清理资源
     */
    public void cleanup(){
        //子类可以重写此方法
    }



}

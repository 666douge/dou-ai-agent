package com.dou.douaiagent.demo.invoke;// 建议dashscope SDK的版本 >= 2.12.0
import java.util.Arrays;
import java.lang.System;
import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.utils.JsonUtils;

/**
 * 阿里云 灵积 SDK 调用
 *
 * 使用cursor开发工具将下面格式的内容转换为java代码
 *
 * 提示词：
 * 将下面的代码转换为hutool格式的java代码
 * curl --location "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation" \
 * --header "Authorization: Bearer $DASHSCOPE_API_KEY" \
 * --header "Content-Type: application/json" \
 * --data '{
 *     "model": "qwen-plus",
 *     "input":{
 *         "messages":[
 *             {
 *                 "role": "system",
 *                 "content": "You are a helpful assistant."
 *             },
 *             {
 *                 "role": "user",
 *                 "content": "你是谁？"
 *             }
 *         ]
 *     },
 *     "parameters": {
 *         "result_format": "message"
 *     }
 * }'
 *
 */
public class SdkAiInvoke {
    public static GenerationResult callWithMessage() throws ApiException, NoApiKeyException, InputRequiredException {
        Generation gen = new Generation();
        Message systemMsg = Message.builder()
                .role(Role.SYSTEM.getValue())
                .content("You are a helpful assistant.")
                .build();
        Message userMsg = Message.builder()
                .role(Role.USER.getValue())
                .content("你好，我是程序员逗哥，正在学习AI应用开发！")
                .build();
        GenerationParam param = GenerationParam.builder()
                // 若没有配置环境变量，请用百炼API Key将下行替换为：.apiKey("sk-xxx")
                .apiKey(TestAPIKey.api_key)
                // 此处以qwen-plus为例，可按需更换模型名称。模型列表：https://help.aliyun.com/zh/model-studio/getting-started/models
                .model("qwen-plus")
                .messages(Arrays.asList(systemMsg, userMsg))
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .build();
        return gen.call(param);
    }
    public static void main(String[] args) {
        try {
            GenerationResult result = callWithMessage();
            System.out.println(JsonUtils.toJson(result));
        } catch (ApiException | NoApiKeyException | InputRequiredException e) {
            // 使用日志框架记录异常信息
            System.err.println("An error occurred while calling the generation service: " + e.getMessage());
        }
        System.exit(0);
    }
}
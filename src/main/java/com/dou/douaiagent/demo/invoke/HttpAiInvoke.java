package com.dou.douaiagent.demo.invoke;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;

public class HttpAiInvoke {

    public static void main(String[] args) {
        // 设置API Key
        String apiKey = TestAPIKey.api_key;
        
        // 构建请求体
        JSONObject requestBody = new JSONObject()
                .set("model", "qwen-plus")
                .set("input", new JSONObject()
                        .set("messages", new JSONObject[]{
                                new JSONObject().set("role", "system").set("content", "You are a helpful assistant."),
                                new JSONObject().set("role", "user").set("content", "你好，我是程序员逗哥，正在学习AI应用开发！")
                        }))
                .set("parameters", new JSONObject()
                        .set("result_format", "message"));
        
        // 发送请求
        HttpResponse response = HttpRequest.post("https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation")
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .body(requestBody.toString())
                .timeout(30000)
                .execute();
        
        // 处理响应
        System.out.println("Status: " + response.getStatus());
        System.out.println("Body: " + response.body());
    }
}
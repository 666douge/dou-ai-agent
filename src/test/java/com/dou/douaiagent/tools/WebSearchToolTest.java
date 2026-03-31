package com.dou.douaiagent.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class WebSearchToolTest {


    @Value("${search-api.api-key}")
    private String apiKey;

    @Test
    void search() {
        WebSearchTool tool = new WebSearchTool(apiKey);
        String query = "用友网络与九恒星两家公司的营业收入对比";
        String result = tool.searchWeb(query);
        Assertions.assertNotNull(result);
    }

}
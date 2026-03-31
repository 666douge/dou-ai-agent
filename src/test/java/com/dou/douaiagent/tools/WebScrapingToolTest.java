package com.dou.douaiagent.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WebScrapingToolTest {

    @Test
    void scrapeWebPage() {
        WebScrapingTool tool = new WebScrapingTool();
        String url = "https://www.codefather.cn/course/1915010091721236482/section/1920794055716278274";
        String content = tool.scrapeWebPage(url);
        Assertions.assertNotNull(content);

    }
}
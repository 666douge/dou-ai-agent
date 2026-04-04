package com.dou.douaiagent.agent;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class DouManusTest {


    @Resource
    private DouManus douManus;

    @Test
    public void testDouManus(){
        String prompt = "我的另一半在成都新津区，帮我找一下适合约会的地方，并结合一些网络图片，指定一份详细的计划，并输出保存的PDF文件。";

        String result = douManus.run(prompt);
        Assertions.assertNotNull(result);
    }
}
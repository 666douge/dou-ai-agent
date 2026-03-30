package com.dou.douaiagent.demo.rag;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MyMutiQueryExpenderTest {

    @Resource
    private MyMutiQueryExpender myMutiQueryExpender;

    @Test
    void expende() {
        String message = "逗哥是啥啊啊啊啊啊啊？";
        myMutiQueryExpender.expende(message);

    }
}
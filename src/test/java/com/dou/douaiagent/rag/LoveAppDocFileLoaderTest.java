package com.dou.douaiagent.rag;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LoveAppDocFileLoaderTest {


    @Resource
    private LoveAppDocFileLoader loveAppDocFileLoader;

    @Test
    void loadDocFiles() {
        loveAppDocFileLoader.loadDocFiles();

    }
}
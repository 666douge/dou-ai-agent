package com.dou.douimagesearchmcpserver.tools;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SearchImageToolTest {

    @Resource
    private SearchImageTool searchImageTool;
    @Test
    void searchImagesfromWeb() {
       String result = searchImageTool.searchImagesfromWeb("computer");
       System.out.print(result);
        Assertions.assertNotNull(result);

    }
}
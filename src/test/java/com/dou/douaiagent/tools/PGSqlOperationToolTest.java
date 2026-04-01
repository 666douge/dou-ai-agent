package com.dou.douaiagent.tools;

import com.dou.douaiagent.mapper.LoveAppMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PGSqlOperationToolTest {

    @Resource
    private LoveAppMapper loveAppMapper;

    @Test
    void add() {
        PGSqlOperationTool tool = new PGSqlOperationTool(loveAppMapper);
        String result = tool.add("张三", 25, "男", "他是一个程序员");
        assertNotNull(result);
        assertTrue(result.contains("successfully") || result.contains("failed"));
    }
}
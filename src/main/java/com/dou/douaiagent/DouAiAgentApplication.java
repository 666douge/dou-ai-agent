package com.dou.douaiagent;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.dou.douaiagent.mapper")
public class DouAiAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(DouAiAgentApplication.class, args);
    }

}

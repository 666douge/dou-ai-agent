package com.dou.douimagesearchmcpserver;

import com.dou.douimagesearchmcpserver.tools.SearchImageTool;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallback;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Scanner;

@SpringBootApplication
public class DouImageSearchMcpServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DouImageSearchMcpServerApplication.class, args);
        new Scanner(System.in).nextLine();
    }

    @Bean
    public ToolCallbackProvider iamgeSearchTool(SearchImageTool searchImageTool){
            return MethodToolCallbackProvider.builder()
                    .toolObjects(searchImageTool)
                    .build();
    }
}

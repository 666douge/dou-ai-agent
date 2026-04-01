package com.dou.douaiagent.tools;

import com.dou.douaiagent.mapper.LoveAppMapper;
import jakarta.annotation.Resource;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbacks;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ToolRegistration {

    @Value("${search-api.api-key}")
    private String apikey;

    @Resource
    private LoveAppMapper lovaAppMapper;

    @Bean
    public ToolCallback[] alltools(){
      FileOperationTool fileOperationTool = new FileOperationTool();
      WebSearchTool webSearchTool = new WebSearchTool(apikey);
      WebScrapingTool webScrapingTool = new WebScrapingTool();
      TerminalOperationTool terminalOperationTool = new TerminalOperationTool();
      ResourceDownloadTool resourceDownloadTool = new ResourceDownloadTool();
      PDFGenerationTool pdfGenerationTool = new PDFGenerationTool();
      PGSqlOperationTool pgSqlOperationTool = new PGSqlOperationTool(lovaAppMapper);
      return ToolCallbacks.from(fileOperationTool,
              webSearchTool,
              webScrapingTool,
              terminalOperationTool,
              resourceDownloadTool,
              pdfGenerationTool,
              pgSqlOperationTool);

    }

}

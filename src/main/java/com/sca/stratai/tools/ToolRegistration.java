package com.sca.stratai.tools;

import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbacks;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 工具注册配置类
 */
@Configuration
public class ToolRegistration {

    @Value("${search-api.api-key}")
    private String searchApiKey;

    /**
     * 创建并配置所有需要的工具回调对象
     * 该方法初始化各种工具实例，并将它们组合成一个工具回调数组
     * 每个工具都有其特定的功能，如文件操作、网络搜索、网页抓取、资源下载、终端操作和PDF生成
     *
     * @return ToolCallback 包含所有工具回调的数组
     */
    @Bean
    public ToolCallback[] allTools() {
        // 创建文件操作工具实例
        FileOperationTool fileOperationTool = new FileOperationTool();
        // 创建网络搜索工具实例，需要传入搜索API密钥
        WebSearchTool webSearchTool = new WebSearchTool(searchApiKey);
        // 创建网页抓取工具实例
        WebScrapingTool webScrapingTool = new WebScrapingTool();
        // 创建资源下载工具实例
        ResourceDownloadTool resourceDownloadTool = new ResourceDownloadTool();
        // 创建终端操作工具实例
        TerminalOperationTool terminalOperationTool = new TerminalOperationTool();
        // 创建PDF生成工具实例
        PDFGenerationTool pdfGenerationTool = new PDFGenerationTool();
        TerminateTool terminateTool = new TerminateTool();
        // 将所有工具实例转换为工具回调数组并返回
        return ToolCallbacks.from(
                fileOperationTool,
                webSearchTool,
                webScrapingTool,
                resourceDownloadTool,
                terminalOperationTool,
                pdfGenerationTool,
                terminateTool
        );
    }

}

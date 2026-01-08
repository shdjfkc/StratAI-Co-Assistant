package com.sca.imagesearchmcpserver;

import com.sca.imagesearchmcpserver.tools.ImageSearchTool;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ImageSearchMcpServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImageSearchMcpServerApplication.class, args);
    }

/**
 * 创建并配置一个图像搜索工具的回调提供者
 * 该方法将ImageSearchTool工具对象包装为MethodToolCallbackProvider
 *
 * @param imageSearchTool 图像搜索工具的具体实现实例
 * @return 配置好的ToolCallbackProvider，用于提供图像搜索功能的回调方法
 */
    @Bean
    public ToolCallbackProvider imageSearchTools(ImageSearchTool imageSearchTool) { // 定义一个名为imageSearchTools的Bean方法
        return MethodToolCallbackProvider.builder() // 使用建造者模式创建MethodToolCallbackProvider实例
                .toolObjects(imageSearchTool) // 设置工具对象为传入的imageSearchTool
                .build(); // 完成构建并返回实例
    }



}

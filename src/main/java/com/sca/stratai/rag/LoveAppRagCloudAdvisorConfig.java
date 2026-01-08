package com.sca.stratai.rag;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetriever;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetrieverOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 基于阿里云知识库服务的RAG增强顾问
 * 该类负责配置一个Advisor bean，用于提供基于知识库的检索增强建议功能
 */
@Configuration
@Slf4j
class LoveAppRagCloudAdvisorConfig {

    @Value("${spring.ai.dashscope.api-key}")
    private String dashScopeApiKey;

    /**
     * 创建一个名为loveAppRagCloudAdvisor的Bean方法，用于构建一个检索增强建议器
     * 该方法使用了DashScopeApi和DashScopeDocumentRetriever来实现文档检索功能
     *
     * @return 返回一个配置好的Advisor实例，用于提供基于知识库的检索增强建议
     */
    @Bean
    public Advisor loveAppRagCloudAdvisor() {
        // 创建DashScopeApi实例，使用提供的API密钥进行初始化
        DashScopeApi dashScopeApi = new DashScopeApi(dashScopeApiKey);
        // 定义知识库名称常量，用于指定要检索的知识库
        final String KNOWLEDGE_INDEX = "恋爱大师";
        // 创建DashScopeDocumentRetriever实例，用于从指定知识库中检索文档
        // 使用构建器模式配置检索选项，包括设置知识库名称 文档检索器
        DocumentRetriever documentRetriever = new DashScopeDocumentRetriever(dashScopeApi,
                DashScopeDocumentRetrieverOptions.builder()
                        .withIndexName(KNOWLEDGE_INDEX)
                        .build());
        // 构建并返回一个RetrievalAugmentationAdvisor实例 检索增强顾问
        // 该实例使用配置好的文档检索器来提供检索增强建议功能
        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(documentRetriever)
                .build();
    }
}




















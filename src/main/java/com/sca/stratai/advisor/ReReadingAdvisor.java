package com.sca.stratai.advisor;

import org.springframework.ai.chat.client.advisor.api.*;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;

/**
 * 自定义 Re2 Advisor
 * 可提高大型语言模型的推理能力
 */
public class ReReadingAdvisor implements CallAroundAdvisor, StreamAroundAdvisor {


/**
 * 在请求处理前对AdvisedRequest进行预处理的方法
 * @param advisedRequest 原始的AdvisedRequest对象
 * @return 处理后的AdvisedRequest对象
 */
    private AdvisedRequest before(AdvisedRequest advisedRequest) {

        // 创建一个新的HashMap，用于存储用户参数
        Map<String, Object> advisedUserParams = new HashMap<>(advisedRequest.userParams());
        // 将用户输入的问题添加到参数中，键为"re2_input_query"
        advisedUserParams.put("re2_input_query", advisedRequest.userText());

        // 使用构建器模式创建新的AdvisedRequest对象
        // 设置用户文本为模板格式，其中包含两次用户输入的问题
        // 将更新后的参数传递给构建器
        return AdvisedRequest.from(advisedRequest)
                .userText("""
                        {re2_input_query}
                        Read the question again: {re2_input_query}
                        """)
                .userParams(advisedUserParams)
                .build();
    }

    @Override
    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        return chain.nextAroundCall(this.before(advisedRequest));
    }

    @Override
    public Flux<AdvisedResponse> aroundStream(AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {
        return chain.nextAroundStream(this.before(advisedRequest));
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }
}

package com.sca.stratai.agent;

import com.sca.stratai.advisor.MyLoggerAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Component;

@Component
public class AgManus extends ToolCallAgent {
  
    /**
     * AgManus类的构造函数，用于初始化AgManus实例
     * @param allTools 所有可用的工具回调数组
     * @param dashscopeChatModel DashScope聊天模型，用于构建聊天客户端
     */
    public AgManus(ToolCallback[] allTools, ChatModel dashscopeChatModel) {
        super(allTools);    // 调用父类ToolCallAgent的构造函数
        // 设置代理名称
        this.setName("agManus");
        // 设置系统提示，定义AI助手的身份和可用工具
        String SYSTEM_PROMPT = """  
                You are AgManus, an all-capable AI assistant, aimed at solving any task presented by the user.  
                You have various tools at your disposal that you can call upon to efficiently complete complex requests.  
                """;  
        this.setSystemPrompt(SYSTEM_PROMPT);  
        // 设置下一步操作提示，指导AI如何选择和使用工具
        String NEXT_STEP_PROMPT = """  
                Based on user needs, proactively select the most appropriate tool or combination of tools.  
                For complex tasks, you can break down the problem and use different tools step by step to solve it.  
                After using each tool, clearly explain the execution results and suggest the next steps.  
                If you want to stop the interaction at any point, use the `terminate` tool/function call.  
                """;  
        this.setNextStepPrompt(NEXT_STEP_PROMPT);  
        // 设置最大步骤数，限制AI助手的操作次数
        this.setMaxSteps(20);
        // 初始化客户端  
        ChatClient chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultAdvisors(new MyLoggerAdvisor())
                .build();  
        this.setChatClient(chatClient);  
    }  
}

package com.sca.stratai.app;

import com.sca.stratai.advisor.MyLoggerAdvisor;

import com.sca.stratai.chatmemory.FileBasedChatMemory;

import com.sca.stratai.rag.QueryRewriter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;

import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Component
@Slf4j
public class LoveApp {

    private final ChatClient chatClient;

    private static final String SYSTEM_PROMPT = "扮演深耕恋爱心理领域的专家。开场向用户表明身份，告知用户可倾诉恋爱难题。" +
            "围绕单身、恋爱、已婚三种状态提问：单身状态询问社交圈拓展及追求心仪对象的困扰；" +
            "恋爱状态询问沟通、习惯差异引发的矛盾；已婚状态询问家庭责任与亲属关系处理的问题。" +
            "引导用户详述事情经过、对方反应及自身想法，以便给出专属解决方案。";

    @Resource
    private VectorStore loveAppVectorStore;

   @Resource
   private Advisor loveAppRagCloudAdvisor;

    @Resource
    private VectorStore pgVectorVectorStore;

    @Resource
    private QueryRewriter queryRewriter;

    @Resource
    private ToolCallback[] allTools;

    //ai调用MCP服务
    @Resource
    private ToolCallbackProvider toolCallbackProvider;

    /**
     * 初始化 LoveApp 应用
     *
     * @param dashscopeChatModel Dashscope 聊天模型
     */
    public LoveApp(ChatModel dashscopeChatModel) {
        // 初始化基于文件的对话记忆
        String fileDir = System.getProperty("user.dir") + "/tmp/chat-memory";
        ChatMemory chatMemory = new FileBasedChatMemory(fileDir);
//        // 初始化基于内存的对话记忆
//        ChatMemory chatMemory = new InMemoryChatMemory();
        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory),
                        //自定义日志记录
                        new MyLoggerAdvisor()
//                        //自定义推理增强 Advisor可按需开启
//                        new ReReadingAdvisor()
                )
                .build();
    }

    /**
     * AI 基础对话（支持多轮对话）
     *
     * @param message 聊天消息
     * @param chatId  聊天会话ID
     * @return 聊天回复
     */

/**
 * doChat 方法 - 处理与AI的聊天对话
 * 该方法通过chatClient实现与AI的多轮对话功能
 *
 * @param message 用户输入的聊天消息内容
 * @param chatId  用于标识聊天会话的唯一ID，用于维护多轮对话上下文
 * @return 返回AI生成的聊天回复内容
 */
    public String doChat(String message, String chatId) {
    // 使用chatClient构建并执行对话请求
        ChatResponse response = chatClient
                .prompt()  // 开始构建提示
                .user(message)  // 添加用户消息
            // 配置对话顾问参数，包括会话ID和检索历史消息数量
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()  // 执行对话调用
                .chatResponse();  // 获取聊天响应对象
    // 从响应中提取AI生成的文本内容
        String content = response.getResult().getOutput().getText();
    // 记录AI回复内容到日志
        log.info("content: {}", content);
    // 返回聊天回复内容
        return content;
    }


    /**
     * AI 基础对话（支持多轮对话） SSE 流模式
     *
     * @param message 聊天消息
     * @param chatId  聊天会话ID
     * @return 聊天回复
     */

    /**
     * doChat 方法 - 处理与AI的聊天对话
     * 该方法通过chatClient实现与AI的多轮对话功能
     *
     * @param message 用户输入的聊天消息内容
     * @param chatId  用于标识聊天会话的唯一ID，用于维护多轮对话上下文
     * @return 返回AI生成的聊天回复内容
     */
    public Flux<String> doChatByStream(String message, String chatId) {
        return chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .stream()
                .content();
    }




    /**
     * LoveRecord类是一个记录，用于存储爱情报告的相关信息
     * 它包含两个字段：标题和建议列表
     */
    record LoveReport(String title, List<String> suggestions) {
        // 这是一个记录类型(Record)的声明
        // 记录是Java 14中引入的一种特殊类，主要用于封装不可变的数据
        // 它自动提供了equals()、hashCode()、toString()和构造器等方法
        // title字段表示爱情报告的标题
        // 类型为String
        // suggestions字段表示爱情报告的建议列表
        // 类型为List<String>，包含多个字符串元素
    }

    /**
     * AI 恋爱报告功能（结构化输出）
     * 该方法用于处理用户消息并生成恋爱报告
     *
     * @param message 用户输入的消息内容
     * @param chatId  聊天会话的唯一标识符
     * @return LoveReport 包含恋爱建议的报告对象
     */
    public LoveReport doChatWithReport(String message, String chatId) {
        // 创建聊天客户端并构建提示
        LoveReport loveReport = chatClient
                .prompt() // 开始构建提示
                .system(SYSTEM_PROMPT + "每次对话后都要生成恋爱结果，标题为{用户名}的恋爱报告，内容为建议列表") // 设置系统提示
                .user(message) // 添加用户消息
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId) // 配置顾问参数
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10)) // 设置记忆检索大小
                .call() // 执行调用
                .entity(LoveReport.class); // 将结果转换为LoveReport对象
        // 记录生成的恋爱报告信息
        log.info("loveReport: {}", loveReport);
        // 返回生成的恋爱报告
        return loveReport;
    }


    /**
     * 执行基于RAG（检索增强生成）的聊天对话
     *
     * @param message 用户输入的消息内容
     * @param chatId  聊天会话ID，用于记忆上下文
     * @return AI助手的回复内容
     */
    public String doChatWithRag(String message, String chatId) {

        // 查询重写
        String rewrittenMessage = queryRewriter.doQueryRewrite(message);
        // 构建聊天请求并获取响应
        ChatResponse chatResponse = chatClient
                .prompt()  // 创建提示构建器
                //使用改写后的查询
                .user(rewrittenMessage)  // 设置用户输入的消息
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)  // 设置会话ID用于记忆
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                // 开启日志，便于观察效果    设置记忆检索大小为10条
                .advisors(new MyLoggerAdvisor())
                // 应用知识库问答
                .advisors(new QuestionAnswerAdvisor(loveAppVectorStore))
                // 应用增强检索服务（云知识库服务）
                //.advisors(loveAppRagCloudAdvisor)
                //应用 Rag检索增强服务基于Postgresql向量存储
               // .advisors(new QuestionAnswerAdvisor(pgVectorVectorStore))
//                .advisors(
//                        LoveAppRagCustomAdvisorFactory.createLoveAppRagCustomAdvisor(
//                                loveAppVectorStore,"单身"
//                        )
//                )
                .call()  // 添加问答顾问，使用向量存储
                .chatResponse();  // 执行调用
        String content = chatResponse.getResult().getOutput().getText();
        // 提取响应中的文本内容
        log.info("content: {}", content);
        // 记录日志
        return content;
    }


/**
 * 使用工具与用户进行聊天的方法
 * 该方法接收用户消息和聊天ID，通过聊天客户端生成响应并返回内容
 *
 * @param message 用户输入的消息内容
 * @param chatId 当前会话的唯一标识符
 * @return 聊天响应的内容文本
 */
    public String doChatWithTools(String message, String chatId) {
        // 构建聊天请求并获取响应
        ChatResponse response = chatClient
                .prompt()  // 创建提示
                .user(message)  // 设置用户消息
                // 配置顾问参数，包括会话ID和检索历史消息的数量
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                // 开启日志，便于观察效果
                .advisors(new MyLoggerAdvisor())
                .tools(allTools)
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }



/**
 * 与MCP进行聊天对话的方法
 * @param message 用户输入的消息内容
 * @param chatId 聊天会话的唯一标识符
 * @return 返回聊天机器人的回复内容
 */
    public String doChatWithMcp(String message, String chatId) {
    // 使用chatClient创建聊天请求，并配置相关参数
        ChatResponse response = chatClient
                .prompt()  // 创建提示
                .user(message)  // 设置用户消息
            // 配置顾问参数，包括会话ID和检索历史记录的数量
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                // 开启日志，便于观察效果
                .advisors(new MyLoggerAdvisor())
                .tools(toolCallbackProvider)
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }

}
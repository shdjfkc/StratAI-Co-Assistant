package com.sca.stratai.chatmemory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.objenesis.strategy.StdInstantiatorStrategy;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 基于文件持久化的对话记忆
 */
public class FileBasedChatMemory implements ChatMemory {

    private final String BASE_DIR;
    private static final Kryo kryo = new Kryo();

    /**
     * 静态初始化代码块
     * 用于配置Kryo序列化框架的相关设置
     */
    static {
        // 设置Kryo不需要强制注册类，这样可以序列化任意类
        kryo.setRegistrationRequired(false);
        // 设置实例化策略为标准实例化策略
        // 这允许Kryo创建没有无参构造函数的对象实例
        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
    }
    /**
     * 构造函数：初始化基于文件的聊天记忆存储系统
     * @param dir 指定聊天记录文件的保存目录路径
     */
    // 构造对象时，指定文件保存目录
    public FileBasedChatMemory(String dir) {
        // 将传入的目录路径保存为类的成员变量
        this.BASE_DIR = dir;
        // 创建File对象，用于操作文件系统
        File baseDir = new File(dir);
        // 检查目录是否存在，如果不存在则创建
        if (!baseDir.exists()) {
        // 创建目录（包括所有不存在的父目录）
            baseDir.mkdirs();
        }
    }


    /**
     * 添加消息到指定对话的方法
     * 该方法会获取或创建一个对话，然后将新消息添加到该对话中，并保存更新后的对话
     *
     * @param conversationId 对话的唯一标识符
     * @param messages 要添加的消息列表
     */
    @Override
    public void add(String conversationId, List<Message> messages) {
        // 获取或创建指定ID的对话，如果对话不存在则创建一个新的对话
        List<Message> conversationMessages = getOrCreateConversation(conversationId);
        // 将新消息添加到对话中
        conversationMessages.addAll(messages);
        // 保存更新后的对话内容
        saveConversation(conversationId, conversationMessages);
    }

    /**
     * 根据会话ID获取最后N条消息
     *
     * @param conversationId 会话ID，用于标识特定的对话
     * @param lastN 需要获取的消息数量，表示从最后开始计算的消息条数
     * @return 返回包含最后N条消息的列表，如果消息总数少于N，则返回所有消息
     */
    @Override
    public List<Message> get(String conversationId, int lastN) {
        // 获取或创建指定会话ID的消息列表
        List<Message> allMessages = getOrCreateConversation(conversationId);
        // 使用Stream API处理消息列表：
        // 1. skip()跳过前面的消息，保留最后N条
        // 2. Math.max(0, ...)确保不会出现负数
        // 3. toList()将处理后的流转换为列表
        return allMessages.stream()
                .skip(Math.max(0, allMessages.size() - lastN))
                .toList();
    }

    @Override
    public void clear(String conversationId) {
        File file = getConversationFile(conversationId);
        if (file.exists()) {
            file.delete();
        }
    }
    /**
     * 根据对话ID获取或创建一个消息列表
     * 如果对话文件已存在，则从文件中读取消息列表；如果不存在，则创建一个新的空列表
     *
     * @param conversationId 对话的唯一标识符
     * @return 包含消息的列表，如果文件存在则返回文件中的消息列表，否则返回空列表
     */

    private List<Message> getOrCreateConversation(String conversationId) {
        // 获取对话对应的文件对象
        File file = getConversationFile(conversationId);
        // 创建一个新的消息列表
        List<Message> messages = new ArrayList<>();
        // 检查文件是否存在
        if (file.exists()) {
            try (Input input = new Input(new FileInputStream(file))) {
                // 使用Kryo从文件中读取对象并转换为ArrayList
                messages = kryo.readObject(input, ArrayList.class);
            } catch (IOException e) {
                // 捕获并打印IO异常
                e.printStackTrace();
            }
        }
        // 返回消息列表（可能是从文件读取的，也可能是新创建的空列表）
        return messages;
    }

    private void saveConversation(String conversationId, List<Message> messages) {
        File file = getConversationFile(conversationId);
        try (Output output = new Output(new FileOutputStream(file))) {
            kryo.writeObject(output, messages);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 根据会话ID获取对应的会话文件
     * @param conversationId 会话的唯一标识符
     * @return 返回一个File对象，表示存储会话数据的文件
     */
    private File getConversationFile(String conversationId) {
        // 创建并返回一个File对象，文件路径为BASE_DIR目录下的conversationId.kryo文件
        return new File(BASE_DIR, conversationId + ".kryo");
    }
}

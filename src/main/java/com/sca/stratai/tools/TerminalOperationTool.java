package com.sca.stratai.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 终端操作工具
 * 该类提供了在终端执行命令的功能
 */
public class TerminalOperationTool {

    /**
     * 在终端执行命令的方法
     * @param command 需要在终端执行的命令字符串
     * @return 返回命令执行后的输出结果，包括标准输出和错误信息
     */
    @Tool(description = "Execute a command in the terminal")
    public String executeTerminalCommand(@ToolParam(description = "Command to execute in the terminal") String command) {
        // 用于存储命令输出的字符串构建器
        StringBuilder output = new StringBuilder();
        try {
            // 使用ProcessBuilder构建进程，执行命令
            ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", command);
            // 注释掉的代码是使用Runtime执行命令的方式
//            Process process = Runtime.getRuntime().exec(command);
            // 启动进程
            Process process = builder.start();
            // 使用try-with-resources自动关闭BufferedReader
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                // 逐行读取命令输出
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }
            // 等待命令执行完成并获取退出码
            int exitCode = process.waitFor();
            // 如果退出码不为0，表示命令执行失败
            if (exitCode != 0) {
                output.append("Command execution failed with exit code: ").append(exitCode);
            }
        } catch (IOException | InterruptedException e) {
            // 捕获并处理可能的IO异常或中断异常
            output.append("Error executing command: ").append(e.getMessage());
        }
        // 返回命令执行结果
        return output.toString();
    }
}

package com.sca.stratai.tools;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import com.sca.stratai.constant.FileConstant;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.File;

/**
 * 资源下载工具类
 */
public class ResourceDownloadTool {

/**
 * 从给定URL下载资源的工具方法
 * @param url 要下载的资源URL地址
 * @param fileName 下载后保存的文件名
 * @return 返回下载结果信息，成功时返回保存路径，失败时返回错误信息
 */
    @Tool(description = "Download a resource from a given URL")
    // 构建完整的文件保存路径
    public String downloadResource(@ToolParam(description = "URL of the resource to download") String url, @ToolParam(description = "Name of the file to save the downloaded resource") String fileName) {
    // 构建文件保存目录路径，使用FileConstant中定义的基础目录
        String fileDir = FileConstant.FILE_SAVE_DIR + "/download";
        String filePath = fileDir + "/" + fileName;
        try {
            // 创建目录
            FileUtil.mkdir(fileDir);
            // 使用 Hutool 的 downloadFile 方法下载资源
            HttpUtil.downloadFile(url, new File(filePath));
            return "Resource downloaded successfully to: " + filePath;
        } catch (Exception e) {
            return "Error downloading resource: " + e.getMessage();
        }
    }
}

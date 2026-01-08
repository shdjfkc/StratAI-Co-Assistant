package com.sca.imagesearchmcpserver.tools;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ImageSearchTool {

    // 替换为你的 Pexels API 密钥（需从官网申请）
    private static final String API_KEY = "xyEB7gC8AI97kefdhTwxezDDZ3Wq89XvhZFnGc5pkvC6DO85S8XCP2bM";

    // Pexels 常规搜索接口（请以文档为准）
    private static final String API_URL = "https://api.pexels.com/v1/search";

/**
 * 从网络搜索图片的工具方法
 * @description 该方法用于根据关键词从网络搜索图片，并返回结果字符串
 * @param query 搜索关键词，用于指定要搜索的图片内容
 * @return 返回搜索到的图片URL列表，以逗号分隔的字符串形式；如果发生错误则返回错误信息
 */
    @Tool(description = "search image from web")
    public String searchImage(@ToolParam(description = "Search query keyword") String query) {
        try {
        // 调用搜索方法获取中等尺寸的图片URL列表，并用逗号连接成字符串返回
            return String.join(",", searchMediumImages(query));
        } catch (Exception e) {
        // 捕获异常并返回错误信息
            return "Error search image: " + e.getMessage();
        }
    }

    /**
     * 搜索中等尺寸的图片列表
     *
     * @param query
     * @return
     */
    public List<String> searchMediumImages(String query) {
        // 设置请求头（包含API密钥）
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", API_KEY);

        // 设置请求参数（仅包含query，可根据文档补充page、per_page等参数）
        Map<String, Object> params = new HashMap<>();
        params.put("query", query);

        // 发送 GET 请求
        String response = HttpUtil.createGet(API_URL)
                .addHeaders(headers)
                .form(params)
                .execute()
                .body();

        // 解析响应JSON（假设响应结构包含"photos"数组，每个元素包含"medium"字段）
        return JSONUtil.parseObj(response)
                .getJSONArray("photos")
                .stream()
                .map(photoObj -> (JSONObject) photoObj)
                .map(photoObj -> photoObj.getJSONObject("src"))
                .map(photo -> photo.getStr("medium"))
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toList());
    }
}

package com.sca.stratai.tools;

import cn.hutool.core.io.FileUtil;

import com.sca.stratai.constant.FileConstant;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;

import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.IOException;

/**
 * PDF生成工具类
 */
public class PDFGenerationTool {



    // 注解：表示这是一个工具方法，用于生成PDF文件
    @Tool(description = "Generate a PDF file with given content")
    public String generatePDF(
            @ToolParam(description = "Name of the file to save the generated PDF") String fileName,  // 参数：要保存生成的PDF的文件名
            @ToolParam(description = "Content to be included in the PDF") String content) {  // 参数：要包含在PDF中的内容
        String fileDir = FileConstant.FILE_SAVE_DIR + "/pdf";  // 定义PDF文件保存的目录路径
        String filePath = fileDir + "/" + fileName;  // 定义完整的PDF文件路径
        try {
            // 创建目录  // 创建PDF文件所需的目录
            FileUtil.mkdir(fileDir);  // 调用工具类方法创建目录
            // 创建 PdfWriter 和 PdfDocument 对象  // 创建PDF文档所需的Writer和Document对象  // 创建文档对象，用于添加内容
            try (PdfWriter writer = new PdfWriter(filePath);  // 创建PDF写入器，使用try-with-resources确保资源自动关闭  // 注释说明自定义字体的方法（当前被注释）
                 PdfDocument pdf = new PdfDocument(writer);  // 创建PDF文档对象
                 // 获取字体文件的绝对路径
                 Document document = new Document(pdf)) {
                // 自定义字体（需要人工下载字体文件到特定目录）
//                String fontPath = Paths.get("src/main/resources/static/fonts/simsun.ttf")  // 创建字体对象，并嵌入PDF
//                        .toAbsolutePath().toString();  // 使用系统内置的中文字体
//                PdfFont font = PdfFontFactory.createFont(fontPath,  // 创建中文字体对象
//                        PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);  // 设置文档字体
                // 使用内置中文字体  // 创建包含内容的段落对象
                PdfFont font = PdfFontFactory.createFont("STSongStd-Light", "UniGB-UCS2-H");  // 使用传入的内容创建段落
                document.setFont(font);  // 将段落添加到文档中
                // 创建段落  // 将段落添加到文档
                Paragraph paragraph = new Paragraph(content);
                // 添加段落并关闭文档  // 返回成功消息，包含文件路径
                document.add(paragraph);  // 捕获IO异常
            }
            // 返回错误消息，包含异常信息
            return "PDF generated successfully to: " + filePath;
        } catch (IOException e) {
            return "Error generating PDF: " + e.getMessage();
        }
    }
}

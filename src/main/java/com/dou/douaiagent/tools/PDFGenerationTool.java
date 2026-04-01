package com.dou.douaiagent.tools;

import cn.hutool.core.io.FileUtil;
import com.dou.douaiagent.constant.FileConstant;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.IOException;

public class PDFGenerationTool {
    //通常情况下，spring ai框架中调用工具后，会将获取到的返回结果再传给大模型进行处理，最后才由大模型返回结果
    //returnDirect 设置为 true后，调用工具生成pdf完成后，不用在请求大模型，直接返回
    @Tool(description = "Generate a PDF file with given content", returnDirect = true)
    public String generatePDF(
            @ToolParam(description = "Name of the file to save the generated PDF") String fileName,
            @ToolParam(description = "Content to be included in the PDF") String content) {
        String fileDir = FileConstant.FILE_SAVE_DIR + "/pdf";
        String filePath = fileDir + "/" + fileName;
        try {
            // 创建目录
            FileUtil.mkdir(fileDir);
            // 创建 PdfWriter 和 PdfDocument 对象
            try (PdfWriter writer = new PdfWriter(filePath);
                 PdfDocument pdf = new PdfDocument(writer);
                 Document document = new Document(pdf)) {
                // 自定义字体（需要人工下载字体文件到特定目录）
//                String fontPath = Paths.get("src/main/resources/static/fonts/simsun.ttf")
//                        .toAbsolutePath().toString();
//                PdfFont font = PdfFontFactory.createFont(fontPath,
//                        PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
                // 使用内置中文字体
                PdfFont font = PdfFontFactory.createFont("STSongStd-Light", "UniGB-UCS2-H");
                document.setFont(font);
                // 创建段落

                content = filterNonBmp(content);
                Paragraph paragraph = new Paragraph(content);
                // 添加段落并关闭文档
                document.add(paragraph);
            }
            return "PDF generated successfully to: " + filePath;
        } catch (IOException e) {
            return "Error generating PDF: " + e.getMessage();
        }
    }

    /**
     * 过滤掉非 BMP 字符（编码 > U+FFFF 的字符）
     */
    public String filterNonBmp(String text) {
        if (text == null) return "";
        // 保留：码点 <= 0xFFFF 的字符
        return text.codePoints()
                .filter(c -> c <= 0xFFFF)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}

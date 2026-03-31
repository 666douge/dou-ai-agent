package com.dou.douaiagent.tools;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PDFGenerationToolTest {

    @Test
    void generatePDF() {
        PDFGenerationTool tool = new PDFGenerationTool();
        String content = "我是程序员豆哥，hello哇！";
        tool.generatePDF("豆哥的自我介绍.pdf", content);

    }
}
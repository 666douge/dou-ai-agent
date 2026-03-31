package com.dou.douaiagent.tools;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FileOperationToolTest {

    @Test
    void readFile() {
        FileOperationTool filetool = new FileOperationTool();
        String fileName = "豆哥的自述.txt";
        filetool.readFile(fileName);

    }

    @Test
    void writeFile() {
        String content = "我是程序员豆哥，请多多关照, www.baidu.com";
        String fileName = "豆哥的自述.txt";
        FileOperationTool filetool = new FileOperationTool();
        filetool.writeFile(fileName, content);
    }
}
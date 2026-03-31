package com.dou.douaiagent.tools;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import com.dou.douaiagent.constant.FileConstant;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * 文件操作工具类
 */
public class FileOperationTool {
    /***
     * 文件路径
     */
    private final String FILE_PATH = FileConstant.FILE_SAVE_DIR + "/file";
    /**
     * 读取文件的方法
     * @param fileName
     * @return
     */
    @Tool(description = "Read content from a file")
    public String readFile(@ToolParam(description = "Name of the file to read") String fileName){
        String fullFileName = FILE_PATH + "/" + fileName;
        try {
            return FileUtil.readUtf8String(fullFileName);
        } catch (Exception e) {
            return "Error reading file: " + e;
        }
    }

    /**
     * 写文件，保存文件的方法
     * @param fileName
     * @param content
     * @return
     */
    @Tool(description = "Write content to a file")
    public String writeFile(@ToolParam(description = "Name of the file to write") String fileName,
                            @ToolParam(description = "Content saved to the file") String content){
        String fullFileName = FILE_PATH + "/" + fileName;
        try {
            //创建目录
            FileUtil.mkdir(FILE_PATH);
            //将文件内容写入到对应的文件中
            FileUtil.writeUtf8String(content, fullFileName);
            return "File written successfully to: " + fullFileName;
        } catch (IORuntimeException e) {
            return "Error writing to file: " + e;
        }
    }
}

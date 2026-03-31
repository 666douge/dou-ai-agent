package com.dou.douaiagent.tools;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResourceDownloadToolTest {

    @Test
    void downloadResource() {
        String url = "https://pic.code-nav.cn/course_picture/1608440217629360130/Ne4P6mqB3CdEZvfQ.webp";
        ResourceDownloadTool tool = new ResourceDownloadTool();
        tool.downloadResource(url, "我的图片.png");

    }
}
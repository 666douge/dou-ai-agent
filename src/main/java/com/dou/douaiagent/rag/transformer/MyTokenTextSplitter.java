package com.dou.douaiagent.rag.transformer;

import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 自定义 基于 Token的 切词器
 * 文本切割
 * 一般不用这个方式，因为按照这种切出来的文档内容可能有问题：从段中落中间切开，出现乱码或不完整
 */
@Component
public class  MyTokenTextSplitter {

    public List<Document> splitDocuments(List<Document> documents) {
        TokenTextSplitter splitter = TokenTextSplitter.builder().build();
        return splitter.apply(documents);
    }

    public List<Document> splitCustomized(List<Document> documents) {
        TokenTextSplitter splitter = TokenTextSplitter.builder()
                .withChunkSize(200)
                .withMinChunkSizeChars(100)
                .withMinChunkLengthToEmbed(10)
                .withMaxNumChunks(5000)
                .withKeepSeparator(true)
                .build();
        return splitter.apply(documents);
    }
}
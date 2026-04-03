package com.dou.douimagesearchmcpserver.tools;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 从网页搜索图片的工具
 */
@Component
public class SearchImageTool {

    private static final String PEXELS_API_URL = "https://api.pexels.com/v1/search";
    @Value("${pexels.api-key}")
    private String pexelsApiKey;

    @Tool(description = "Search image from web")
    public String searchImagesfromWeb(@ToolParam(description = "Search query keyword") String query) {
        try {
            HttpResponse response = HttpRequest.get(PEXELS_API_URL)
                    .header("Authorization", pexelsApiKey)
                    .header("Accept", "application/json")
                    .form("query", query)
                    //.form("per_page", 1)
                    .timeout(120000)
                    .execute();

            JSONObject json = new JSONObject(response.body());
            JSONArray photos = json.getJSONArray("photos");
            if (photos == null || photos.isEmpty()) {
                return "Error search image";
            }
            //将返回的多个图片的地址拼接成字符串
            List<String> resultList = new ArrayList<>();
            Iterator<Object> iterator = photos.stream().iterator();
            while(iterator.hasNext()){
                JSONObject jsonObj = (JSONObject)iterator.next();
                JSONObject src = jsonObj.getJSONObject("src");
                String medium = src.getStr("medium");
                if(StrUtil.isNotBlank(medium)){
                    resultList.add(medium);
                }
            }

            /*List<String> resultList = photos.stream().map(photoObj -> (JSONObject)photoObj)
                    .map(photoObj -> photoObj.getJSONObject("src"))
                    .map(photo -> photo.getStr("medium"))
                    .filter(StrUtil::isNotBlank)
                    .collect(Collectors.toList());*/

            return String.join(",", resultList);
        } catch (Exception e) {
            return "Error search image: " + e;
        }
    }
}

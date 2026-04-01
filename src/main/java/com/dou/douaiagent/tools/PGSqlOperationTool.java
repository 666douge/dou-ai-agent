package com.dou.douaiagent.tools;

import com.dou.douaiagent.mapper.LoveAppMapper;
import com.dou.douaiagent.model.LoveAppUser;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * 进行数据库操作的方法
 */

public class PGSqlOperationTool {

    private LoveAppMapper loveAppMapper;

    public PGSqlOperationTool(LoveAppMapper loveAppMapper){
        this.loveAppMapper = loveAppMapper;
    }

    @Tool(description = "Save a person's information to the database")
    public String add(@ToolParam(description = "The name of the person") String userName,
                      @ToolParam(description = "The age of the person") int age,
                      @ToolParam(description = "The sex of the person") String sex,
                      @ToolParam(description = "The information of the person") String information){

        LoveAppUser user = new LoveAppUser();
        user.setUserName(userName);
        user.setAge(age);
        user.setSex(sex);
        user.setInformation(information);
        int result = loveAppMapper.insert(user);

        if(result == 0){
            return "Save the information failed。";
        }
        return "Save the information successfully。";

    }
}

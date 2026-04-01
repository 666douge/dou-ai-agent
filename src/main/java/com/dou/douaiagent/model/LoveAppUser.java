package com.dou.douaiagent.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("loveapp_user")
@Data
public class LoveAppUser {

    @TableId
    private Long id;

    private String userName;

    private String sex;

    private int age;


    private String information;

}

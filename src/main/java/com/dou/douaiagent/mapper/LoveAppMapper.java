package com.dou.douaiagent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dou.douaiagent.model.LoveAppUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * loverApp的Mapper文件
 * 不用单独加@Mapper注解
 */
public interface LoveAppMapper extends BaseMapper<LoveAppUser> {
}

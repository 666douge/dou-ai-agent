package com.dou.douaiagent.agent;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * ReAct(Reasonsing and Acting) 模式代理抽象类
 * 实现了 思考-行动 的循环模式
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Slf4j
public abstract class ReActAgent extends BaseAgent{

    /**
     * 处理当前状态并决定下一步行动
     *
     * @return 是否需要执行行动， true 表示需要执行；  false 不需要执行
     */
    public abstract boolean think();

    /**
     * 执行下一步行动
     * @return
     */
    public abstract String act();

    /**
     * 执行单个步骤 思考-执行
     * @return
     */
    @Override
    public String step(){
        try {
            boolean shouldact = think();
            if(!shouldact){
                return "思考完成 - 无需行动";
            }
            return act();
        } catch (Exception e) {
           e.printStackTrace();
           return "步骤执行失败：" + e.getMessage();
        }

    }



}

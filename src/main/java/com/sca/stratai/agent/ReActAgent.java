package com.sca.stratai.agent;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * ReAct (Reasoning and Acting) 模式的代理抽象类  
 * 实现了思考-行动的循环模式  
 * 这个类继承自BaseAgent，并提供了基本的思考-行动框架
 */
@EqualsAndHashCode(callSuper = true)    // 使用Lombok注解，包含父类的equals和hashCode方法
@Data    // 使用Lombok注解，自动生成getter、setter、toString等方法
public abstract class ReActAgent extends BaseAgent {  
  
    /**  
     * 处理当前状态并决定下一步行动  
     *   这是一个抽象方法，需要子类实现具体的思考逻辑
     *
     * @return 是否需要执行行动，true表示需要执行，false表示不需要执行
     */  
    public abstract boolean think();  
  
    /**  
     * 执行决定的行动  
     *   这是一个抽象方法，需要子类实现具体的行动逻辑

     *
     * @return 行动执行结果  ，以字符串形式返回
     */  
    public abstract String act();  
  
    /**  
     * 执行单个步骤：思考和行动  
     *   这是ReAct模式的核心步骤，先思考再决定是否行动

     *
     * @return 步骤执行结果  ，成功返回行动结果或提示信息，失败返回错误信息
     */  
    @Override    // 重写父类的step方法
    public String step() {  
        try {  
            // 调用think方法进行思考，决定是否需要行动
            boolean shouldAct = think();
            // 如果思考后决定不需要行动，返回提示信息
            if (!shouldAct) {
                return "思考完成 - 无需行动";  
            }  
            // 如果需要行动，调用act方法执行行动并返回结果
            return act();
        } catch (Exception e) {  
            // 记录异常日志  
            e.printStackTrace();  
            return "步骤执行失败: " + e.getMessage();  
        }  
    }  
}

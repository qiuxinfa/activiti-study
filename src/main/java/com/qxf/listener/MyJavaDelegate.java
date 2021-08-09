package com.qxf.listener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

/**
 * @ClassName MyJavaDelegate
 * @Description TODO
 * @Author qiuxinfa
 * @Date 2021/7/10 21:30
 **/
@Component
public class MyJavaDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution delegate) {
        System.out.println("执行MyJavaDelegate，流程实例id = "+delegate.getProcessInstanceId()
                +"，当前活动节点 = "+delegate.getCurrentActivityId());
    }
}

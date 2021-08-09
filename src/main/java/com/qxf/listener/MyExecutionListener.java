package com.qxf.listener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Component;

/**
 * @ClassName MyExecutionListener
 * @Description TODO
 * @Author qiuxinfa
 * @Date 2021/7/10 21:09
 **/
@Component
public class MyExecutionListener implements ExecutionListener {
    @Override
    public void notify(DelegateExecution delegate) {
        if (EVENTNAME_START.equals(delegate.getEventName())){
            System.out.println("执行MyExecutionListener，流程启动了：流程实例id = "+delegate.getProcessInstanceId());
        }else if (EVENTNAME_END.equals(delegate.getEventName())){
            System.out.println("执行MyExecutionListener，流程结束了：流程实例id = "+delegate.getProcessInstanceId());
        }else if (EVENTNAME_TAKE.equals(delegate.getEventName())){
            // 执行连线部分
            System.out.println("执行MyExecutionListener，当前执行的节点："+delegate.getCurrentActivityId()
                    +"，流程实例id = "+delegate.getProcessInstanceId());
            System.out.println("isTeacherAudit = "+delegate.getVariable("isTeacherAudit"));
        }
    }
}

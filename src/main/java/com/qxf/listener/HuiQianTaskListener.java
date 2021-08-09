package com.qxf.listener;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.stereotype.Component;

/**
 * @ClassName HuiQianTaskListener
 * @Description 会签的任务监听器
 * @Author qiuxinfa
 * @Date 2021/8/5 19:44
 **/
@Component
public class HuiQianTaskListener implements TaskListener{
    @Override
    public void notify(DelegateTask delegateTask) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        String processInstanceId = delegateTask.getProcessInstanceId();
        String executionId = delegateTask.getExecutionId();
        Integer nrOfCompletedInstances = (Integer) runtimeService.getVariable(executionId,"nrOfCompletedInstances");
        Integer nrOfInstances = (Integer) runtimeService.getVariable(executionId,"nrOfInstances");
        Boolean agree = delegateTask.getVariable("agree", Boolean.class);
        if (agree){
            Object cnt = runtimeService.getVariable(processInstanceId, "agreeCnt");
            Integer agreeCnt = cnt == null ? 0 : (Integer)cnt + 1;
            runtimeService.setVariable(processInstanceId,"agreeCnt",agreeCnt);
            if (agreeCnt*1.0 / nrOfInstances >= 0.5){
                runtimeService.setVariable(processInstanceId,"isApply","true");
            }
        }
        // 如果一个审批人完成了审批进入到该监听时nrOfCompletedInstances的值还没有更新，因此需要+1
        System.out.println("nrOfInstances = "+nrOfInstances+" ，nrOfCompletedInstances = "+(nrOfCompletedInstances+1));
    }
}

package com.qxf.listener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.stereotype.Component;

/**
 * @ClassName MyTaskListener
 * @Description TODO
 * @Author qiuxinfa
 * @Date 2021/7/10 21:22
 **/
@Component
public class MyTaskListener implements TaskListener{
    /**
     String EVENTNAME_CREATE = "create";
     String EVENTNAME_ASSIGNMENT = "assignment";
     String EVENTNAME_COMPLETE = "complete";
     String EVENTNAME_DELETE = "delete";
     String EVENTNAME_ALL_EVENTS = "all";
     */
    @Override
    public void notify(DelegateTask delegate) {
        // 任务的完成complete、删除delete和下一个任务的创建create是连着发生的
        System.out.println("流程实例id = "+delegate.getProcessInstanceId()
                +"，任务id = "+delegate.getId()+"，触发的事件 = "+delegate.getEventName());
    }
}

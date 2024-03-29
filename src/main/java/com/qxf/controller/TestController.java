package com.qxf.controller;

import com.qxf.util.ActivitiUtil;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName TestController
 * @Description 监听器测试
 * @Author qiuxinfa
 * @Date 2021/7/8 22:51
 **/
@RestController
@RequestMapping("/listener")
public class TestController {

    public static final String PROCESS_KEY = "testListener";

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private IdentityService identityService;

    @Autowired
    private TaskService taskService;

    @GetMapping("/start")
    public String start(String userId){
        // 设置流程启动人
        identityService.setAuthenticatedUserId(userId);
        // 通过流程key启动流程
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(PROCESS_KEY);
//        // 挂起流程实例
//        runtimeService.suspendProcessInstanceById(processInstance.getId());
//        // 激活流程实例
//        runtimeService.activateProcessInstanceById(processInstance.getId());
        return "流程启动成功，流程实例id = "+processInstance.getId();
    }

    @GetMapping("/list")
    public List<Map<String, Object>> queryTaskList(String processInstanceId){
        List<Task> list = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        return ActivitiUtil.parseResult(list);
    }

    @GetMapping("/claim")
    public String claim(String taskId,String userId){
        // 任务签收
        taskService.claim(taskId,userId);
        // setAssignee和claim两个的区别是在认领任务时，claim会检查该任务是否已经被认领，
        // 如果被认领则会抛出ActivitiTaskAlreadyClaimedException 而setAssignee不会进行这样的检查。
//        taskService.setAssignee(taskId,userId);
        // 完成任务，流转到下一个节点
        taskService.complete(taskId);
        return "任务签收成功！";
    }

    @GetMapping("/audit")
    public String teacherAudit(String taskId,String isAudit) {
        Map<String,Object> map = new HashMap<>(1);
        map.put("audit",isAudit);
        taskService.complete(taskId,map);
        return "true".equals(isAudit) ? "审核通过！" : "审核不通过";
    }
}

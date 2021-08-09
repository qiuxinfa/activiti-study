package com.qxf.controller;

import com.qxf.cmd.JumpCmd;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName TestJumpController
 * @Description 流程跳转测试（不需要连线，实现跳转，变相实现退回）
 * @Author qiuxinfa
 * @Date 2021/8/7 10:03
 **/
@RestController
public class TestJumpController {
    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ManagementService managementService;

    @GetMapping("/jump")
    public void jump(){
        // 启动流程
        String key = "test_jump";
        Map<String,Object> variables = new HashMap<>(1);
        variables.put("user1","张三");
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(key, variables);

        // 学生提交请假
        Task task = taskService.createTaskQuery().taskAssignee("张三").singleResult();
        variables.clear();
        variables.put("user2","李四");
        taskService.complete(task.getId(),variables);

        // 班长审批
        task = taskService.createTaskQuery().taskAssignee("李四").singleResult();
        variables.clear();
        variables.put("user3","王五");
        taskService.complete(task.getId(),variables);

        // 辅导员审批student_apply、class_audit
        boolean isJump = true;
        task = taskService.createTaskQuery().taskAssignee("王五").singleResult();
        if (isJump){
            // 跳转到学生提交请假
            managementService.executeCommand(new JumpCmd(task.getId(), "student_apply"));
            task = taskService.createTaskQuery().taskAssignee("张三").singleResult();
            taskService.complete(task.getId(),variables);
            // 班长审批
            task = taskService.createTaskQuery().taskAssignee("李四").singleResult();
            taskService.complete(task.getId(),variables);
            // 辅导员审批
            task = taskService.createTaskQuery().taskAssignee("王五").singleResult();
            taskService.complete(task.getId());
        }else {
            taskService.complete(task.getId());
        }

    }
}

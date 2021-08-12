package com.qxf.controller;

import com.qxf.cmd.AddMultiInstanceCmd;
import org.activiti.engine.*;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName TestHuiqian
 * @Description 测试多实例会签
 * @Author qiuxinfa
 * @Date 2021/8/2 23:52
 **/
@RestController
public class TestHuiqian {
    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private RuntimeService runtimeService;

    @GetMapping("/testHuiqian")
    public void testHuiqian(){
        // 流程id
        String key = "test_huiqian_2";  // 并行多实例
        boolean testAddInstance = false;  // 测试添加会签人员，开关
        // 申请人列表
        List<String> applyUserList = new ArrayList<>(3);
        applyUserList.add("张三");
        applyUserList.add("李四");
        applyUserList.add("王五");
        Map<String,Object> variables = new HashMap<>(1);
        variables.put("applyUserList",applyUserList);
        // 申请人同意的数量
        variables.put("agreeCnt",0);
        variables.put("isApply","false");
        variables.put("testAddUserTask",true);
        // 启动流程
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(key, variables);
        // 查询任务
        List<Task> tasks = taskService.createTaskQuery().taskAssignee("张三").list();
        List<Task> tasks1 = taskService.createTaskQuery().taskAssignee("李四").list();
        List<Task> tasks2 = taskService.createTaskQuery().taskAssignee("王五").list();

        Map<String,Object> applyMap = new HashMap<>(2);
        applyMap.put("agree",true);
        applyMap.put("leader","qxf");
        for (Task task : tasks){
            taskService.complete(task.getId(),applyMap);
        }

        // 测试动态添加会签人员
        if (testAddInstance){
            String taskId = tasks1.get(0).getId();
            String addUser = "jack";
            this.AddInstance(taskId,addUser);
            List<Task> taskList = taskService.createTaskQuery().taskAssignee(addUser).list();
            applyMap.put("agree",true);
            for (Task task : taskList) {
                taskService.complete(task.getId(),applyMap);
            }
            // 更新任务：因为动态添加的人员完成任务后，如果满足完成条件，其他任务会被删除
            tasks1 = taskService.createTaskQuery().taskAssignee("李四").list();
            tasks2 = taskService.createTaskQuery().taskAssignee("王五").list();
        }

        applyMap.put("agree",false);
        for (Task task : tasks1){
            taskService.complete(task.getId(),applyMap);
        }

        applyMap.put("agree",true);
        for (Task task : tasks2){
            // 后面的变量会覆盖前面的同名变量
            taskService.complete(task.getId(),applyMap);
        }
        applyMap.clear();

        List<Task> leaderTasks = taskService.createTaskQuery().taskAssignee("qxf").list();
        applyMap.put("leaderAudit","true");
        if (!CollectionUtils.isEmpty(leaderTasks)){
            for (Task task : leaderTasks) {
                taskService.complete(task.getId(),applyMap);
            }
        }
    }

    // 添加会签人员
    private void AddInstance(String taskId,String addUserTaskAssign){
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
        ManagementService managementService = defaultProcessEngine.getManagementService();
        // 执行增加实例命令
        managementService.executeCommand(new AddMultiInstanceCmd(taskId, addUserTaskAssign));
    }
}

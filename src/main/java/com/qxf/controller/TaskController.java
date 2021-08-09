package com.qxf.controller;

import com.qxf.config.MyProcessDiagramGenerator;
import com.qxf.util.ActivitiUtil;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.task.Task;
import org.activiti.image.ProcessDiagramGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName TaskController
 * @Description TODO
 * @Author qiuxinfa
 * @Date 2021/7/9 23:51
 **/
@RestController
@RequestMapping("/task")
public class TaskController {
    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private ProcessEngine processEngine;

    /**
     *获取待签收列表
     */
    @GetMapping("/claimList")
    public Object claimList(){
        // 当前用户id
        String userId = "qxf";
        // 根据用户id查询得到角色列表
        List<String> roles = new ArrayList<>(2);
        roles.add("admin");
        roles.add("user");
        // 岗位待签收
        List<Task> list = taskService.createTaskQuery().taskUnassigned().taskCandidateGroupIn(roles).list();
        // 个人待签收
        List<Task> personList = taskService.createTaskQuery().taskUnassigned().taskCandidateUser(userId).list();
        // 将两个和起来就待签收，也可以根据业务分开查询
        if (CollectionUtils.isEmpty(list)){
            if (CollectionUtils.isEmpty(personList)){
                return null;
            }
            return ActivitiUtil.parseResult(personList);
        }
        list.addAll(personList);
        return ActivitiUtil.parseResult(list);
    }

    /**
     *获取待办列表
     */
    @GetMapping("/todoList")
    public Object todoList(){
        // 当前用户id
        String userId = "qxf";
        // 个人待办
        List<Task> personList = taskService.createTaskQuery().taskCandidateUser(userId).list();
        if (CollectionUtils.isEmpty(personList)){
            return null;
        }
        return ActivitiUtil.parseResult(personList);
    }

    /**
     *签收任务
     */
    @GetMapping("/claimTask")
    public String claimTask(String taskId){
        String userId = "qxf";
        taskService.claim(taskId,userId);
        return "id = "+taskId+"的任务已签收";
    }

    /**
     *完成任务，流程流转到下一个节点
     */
    @GetMapping("/completeTask")
    public String completeTask(String taskId){
        taskService.complete(taskId);
        return "id = "+taskId+"的任务已完成";
    }

    /**
     * 通过流程实例id 获取流程图像，已执行节点和流程线高亮显示
     */
    @GetMapping("/viewProcessImage")
    public void viewProcessImage(String processInstanceId, HttpServletResponse response) {
        System.out.println("[开始]-获取流程图图像");
        try {
            //  获取历史流程实例
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(processInstanceId).singleResult();

            if (historicProcessInstance == null) {
                throw new Exception("获取流程实例ID[" + processInstanceId + "]对应的历史流程实例失败！");
            }
            // 获取流程定义
            ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
                    .getDeployedProcessDefinition(historicProcessInstance.getProcessDefinitionId());

            // 获取流程历史中已执行节点，并按照节点在流程中执行先后顺序排序
            List<HistoricActivityInstance> historicActivityInstanceList = historyService.createHistoricActivityInstanceQuery()
                    .processInstanceId(processInstanceId).orderByHistoricActivityInstanceId().asc().list();

            // 已执行的节点ID集合
            List<String> executedActivityIdList = this.getActiveIds(historicActivityInstanceList);
//            List<String> executedActivityIdList = historicActivityInstanceList.stream()
//                    .map(HistoricActivityInstance::getActivityId).collect(Collectors.toList());
            BpmnModel bpmnModel = repositoryService.getBpmnModel(historicProcessInstance.getProcessDefinitionId());

            // 已执行的线集合
            // 获取流程走过的线 (getHighLightedFlows是下面的方法)
            List<String> flowIds = getHighLightedFlows(bpmnModel,historicActivityInstanceList);

            // 获取流程图图像字符流
            processEngine.getProcessEngineConfiguration().setProcessDiagramGenerator(new MyProcessDiagramGenerator());
            ProcessDiagramGenerator pec = processEngine.getProcessEngineConfiguration().getProcessDiagramGenerator();
            //配置字体
            InputStream imageStream = pec.generateDiagram(bpmnModel, "png", executedActivityIdList, flowIds, "宋体", "微软雅黑", "黑体", null, 2.0);

            response.setContentType("image/png");
            OutputStream os = response.getOutputStream();
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = imageStream.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            imageStream.close();
            System.out.println("[完成]-获取流程图图像");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("【异常】-获取流程图失败！" + e.getMessage());
            System.out.println("获取流程图失败！" + e.getMessage());
        }
    }

    // 获取已执行的节点ID集合
    private List<String> getActiveIds( List<HistoricActivityInstance> historicActivityInstanceList){
        // 已执行的节点ID集合
        List<String> executedActivityIdList = new ArrayList<>();
        int index = 1;
        System.out.println("获取已经执行的节点ID");
        for (HistoricActivityInstance activityInstance : historicActivityInstanceList) {
            executedActivityIdList.add(activityInstance.getActivityId());
            System.out.println("第[" + index + "]个已执行节点=" + activityInstance.getActivityId() + " : " + activityInstance.getActivityName());
            index++;
        }
        return executedActivityIdList;
    }

    public List<String> getHighLightedFlows(BpmnModel bpmnModel,List<HistoricActivityInstance> historicActivityInstances) {
        // 时间格式化
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 用以保存高亮的线flowId
        List<String> highFlows = new ArrayList<>();

        for (int i = 0; i < historicActivityInstances.size() - 1; i++) {
            // 对历史流程节点进行遍历
            // 得到节点定义的详细信息
            FlowNode activityImpl = (FlowNode) bpmnModel.getMainProcess().getFlowElement(historicActivityInstances.get(i).getActivityId());

            // 用以保存后续开始时间相同的节点
            List<FlowNode> sameStartTimeNodes = new ArrayList<>();
            FlowNode sameActivityImpl1 = null;

            // 第一个节点
            HistoricActivityInstance activityImpl_ = historicActivityInstances.get(i);
            HistoricActivityInstance activityImp2_;

            for (int k = i + 1; k <= historicActivityInstances.size() - 1; k++) {
                // 后续第1个节点
                activityImp2_ = historicActivityInstances.get(k);

                //都是userTask，且主节点与后续节点的开始时间相同，说明不是真实的后继节点
                if (activityImpl_.getActivityType().equals("userTask") && activityImp2_.getActivityType().equals("userTask") &&
                        df.format(activityImpl_.getStartTime()).equals(df.format(activityImp2_.getStartTime())))
                {

                } else {
                    //找到紧跟在后面的一个节点
                    sameActivityImpl1 = (FlowNode) bpmnModel.getMainProcess().getFlowElement(historicActivityInstances.get(k).getActivityId());
                    break;
                }
            }
            // 将后面第一个节点放在时间相同节点的集合里
            sameStartTimeNodes.add(sameActivityImpl1);
            for (int j = i + 1; j < historicActivityInstances.size() - 1; j++) {
                // 后续第一个节点
                HistoricActivityInstance activityImpl1 = historicActivityInstances.get(j);
                // 后续第二个节点
                HistoricActivityInstance activityImpl2 = historicActivityInstances.get(j + 1);
                // 如果第一个节点和第二个节点开始时间相同保存
                if (df.format(activityImpl1.getStartTime()).equals(df.format(activityImpl2.getStartTime()))) {
                    FlowNode sameActivityImpl2 = (FlowNode) bpmnModel.getMainProcess().getFlowElement(activityImpl2.getActivityId());
                    sameStartTimeNodes.add(sameActivityImpl2);
                } else {// 有不相同跳出循环
                    break;
                }
            }
            // 取出节点的所有出去的线
            List<SequenceFlow> pvmTransitions = activityImpl.getOutgoingFlows();
            // 对所有的线进行遍历
            for (SequenceFlow pvmTransition : pvmTransitions) {
                // 如果取出的线的目标节点存在时间相同的节点里，保存该线的id，进行高亮显示
                FlowNode pvmActivityImpl = (FlowNode) bpmnModel.getMainProcess().getFlowElement(pvmTransition.getTargetRef());
                if (sameStartTimeNodes.contains(pvmActivityImpl)) {
                    highFlows.add(pvmTransition.getId());
                }
            }

        }
        return highFlows;

    }

}

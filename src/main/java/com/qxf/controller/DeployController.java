package com.qxf.controller;

import com.qxf.dto.ActDeploymentDTO;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.image.ProcessDiagramGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName
 * @Description 流程部署相关操作
 * @Author qiuxinfa
 * @Date 2021/7/7 20:23
 **/
@RestController
@RequestMapping("/deploy")
public class DeployController {
    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private ProcessEngine processEngine;

    // 部署流程（插入数据ACT_RE_DEPLOYMENT、ACT_GE_BYTEARRAY）
    @PostMapping
    public String deployProcess(MultipartFile file){
        if (file == null){
            return "流程部署文件不能为空";
        }
        String deploymentId = null;
        try {
            Deployment deploy = repositoryService.createDeployment()
                    .addInputStream(file.getOriginalFilename(), file.getInputStream()).deploy();
            deploymentId = deploy.getId();
        } catch (IOException e) {
            System.out.println("流程部署失败");
            e.printStackTrace();
        }
        return deploymentId;
    }

    // 获取已部署的流程（ACT_RE_PROCDEF）
    @GetMapping("/list")
    public List<ActDeploymentDTO> list(){
        // ProcessDefinition属性里面有一些属性会导致循环引用
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().list();
        if (CollectionUtils.isEmpty(list)){
            return null;
        }
        List<ActDeploymentDTO> results = new ArrayList<>(list.size());
        for (ProcessDefinition definition : list) {
//            List<Deployment> deployments = repositoryService.createDeploymentQuery().list();
            Deployment deployment = repositoryService.createDeploymentQuery()
                    .deploymentId(definition.getDeploymentId()).singleResult();
            ActDeploymentDTO dto = new ActDeploymentDTO();
            dto.setKey(definition.getKey());
            dto.setDefinitionId(definition.getId());
            dto.setDeploymentId(deployment.getId());
            dto.setDeploymentTime(deployment.getDeploymentTime());
            dto.setDeploymentName(deployment.getName());
            dto.setSuspended(definition.isSuspended());
            dto.setVersion(definition.getVersion());
            dto.setResourceName(definition.getResourceName());
            results.add(dto);
        }
        return results;
    }

    @GetMapping("/viewProcessImg")
    public void viewProcessImg(String processDefinitionId, HttpServletResponse response){
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        ProcessDiagramGenerator diagramGenerator = processEngine.getProcessEngineConfiguration().getProcessDiagramGenerator();
        try {
            // 中文显示的是口口口，设置字体就好了
            InputStream imageStream = diagramGenerator
                    .generateDiagram(bpmnModel, "png", new ArrayList<>(), new ArrayList<>(),
                            "宋体", "微软雅黑", "黑体", null, 2.0);
            ServletOutputStream outputStream = response.getOutputStream();
            response.setContentType("image/png");
            OutputStream os = response.getOutputStream();
            int bytesRead;
            byte[] buffer = new byte[8192];
            while ((bytesRead = imageStream.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            outputStream.close();
            imageStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 流程的激活(SUSPENSION_STATE_设置为1，版本号加1)
    @PostMapping("/activateProcess")
    public String activateProcess(String processDefinitionId){
        if (processDefinitionId == null){
            return "流程定义id不能为空";
        }
        repositoryService.activateProcessDefinitionById(processDefinitionId);
        return "已激活";
    }

    // 流程的挂起(SUSPENSION_STATE_设置为2，版本号加1)
    @PostMapping("/suspendProcess")
    public String suspendProcess(String processDefinitionId){
        if (processDefinitionId == null){
            return "流程定义id不能为空";
        }
        repositoryService.suspendProcessDefinitionById(processDefinitionId);
        return "已挂起";
    }
}

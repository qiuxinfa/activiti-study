package com.qxf.controller;

import com.qxf.dto.ModelDTO;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 流程设计相关接口
 */
@RestController
@RequestMapping("/workflow")
public class ProcessDesignController {
    
    @Autowired
    private RepositoryService repositoryService;
    
    /**
     * 创建模型
     */
    @PostMapping("/models")
    public String createModel(@RequestParam String key, @RequestParam String name, @RequestParam String category){
        Model model = repositoryService.newModel();
        model.setKey(key);
        model.setName(name);
        model.setCategory(category);
        repositoryService.saveModel(model);
        return "创建模型成功，模型id = "+model.getId();
    }
    
    @GetMapping("/models")
    public List<Model> listModel() {
        List<Model> listModel = repositoryService.createModelQuery().list();
        return  listModel;
    }

    /**
     * 保存模型
     */
    @PostMapping(value = "/saveModelXml")
    public String saveModelXml(@RequestBody ModelDTO dto) {
        Model model = repositoryService.getModel(dto.getModelId());
        model.setVersion(model.getVersion()+1);
        repositoryService.saveModel(model);
        repositoryService.addModelEditorSource(dto.getModelId(),dto.getBpmnXml().getBytes());
        repositoryService.addModelEditorSourceExtra(dto.getModelId(),dto.getSvgXml().getBytes());
        return "保存模型成功！";
    }
    
    @DeleteMapping("/models/{modelId}")
    public void deleteModel(@PathVariable String modelId){
        repositoryService.deleteModel(modelId);
    }
    
    /**
     * 根据生成的ID获取模型流程编辑器
     * @param modelId
     * @return
     */
    @GetMapping("/models/{modelId}")
    public String getEditorXml(@PathVariable String modelId) {
        byte[] editorSource = repositoryService.getModelEditorSource(modelId);
        return editorSource == null ? null : new String(editorSource);
    }
    
    @GetMapping(value = "/models/deploy/{modelId}")
    public String deploy(@PathVariable String modelId){
        byte[] source = repositoryService.getModelEditorSource(modelId);
        Deployment deploy = repositoryService.createDeployment().addString("测试部署123", new String(source)).deploy();
        return "部署成功，部署id = "+deploy.getId();
    }
}

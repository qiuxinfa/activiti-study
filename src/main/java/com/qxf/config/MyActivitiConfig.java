package com.qxf.config;

import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * @ClassName MyActivitiConfig
 * @Description TODO
 * @Author qiuxinfa
 * @Date 2021/8/8 17:16
 **/
@Configuration
public class MyActivitiConfig {

    @Bean
    public ProcessEngineConfiguration processEngineConfiguration(@Qualifier("dataSource") DataSource dataSource, PlatformTransactionManager transactionManager) {
        SpringProcessEngineConfiguration processEngineConfiguration = new SpringProcessEngineConfiguration();
        processEngineConfiguration.setDataSource(dataSource);
        processEngineConfiguration.setDatabaseSchemaUpdate(processEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
        processEngineConfiguration.setDatabaseType("mysql");
        processEngineConfiguration.setTransactionManager(transactionManager);
//        processEngineConfiguration.setIdGenerator(new IdGen());
//        processEngineConfiguration.setAsyncExecutorEnabled(true);//使用AsyncExecutor代替默认的JobExecutor;
        processEngineConfiguration.setAsyncExecutorActivate(true);//工作流引擎在启动时就建立启动AsyncExecutor线程
        return processEngineConfiguration;
    }
//
//    @Bean
//    public ProcessEngineFactoryBean processEngine(ProcessEngineConfiguration processEngineConfiguration) {
//        ProcessEngineFactoryBean processEngineFactoryBean = new ProcessEngineFactoryBean();
//        processEngineFactoryBean.setProcessEngineConfiguration((ProcessEngineConfigurationImpl) processEngineConfiguration);
//        return processEngineFactoryBean;
//    }
//
//    @Bean
//    public RepositoryService repositoryService(ProcessEngine processEngine) {
//        return processEngine.getRepositoryService();
//    }
//
//    @Bean
//    public RuntimeService runtimeService(ProcessEngine processEngine){
//        return processEngine.getRuntimeService();
//    }
//
//    @Bean
//    public FormService formService(ProcessEngine processEngine){
//        return processEngine.getFormService();
//    }
//
//    @Bean
//    public IdentityService identityService(ProcessEngine processEngine){
//        return processEngine.getIdentityService();
//    }
//
//    @Bean
//    public TaskService taskService(ProcessEngine processEngine){
//        return processEngine.getTaskService();
//    }
//
//    @Bean
//    public HistoryService historyService(ProcessEngine processEngine){
//        return  processEngine.getHistoryService();
//    }
//
//    @Bean
//    public ManagementService managementService(ProcessEngine processEngine){
//        return processEngine.getManagementService();
//    }

}
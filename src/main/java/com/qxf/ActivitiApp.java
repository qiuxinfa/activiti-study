package com.qxf;

import org.activiti.spring.boot.SecurityAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @ClassName ActivitiApp
 * @Description TODO
 * @Author qiuxinfa
 * @Date 2021/7/6 21:59
 **/
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class ActivitiApp {
    public static void main(String[] args) {
        SpringApplication.run(ActivitiApp.class,args);
    }
}

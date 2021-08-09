package com.qxf.dto;

import lombok.Data;

import java.util.Date;

/**
 * @ClassName ActDeploymentDTO
 * @Description TODO
 * @Author qiuxinfa
 * @Date 2021/7/8 0:04
 **/
@Data
public class ActDeploymentDTO {
    private String deploymentId;
    private String deploymentName;
    private Date deploymentTime;
    private String definitionId;
    private String key;
    private Boolean suspended;
    private String resourceName;
    private Integer version;
}

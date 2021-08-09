package com.qxf.dto;

import lombok.Data;

/**
 * @ClassName ModelDTO
 * @Description TODO
 * @Author qiuxinfa
 * @Date 2021/7/24 18:11
 **/
@Data
public class ModelDTO {
    private String modelId;
    private String bpmnXml;
    private String svgXml;
}

package com.qxf.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName ActivitiUtil
 * @Description list对象转List<Map<String, Object>>
 * @Author qiuxinfa
 * @Date 2021/7/7 21:56
 **/
public class ActivitiUtil {
    public static List<Map<String, Object>> parseResult(List<?> objs) {
        // 用于存放多个对象的集合
        List<Map<String, Object>> pdResult = new ArrayList<>();
        // 遍历方法参数中的集合
        for (Object obj : objs) {
            // 用于封装单个对象 get 方法返回值的 Map 集合
            Map<String, Object> pdMap = new HashMap<>();
            // 通过反射获取该对象的方法对象数组
            Method[] methods = obj.getClass().getDeclaredMethods();
            // 遍历方法对象数组
            for (Method method : methods) {
                // 获取方法名称
                String methodName = method.getName();
                // 判断该方法是否名称不为 null ，并且名称是以 get 开头，满足条件进入 if 中
                if (methodName != null && methodName.startsWith("get")) {
                    // 设置方法的访问权限
                    method.setAccessible(true);
                    try {
                        // 将方法名的 get 前缀去掉
                        String pdKey = methodName.substring(3);
                        // 首字母小写
                        String fieldName = Character.toLowerCase(pdKey.charAt(0))+pdKey.substring(1);
                        // 将 get 方法的名称作为 Map 的 key，将返回值作为 value 进行封装
                        pdMap.put(fieldName, method.invoke(obj, null));
                    } catch (Exception e) {
                        System.out.println("解析方法："+methodName+"出错了");
                        e.printStackTrace();
                    }
                }
            }
            // 将封装好的 Map 集合添加到 List 集合中
            pdResult.add(pdMap);
        }
        return pdResult;
    }
}

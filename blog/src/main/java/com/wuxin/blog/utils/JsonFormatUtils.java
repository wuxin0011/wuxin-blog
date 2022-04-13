package com.wuxin.blog.utils;

import com.alibaba.fastjson.JSON;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: wuxin001
 * @Date: 2022/03/19/1:46
 * @Description:
 */
public class JsonFormatUtils {

    public static <T> List<T> objectToArr(Object o, Class<T> valueType) {
        try {
            return JSON.parseArray(JSON.toJSONString(o),valueType);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


    public static <T> T toObject(Object o, Class<T> valueType) {
        try {
            return JSON.parseObject(JSON.toJSONString(o), valueType);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

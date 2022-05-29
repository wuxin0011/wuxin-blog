package com.wuxin.blog.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wuxin.blog.utils.string.StringUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: wuxin001
 * @Date: 2022/03/19/1:46
 * @Description:
 */
public class JsonFormatUtils {

    private static final Logger logger = LoggerFactory.getLogger(JsonFormatUtils.class);

    public static <T> List<T> objectToArr(Object o, Class<T> valueType) {
        try {
            return JSON.parseArray(formatJson(o), valueType);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("json format error:{}", e.getMessage());
            return new ArrayList<>();
        }
    }


    public static <T> T toObject(Object o, Class<T> valueType) {
        try {
            return JSON.parseObject(formatJson(o), valueType);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("json format error:{}", e.getMessage());
            return null;
        }
    }

    private static String formatJson(Object o) {
        return JSON.toJSONString(o);
    }
}

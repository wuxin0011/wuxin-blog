package com.wuxin.blog.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wuxin.blog.utils.string.StringUtils;
import org.apache.commons.lang3.StringEscapeUtils;

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
            return JSON.parseArray(formatJson(o), valueType);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


    public static <T> T toObject(Object o, Class<T> valueType) {
        try {
            //  直接使用 出现 JSON解析Object失败 异常 return JSON.parseObject(JSONObject.toJSONString(o), valueType);
            return JSON.parseObject(formatJson(o), valueType);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String removeQuotesAndUnescape(String uncleanJson) {
        String noQuotes = uncleanJson.replaceAll("^\"|\"$", "");
        return StringEscapeUtils.unescapeJava(noQuotes);
    }

    private static String formatJson(Object o) {
        // 获取初始JSONStr
        String jsonString = JSON.toJSONString(o);
        // 删除格式化字符串
        String s = removeQuotesAndUnescape(jsonString);
        // 解析为Object对象
        Object o1 = JSON.toJSON(s);
        // 返回JSON
        return JSON.toJSONString(o1);
    }
}

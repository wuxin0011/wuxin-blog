package com.wuxin.blog.utils;

import com.wuxin.blog.exception.CustomException;
import com.wuxin.blog.pojo.User;
import com.wuxin.blog.utils.string.StringUtils;

/**
 * @Author: wuxin001
 * @Date: 2022/01/04/0:21
 * @Description:
 */
public class ThrowUtils {

    public static final CustomException CUSTOM_EXCEPTION = new CustomException(500, "服务器开小差去了哦~");

    public static final CustomException NOT_FOUND = new CustomException("获取不到内容");

    public static void isNull(Object o, String message) {
        if (StringUtils.isNull(o)) {
            throw NOT_FOUND;
        }
    }

    public static void isNull(Object o) {
        if (StringUtils.isNull(o)) {
            throw NOT_FOUND;
        }
    }

    public static void userNull(User user) {
        if (StringUtils.isNull(user)) {
            throw new CustomException("不用户存在！");
        }
        if (StringUtils.isNull(user.getNickname())) {
            throw new CustomException("获取昵称获取失败！");
        }
    }


    public static void ops(Integer i, String message) {
        if (i != 1) {
            throw new CustomException(message);
        }
    }


    public static void ops(Integer i) {

        if (i != 1) {
            throw CUSTOM_EXCEPTION;
        }
    }

}

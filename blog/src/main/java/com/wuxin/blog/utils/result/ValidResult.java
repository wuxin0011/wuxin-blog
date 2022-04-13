package com.wuxin.blog.utils.result;

import com.wuxin.blog.utils.string.StringUtils;

/**
 * @Author: wuxin001
 * @Date: 2022/04/11/8:42
 * @Description: 结果工具类
 */
public class ValidResult {



    public static Result result(Object o, String success, String error) {
        if (o instanceof Integer) {
            int i = (int) o;
            if (i > 0) {
                return ok(success);
            }
        }
        if (o instanceof Boolean) {
            boolean b = (boolean) o;
            if (b) {
                return ok(success);
            }
        }
        return error(error);
    }

    public static Result result(Object o, String success) {
        return result(o, success, null);
    }

    public static Result ok(String msg) {
        return StringUtils.isNotNull(msg) ? Result.ok(msg) : Result.ok();
    }

    public static Result error(String msg) {
        return StringUtils.isNotNull(msg) ? Result.error(msg) : Result.error();
    }

    public static Result isOk(boolean b, String msg) {
        return b ? ok(msg) : error(msg);
    }

    public static Result isOk(boolean b, String msg, String error) {
        return b ? ok(msg) : error(error);
    }

    public static Result isError(boolean b, String error) {
        return isOk(!b, null,error);
    }

    public static Result isError(boolean b, String success, String error) {
        return isOk(!b, success, error);
    }


}

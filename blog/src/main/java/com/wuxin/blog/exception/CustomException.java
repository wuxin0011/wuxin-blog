package com.wuxin.blog.exception;

/**
 * @Author: wuxin001
 * @Date: 2022/01/01/22:59
 * @Description: 自定义异常 默认402
 */
public class CustomException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private int code;
    private String message;

    public CustomException() {
        super();
        this.code = 402;
    }

    public CustomException(String message) {
        super(message);
        this.code = 402;
    }

    public CustomException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

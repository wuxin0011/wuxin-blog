package com.wuxin.blog.service;

import com.wuxin.blog.pojo.CommentReply;


/**
 * @Author: wuxin001
 * @Date: 2021/10/03/1:35
 * @Description:
 */
public interface MailService {

    /**
     * 发送邮箱验证码
     *
     * @param email   邮箱
     */
    void sendMimeMail(String email);





    /**
     * 订阅消息发送
     *
     */
    void pubMessage(String username,String content ,Integer type,Long BlogId);




    /**
     * 订阅消息发送
     */
    void pubMessage(CommentReply reply,String email);



}

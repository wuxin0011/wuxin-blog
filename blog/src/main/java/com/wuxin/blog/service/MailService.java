package com.wuxin.blog.service;

import com.wuxin.blog.pojo.CommentReply;


/**
 * @Author: wuxin001
 * @Date: 2021/10/03/1:35
 * @Description:
 */
public interface MailService {

    /**
     * 发送邮箱验证码 默认有效时间为10分钟
     *
     * @param email 邮箱
     */
    void sendMimeMail(String email);


    /**
     * 发送用户邮件给博主
     *
     * @param username 评论用户名
     * @param content  评论内容
     * @param type     文章类型
     * @param blogId   文章id
     */
    void pubMessage(String username, String content, Integer type, Long blogId);


    /**
     * 邮件发送给用户
     *
     * @param reply 回复内容
     * @param email 发送用户的游戏
     */
    void pubMessage(CommentReply reply, String email);


}

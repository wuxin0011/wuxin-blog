package com.wuxin.blog.mode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: wuxin001
 * @Date: 2022/01/20/14:03
 * @Description: 缓存评论用户信息
 */
@Data
public class UserComment {


    /**
     * 评论用户Id
     */
    private Long userId;


    /**
     * 评论用户Id
     */
    private String nickname;


    /**
     * email
     */
    private String email;


    /**
     * 是否发布消息
     */
    private boolean subscription;

    /**
     * 用户头像
     */
    private String avatar;


    private static UserComment userComment(){
        return new UserComment();
    }



    public static UserComment getInstance(Long userId,String nickname,String email,String avatar,boolean subscription){
        UserComment userComment = userComment();
        userComment.setNickname(nickname);
        userComment.setEmail(email);
        userComment.setAvatar(avatar);
        userComment.setUserId(userId);
        userComment.setSubscription(subscription);
        return userComment;
    }



}

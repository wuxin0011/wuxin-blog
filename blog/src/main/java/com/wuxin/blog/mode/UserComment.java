package com.wuxin.blog.mode;

import com.wuxin.blog.utils.string.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

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

    //
    // /**
    //  * 记录用户信息修改次数 默认为0 表示未修改！
    //  */
    // private int modify;


    private static UserComment userComment() {
        return new UserComment();
    }


    public static UserComment getInstance(Long userId, String nickname, String email, String avatar, boolean subscription) {
        UserComment userComment = userComment();
        userComment.setNickname(nickname);
        userComment.setEmail(email);
        userComment.setAvatar(avatar);
        userComment.setUserId(userId);
        userComment.setSubscription(subscription);
        return userComment;
    }

    /**
     * 重写userComment判断
     * userid 必须相等
     * nickname 、email 二者一个相等判断用户相等
     *
     * @param o userComment
     * @return true
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserComment that = (UserComment) o;

        if (userIsNull(this) || userIsNull(that)) {
            return false;
        }
        return Objects.equals(userId, that.userId) && (Objects.equals(nickname, that.nickname) || Objects.equals(email, that.email));
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, nickname, email);
    }

    public static boolean userIsNull(UserComment userComment) {
        return userComment == null || userComment.getUserId() == null || StringUtils.isEmpty(userComment.getNickname()) || StringUtils.isEmpty(userComment.getEmail());
    }

    public static boolean userInfoIsChange(UserComment u1, UserComment u2) {
        // 如果用户相等
        if (u1.equals(u2)) {
            if (!u1.getNickname().equals(u2.getNickname())) {
                return true;
            }
            if (!u1.getEmail().equals(u2.getEmail())) {
                return true;
            }
            return !u1.isSubscription() == u2.isSubscription();
        }
        return false;
    }
}

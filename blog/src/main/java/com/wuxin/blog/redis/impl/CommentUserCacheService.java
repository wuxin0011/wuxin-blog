package com.wuxin.blog.redis.impl;

import com.wuxin.blog.constant.RedisKey;
import com.wuxin.blog.constant.RedisKeyExpire;
import com.wuxin.blog.mode.UserComment;
import com.wuxin.blog.pojo.User;
import com.wuxin.blog.redis.RedisService;
import com.wuxin.blog.service.UserService;
import com.wuxin.blog.utils.JsonFormatUtils;
import com.wuxin.blog.utils.string.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: wuxin001
 * @Date: 2022/01/23/23:59
 * @Description:
 */
@Component
public class CommentUserCacheService {

    private List<UserComment> userCommentList;

    @Autowired
    private RedisService redisService;

    @Autowired
    private UserService userService;

    // 根据id获取用户
    public UserComment getUserComment(Long userId) {
        if (userId == null) {
            return null;
        }
        getUserCommentList();
        for (UserComment userComment : userCommentList) {
            if (userId.equals(userComment.getUserId())) {
                return userComment;
            }
        }
        User user = userService.selectCommentUserByUserId(userId);
        return addCacheUserComment(user);
    }


    public UserComment getUserComment(String nickname, String email, boolean subscription) {
        if (StringUtils.isEmpty(nickname) && StringUtils.isEmpty(email)) {
            return null;
        }
        getUserCommentList();
        for (UserComment userComment : userCommentList) {
            if (nickname.equals(userComment.getNickname()) && email.equals(userComment.getEmail())) {
                // 判断用户信息是否修改
                if (subscription != userComment.isSubscription()) {
                    userComment.setSubscription(subscription);
                    // userComment.setModify(userComment.getModify() + 1);
                    // 更新
                    updateUserCommentList();
                }

                return userComment;
            }
        }
        return null;
    }


    // 添加缓存用户信息
    public UserComment addCacheUserComment(User user) {
        if (user == null || user.getUserId() == null || StringUtils.isEmpty(user.getNickname()) || StringUtils.isEmpty(user.getEmail())) {
            return null;
        }
        getUserCommentList();
        UserComment instance = UserComment.getInstance(user.getUserId(), user.getNickname(), user.getEmail(), user.getAvatar(), user.isSubscription());
        // 由于用户信息修改可能获取不到判断一下 用户信息是否修改
        userIsExist(instance.getUserId());
        this.userCommentList.add(instance);
        updateUserCommentList();
        return instance;
    }

    /**
     * 缓存中是否已经有用户信息
     *
     * @param userId userid
     * @return
     */
    public boolean userIsExist(Long userId) {
        if (userId == null) {
            return false;
        }
        // getUserCommentList();
        for (UserComment comment : this.userCommentList) {
            if (comment != null && userId.equals(comment.getUserId())) {
                // 如果缓存获取不到用户信息 但是用户存在，说明用户信息修改l
                this.userCommentList.remove(comment);
                return true;
            }
        }
        return false;
    }


    /**
     * 用户信息很少修改,缓存半个月
     */
    public void updateUserCommentList() {
        redisService.set(RedisKey.USER_COMMENT_LIST, this.userCommentList, RedisKeyExpire.MOUTH_HALF);
    }


    // 获取所有用户信息
    public List<UserComment> getUserCommentList() {
        if (redisService.hasKey(RedisKey.USER_COMMENT_LIST)) {
            this.userCommentList = JsonFormatUtils.objectToArr(redisService.hasKey(RedisKey.USER_COMMENT_LIST), UserComment.class);
        } else {
            this.userCommentList = new ArrayList<>();
        }
        return userCommentList;
    }

}

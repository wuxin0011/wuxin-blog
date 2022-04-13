package com.wuxin.blog.mode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author: wuxin001
 * @Date: 2022/01/06/17:14
 * @Description: 用户账号信息修改
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAccount implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户登录名
     */
    private String username;

    /**
     *昵称
     */
    private String nickname;

    /**
     * 新密码
     */
    private String newPassword;

    /**
     * 邮箱
     */
    private String email;

    /**
     * token
     */
    private String token;


    /**
     * 有效时间
     */
    private Long expireTime;

}

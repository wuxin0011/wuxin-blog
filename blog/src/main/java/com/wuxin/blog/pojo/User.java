package com.wuxin.blog.pojo;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.wuxin.blog.pojo.mode.CreateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Administrator
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@TableName("wx_user")
public class User extends CreateTime  implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 使用JsonSerialize处理雪花算法生成id过长精度丢失问题
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @TableId(value = "user_id", type = IdType.ASSIGN_ID)
    private Long userId;


    /**
     * 用户名 登录时使用
     */
    private String username;

    /**
     * 密码 登录时使用
     */
    private String password;

    /**
     * 昵称，默认和用户名一致，后台可修改
     */
    private String nickname;

    /**
     * 密码盐
     */
    private String salt;

    /**
     * 头像地址
     */
    private String avatar;

    /**
     * 邮箱 唯一 由于注册、登录、邮件消息
     */
    private String email;

    /**
     * 手机号，本打算注册使用手机短信验证码，但是手机验证码申请不通过，
     * 另外 手机号申请免费短信有次数限制，好像是100条
     */
    private String phone;

    /**
     * 信息修改时间
     */
    private Date updateTime;

    /**
     * 个人名言
     */
    private String motto;

    /**
     * 用户账号状态
     */
    private boolean status;

    /**
     * 消息订阅 评论回复信息接收
     */
    private boolean subscription ;

    /**
     * 角色信息 默认 普通用户
     */
    @TableField(value = "role_id")
    private Long roleId;

    @TableField(exist = false)
    private String code;
    @TableField(exist = false)
    private String roleName;
}

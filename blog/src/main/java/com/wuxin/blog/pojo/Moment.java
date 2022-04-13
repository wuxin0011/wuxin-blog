package com.wuxin.blog.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: wuxin001
 * @Date: 2021/10/01/11:16
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("wx_moment")
public class Moment implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @TableId(value = "moment_id", type = IdType.ASSIGN_ID)
    private Long momentId;

    /**
     * 内容
     */
    private String content;

    /**
     * 发布日期
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 点赞数
     */
    private int likes;

    /*
     * 用户
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("user_id")
    private Long userId;


    /**
     * 用户名
     */
    @TableField(exist = false)
    private String username;


    /**
     * 用户头像
     */
    @TableField(exist = false)
    private String avatar;
}

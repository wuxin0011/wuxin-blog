package com.wuxin.blog.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: wuxin001
 * @Date: 2022/03/21/10:00
 * @Description: 访客统计
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("wx_visitor")
public class Visitor {

    /**
     * 使用JsonSerialize处理雪花算法生成id过长精度丢失问题
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @TableId(value = "id",type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 访客标识
     */
    private String uuid;

    /**
     * 访问ip
     */
    private String ip;

    /**
     * 访问地址
     */
    private String address;

    /**
     * 访问操作系统
     */
    private String os;

    /**
     * 浏览器
     */
    private String browser;


    /**
     * 最后访问时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}

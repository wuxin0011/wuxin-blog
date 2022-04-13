package com.wuxin.blog.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author: wuxin001
 * @Date: 2021/12/21/11:12
 * @Description:
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("wx_my_system")
public class MySystem implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final Integer SYSTEM_ID = 1;

    @JsonSerialize(using = ToStringSerializer.class)
    @TableId(value = "id",type = IdType.ASSIGN_ID)
    private Integer id;


    /**
     * 后台登录图标
     */
    @TableField(value = "admin_icon")
    private String adminIcon;


    /**
     * 登录地址
     */
    @TableField(value = "login_url")
    private String loginUrl;


    /**
     * 网站地址
     */
    @TableField(value = "web_url")
    private String webUrl;


    /**
     * 二维码
     */
    @TableField(value = "er_code")
    private String erCode;


    /**
     * 博主评论标签名称
     */
    @TableField("comment_label_name")
    private String commentLabelName;


    /**
     * 博主评论标签颜色
     */
    @TableField("comment_label_color")
    private String commentLabelColor;



    /**
     * 名称
     */
    @TableField("web_name")
    private String webName;

    /**
     * 域名备案
     */
    @TableField("web_record")
    private String webRecord;


}

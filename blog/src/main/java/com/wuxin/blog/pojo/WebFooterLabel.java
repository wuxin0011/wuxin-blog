package com.wuxin.blog.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
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
 * @Date: 2021/12/24/22:46
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("wx_web_footer_label")
public class WebFooterLabel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @TableId(value = "id",type = IdType.ASSIGN_ID)
    private Integer id;

    /**
     * 类型名
     */
    private String typeName;

    /**
     * 类型颜色
     */
    private String typeColor;

    /**
     * 名称
     */
    private String name;

    /**
     * 颜色
     */
    private String color;

    /**
     * 访问地址
     */
    private String url;

    /**
     * 相关描述
     */
    private String title;
}

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
 * @Date: 2021/09/01/12:57
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("wx_blog_tag")
public class BlogTag implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = ToStringSerializer.class)
    @TableId(value = "blog_tag_id",type = IdType.AUTO)
    private Long id;
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "blog_id")
    private Long blogId;
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "tag_id")
    private Long tagId;
}

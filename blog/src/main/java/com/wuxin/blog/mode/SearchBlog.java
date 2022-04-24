package com.wuxin.blog.mode;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: wuxin001
 * @Date: 2022/01/25/13:36
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchBlog {


    @JsonSerialize(using = ToStringSerializer.class)
    private Long blogId;
    private String title;
    private String content;
    private Date createTime;
}

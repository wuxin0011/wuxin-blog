package com.wuxin.blog.pojo;


import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.wuxin.blog.pojo.mode.CreateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Administrator
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("wx_update_question")
public class UpdateQuestion extends CreateTime implements Serializable {

    private static final long serialVersionUID = 1L;
    @JsonSerialize(using = ToStringSerializer.class)
    @TableId(value = "id",type = IdType.ASSIGN_ID)
    private Long id;
    private String content;
    private String color;
}

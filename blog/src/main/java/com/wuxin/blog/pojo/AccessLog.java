package com.wuxin.blog.pojo;


import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.wuxin.blog.mode.Log;
import lombok.*;

import java.io.Serializable;

/**
 * 访问信息来源实体类
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@TableName("wx_access_log")
public class AccessLog extends Log implements Serializable {

    @JsonSerialize(using = ToStringSerializer.class)
    @TableId(value = "id",type = IdType.ASSIGN_ID)
    private Long id;


}

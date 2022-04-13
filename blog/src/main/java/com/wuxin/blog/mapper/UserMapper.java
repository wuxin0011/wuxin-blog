package com.wuxin.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wuxin.blog.pojo.User;
import org.apache.ibatis.annotations.Mapper;


/**
 * @author Administrator
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}

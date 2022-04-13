package com.wuxin.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wuxin.blog.pojo.CommentReply;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: wuxin001
 * @Date: 2021/09/02/16:55
 * @Description: 评论
 */
@Mapper
public interface CommentReplyMapper extends BaseMapper<CommentReply> {
}

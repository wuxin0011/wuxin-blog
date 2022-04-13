package com.wuxin.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuxin.blog.mapper.BlogTagMapper;
import com.wuxin.blog.mapper.TagMapper;
import com.wuxin.blog.pojo.BlogTag;
import com.wuxin.blog.service.BlogTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: wuxin001
 * @Date: 2022/03/07/13:02
 * @Description:
 */
@Service
public class BlogTagServiceImpl extends ServiceImpl<BlogTagMapper, BlogTag> implements BlogTagService {

    @Autowired
    private BlogTagMapper blogTagMapper;


    @Override
    public void deleteByTagId(Long tagId) {
        LambdaQueryWrapper<BlogTag> query = new LambdaQueryWrapper<>();
        query.eq(BlogTag::getTagId,tagId);
        blogTagMapper.delete(query);
    }

    @Override
    public void deleteByBlogId(Long blogId) {
        LambdaQueryWrapper<BlogTag> query = new LambdaQueryWrapper<>();
        query.eq(BlogTag::getBlogId,blogId);
        blogTagMapper.delete(query);
    }

    @Override
    public void updateBlogTag(List<Long> tagIds, Long blogId) {

    }
}

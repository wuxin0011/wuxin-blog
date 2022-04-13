package com.wuxin.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuxin.blog.pojo.BlogTag;

import java.util.List;

/**
 * @Author: wuxin001
 * @Date: 2022/03/07/13:01
 * @Description:
 */
public interface BlogTagService extends IService<BlogTag> {

    /**
     * 删除文章标签
     * @param tagId tagid
     */
    void deleteByTagId(Long tagId);

    /**
     * 删除文章标签
     * @param blogId blogid
     */
    void deleteByBlogId(Long blogId);


    /**
     * 修改文章标签
     * @param tagIds tagid
     * @param blogId blogid
     */
    void updateBlogTag(List<Long> tagIds, Long blogId);
}

package com.wuxin.blog.redis.impl;

import com.wuxin.blog.constant.RedisKey;
import com.wuxin.blog.constant.RedisKeyExpire;
import com.wuxin.blog.pojo.Blog;
import com.wuxin.blog.redis.RedisService;
import com.wuxin.blog.service.BlogService;
import com.wuxin.blog.utils.string.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @Author: wuxin001
 * @Date: 2022/04/29/20:27
 * @Description: 文章浏览量缓存
 */

@Component
public class BlogViewsCacheService {

    private static final Logger logger = LoggerFactory.getLogger(BlogViewsCacheService.class);

    @Autowired
    private RedisService redisService;

    @Autowired
    private BlogService blogService;

    /**
     * 已经缓存的文章列表 为缓存入库
     */
    private List<String> blogList = null;

    /**
     * 文章缓存列表
     */
    public void getBlogList() {
        boolean hasKey = redisService.hasKey(RedisKey.blog_list_save);
        if (hasKey) {
            Object o = redisService.get(RedisKey.blog_list_save);
            if (o != null) {
                this.blogList = (List<String>) o;
                return;
            }
        }
        this.blogList = new ArrayList<String>();
        redisService.set(RedisKey.blog_list_save, this.blogList, RedisKeyExpire.DAY_1);
    }


    public void updateBlogList(Long blogId) {
        if (blogId == null) {
            return;
        }
        // 获取文章列表
        getBlogList();
        if (this.blogList.size() != 0) {
            String s = String.valueOf(blogId);
            // 如果不包含该id
            if (!this.blogList.contains(s)) {
                this.blogList.add(s);
                // 添加到缓存
                redisService.set(RedisKey.blog_list_save, this.blogList, RedisKeyExpire.MOUTH_HALF);
                return;
            }
        }
        redisService.set(RedisKey.blog_list_save, this.blogList, RedisKeyExpire.MOUTH_HALF);
    }

    /**
     * 文章缓存浏览量
     */
    public int viewsCache(Long blogId) {
        String key = RedisKey.getKey(blogId, RedisKey.blog_views);
        boolean hasKey = redisService.hasKey(key);
        updateBlogList(blogId);
        if (hasKey) {
            Object o = redisService.get(key);
            if (o != null) {
                int views = Integer.parseInt(String.valueOf(o));
                views++;
                redisService.set(key, views, RedisKeyExpire.DAY_1);
                return views;
            }
        }
        // 从数据库中获取浏览量
        Blog blog = blogService.queryViewsById(blogId);
        redisService.set(key, blog.getViews(), RedisKeyExpire.DAY_1);
        return blog.getViews();
    }

    /**
     * uuid是否缓存
     */
    public boolean uuidIsCache(Long blogId, String uuid) {
        String key = RedisKey.getKey(blogId, RedisKey.blog_id_list);
        boolean hasKey = redisService.hasKey(key);
        List<String> uuidList = null;
        if (hasKey) {
            uuidList = (List<String>) redisService.get(key);
            if (uuidList != null && !uuidList.contains(uuid)) {
                uuidList.add(uuid);
                redisService.set(key, uuidList, RedisKeyExpire.DAY_1);
                viewsCache(blogId);
                return false;
            } else if (uuid != null) {
                return true;
            }
        }
        viewsCache(blogId);
        redisService.set(key, new ArrayList<String>(), RedisKeyExpire.DAY_1);
        return false;
    }

    /**
     * 缓存入库
     */
    public void blogViewsSave() {
        getBlogList();
        for (String s : this.blogList) {
            long blogId = Long.parseLong(s);
            try {
                // 休眠500ms缓存
                Thread.sleep(500);
                Blog blog = blogService.queryViewsById(blogId);
                blog.setViews(viewsCache(blogId));
                blogService.updateBlog(blog);
            } catch (InterruptedException e) {
                e.printStackTrace();
                logger.error("更新失败！error:{}", e.getMessage());
            }

        }
    }


}

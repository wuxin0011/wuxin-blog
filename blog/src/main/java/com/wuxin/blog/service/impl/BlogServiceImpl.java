package com.wuxin.blog.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuxin.blog.exception.CustomException;
import com.wuxin.blog.mapper.*;
import com.wuxin.blog.mode.SearchBlog;
import com.wuxin.blog.pojo.Blog;
import com.wuxin.blog.pojo.BlogTag;
import com.wuxin.blog.pojo.Comment;
import com.wuxin.blog.pojo.Tag;
import com.wuxin.blog.redis.CacheService;
import com.wuxin.blog.redis.RedisKey;
import com.wuxin.blog.redis.RedisService;
import com.wuxin.blog.service.BlogService;
import com.wuxin.blog.utils.JsonFormatUtils;
import com.wuxin.blog.utils.mapper.MapperUtils;
import com.wuxin.blog.utils.string.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: wuxin001
 * @Date: 2021/10/01/11:08
 * @Description:
 */
@Service
@Transactional(rollbackFor = {Exception.class})
public class BlogServiceImpl implements BlogService {


    private final static String BLOG = RedisKey.BLOG;


    private final static String BLOG_LIST = RedisKey.BLOG_LIST;

    private final static String BLOG_PAGE_LIST = "blog:page:list:";


    @Autowired
    private BlogMapper blogMapper;


    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private BlogTagMapper blogTagMapper;

    @Autowired
    private RedisService redisService;

    @Autowired
    private CacheService cacheService;

    @Override
    public Long addBlog(Blog blog) {
        blogMapper.insert(blog);
        // 删除redis中博客列表
        deleteBlogListCache();
        return blog.getBlogId();
    }


    @Override
    public List<Blog> getAllBlogList() {
        return new LambdaQueryChainWrapper<>(blogMapper).select(Blog::getBlogId, Blog::getTitle, Blog::getCreateTime).orderByDesc(Blog::getCreateTime).list();
    }

    @Override
    public List<SearchBlog> searchBlog(String keywords) {
        return blogMapper.searchBlog(keywords);
    }

    @Override
    public void delBlog(Long blogId) {
        deleteBlogCache(blogId);
        deleteBlogListCache();
        blogMapper.deleteById(blogId);
    }


    @Override
    public void updateBlog(Blog blog) {
        String blogKey = getBlogKey(blog.getBlogId());
        deleteBlogCache(blog.getBlogId());
        MapperUtils.lambdaUpdateChainWrapper(blogMapper).eq(Blog::getBlogId, blog.getBlogId()).update(blog);
        deleteBlogListCache();
    }

    @Override
    public IPage<Blog> findBlog(Integer current, Integer limit) {
        String hk = getListBlogKey(current);
        boolean b = redisService.hHasKey(BLOG_LIST, hk);
        if (b) {
            IPage<Blog> page = (IPage<Blog>) redisService.hget(BLOG_LIST, hk);
            if (page.getRecords().size() != 0) {
                return page;
            }
        }
        LambdaQueryChainWrapper<Blog> queryChainWrapper = new LambdaQueryChainWrapper<>(blogMapper);
        queryChainWrapper.orderByDesc(Blog::getTop).orderByDesc(Blog::getCreateTime).eq(Blog::isPublish, 1);
        queryChainWrapper.select(Blog::getBlogId, Blog::getCreateTime, Blog::getDescription, Blog::getCid, Blog::getViews, Blog::getTitle, Blog::getTop, Blog::getWords, Blog::getUserId);
        Page<Blog> page = queryChainWrapper.page(new Page<>(current, limit));
        for (Blog blog : page.getRecords()) {
            getBlogInfo(blog, 1);
        }
        // 缓存七天
        redisService.hset(BLOG_LIST, hk, page, 604800L);
        return page;
    }

    @Override
    public List<SearchBlog> newBlog() {
        // 从数据库中获取list
        return blogMapper.newBlog();
    }

    @Override
    public Blog findBlogByBlogId(Long blogId) {
        // 从redis查找
        String hk = getBlogKey(blogId);
        boolean b = redisService.hHasKey(BLOG, hk);
        if (b) {
            // String s = (String) ;
            // 如果blog对象不为空
            // Blog blog = JSONObject.parseObject(s, Blog.class);
            Blog blog = JsonFormatUtils.toObject(redisService.hget(BLOG, hk), Blog.class);
            if (StringUtils.isNotNull(blog)) {
                return blog;
            }
        }
        // 从数据库中查找
        Blog blog = blogMapper.selectById(blogId);
        if (StringUtils.isNull(blog)) {
            throw new CustomException("文章不存在！");
        }
        Blog blogInfo = getBlogInfo(blog, 0);
        // 缓存
        redisService.hset(BLOG, hk, JSONObject.toJSONString(blogInfo));
        return blogInfo;
    }

    @Override
    public IPage<Blog> findBlogByUserId(Long userId, Integer current, Integer size) {
        return new LambdaQueryChainWrapper<>(blogMapper).orderByDesc(Blog::getCreateTime).eq(Blog::getUserId, userId).page(new Page<>(current, size));
    }

    @Override
    public List<Blog> randomBlog() {
        List<Blog> blogList = blogMapper.getRandomFiveBlog();
        blogList.forEach(blog -> {
            getBlogInfo(blog, 0);
        });
        return blogList;
    }


    @Override
    public Integer countAllBlogViews() {
        int count = 0;
        List<Blog> blogList = new LambdaQueryChainWrapper<Blog>(blogMapper).list();
        for (Blog blog : blogList) {
            count = count + blog.getViews();
        }
        return count;
    }

    @Override
    public Integer blogCount() {
        return blogMapper.selectCount(null);
    }

    @Override
    public IPage<Blog> findBlogPage(Integer current, Integer limit, String keywords, String start, String end, Long cid) {
        String key = RedisKey.getKey(BLOG_PAGE_LIST, current, limit, cid + start + end + keywords);

        boolean hasKey = redisService.hHasKey(BLOG_PAGE_LIST, key);
        if (hasKey) {
            IPage<Blog> page = (IPage<Blog>) redisService.hget(BLOG_PAGE_LIST, key);
            if (StringUtils.isNotNull(page) && page.getRecords().size() != 0) {
                return page;
            }
        }

        LambdaQueryChainWrapper<Blog> queryChainWrapper = new LambdaQueryChainWrapper<>(blogMapper);
        queryChainWrapper.eq(StringUtils.isNotNull(cid), Blog::getCid, cid);
        queryChainWrapper.orderByDesc(Blog::getCreateTime);
        queryChainWrapper.like(StringUtils.isNotEmpty(keywords), Blog::getTitle, keywords);
        queryChainWrapper.between(StringUtils.isNotEmpty(start) && StringUtils.isNotEmpty(end), Blog::getCreateTime, start, end);
        Page<Blog> page = queryChainWrapper.page(new Page<>(current, limit));
        page.getRecords().forEach(blog -> {
            getBlogInfo(blog, 1);
        });
        System.out.println("=================blog page mysql ================");
        redisService.hset(BLOG_PAGE_LIST, key, page, 60L);
        return page;
    }

    @Override
    public void deleteByCid(Long cid) {
        LambdaQueryWrapper<Blog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Blog::getCid, cid);
        List<Blog> blogList = blogMapper.selectList(queryWrapper);
        blogList.forEach(blog -> deleteBlogCache(blog.getBlogId()));
        blogMapper.delete(queryWrapper);
        deleteBlogListCache();
    }

    @Override
    public int delBlogByUserId(Long userId) {
        LambdaQueryWrapper<Blog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Blog::getUserId, userId);
        List<Blog> blogList = blogMapper.selectList(queryWrapper);
        blogList.forEach(blog -> deleteBlogCache(blog.getBlogId()));
        deleteBlogListCache();
        return blogMapper.delete(queryWrapper);
    }

    @Override
    public Long getUserIdByBlogId(Long blogId) {
        return blogMapper.selectById(blogId).getUserId();
    }

    @Override
    public SearchBlog beforeBlog(Long blogId) {
        return blogMapper.beforeBlog(blogId);
    }

    @Override
    public SearchBlog nextBlog(Long blogId) {
        return blogMapper.nextBlog(blogId);
    }

    public Blog getBlogInfo(Blog blog, Integer type) {
        // 获取用户昵称
        blog.setUsername(cacheService.getUserCommentByUserId(blog.getUserId()).getNickname());
        // 获取分类
        blog.setCategory(cacheService.getCategoryCacheByCid(blog.getCid()));
        // 获取评论数量
        if (type.equals(1)) {
            Integer count = new LambdaQueryChainWrapper<>(commentMapper).eq(Comment::getType, Comment.BLOG_COMMENT).eq(StringUtils.longIsNotNull(blog.getBlogId()), Comment::getBlogId, blog.getBlogId()).count();
            blog.setCommentNum(count);
        }
        // 返回所有标签
        List<BlogTag> list = new LambdaQueryChainWrapper<>(blogTagMapper).eq(BlogTag::getBlogId, blog.getBlogId()).list();
        List<Tag> tags = new ArrayList<>();
        list.forEach(blogTag -> {
            Tag tagCacheById = cacheService.getTagCacheById(blogTag.getTagId());
            if (StringUtils.isNotNull(tagCacheById)) {
                tags.add(tagCacheById);
            }
        });
        //添加标签名
        blog.setTags(tags);
        return blog;
    }

    /**
     * 使用redis遍历删除blog-list
     */
    public void deleteBlogListCache() {
        String key = RedisKey.getKey(BLOG_PAGE_LIST, 1, 10, "");
        redisService.hdel(BLOG_PAGE_LIST, key);
        // 获取文章总数
        Integer total = blogMapper.selectCount(null);
        // 前台设置了页码大小为5
        Integer size = 5;
        // 获取页码总数
        int i = (total / size) + 1;
        for (int i1 = 1; i1 < i; i1++) {
            String hk = RedisKey.getKey(i1, BLOG_LIST);
            boolean b = redisService.hHasKey(BLOG_LIST, hk);
            if (b) {
                redisService.hdel(BLOG_LIST, hk);
            }
        }
        cacheService.deleteBlogAllListCache();

    }

    /**
     * 删除文章缓存
     */
    public void deleteBlogCache(Long blogId) {
        redisService.hdel(BLOG, getBlogKey(blogId));
    }

    public String getBlogKey(Long blogId) {
        return RedisKey.getKey(blogId, BLOG);
    }

    public String getListBlogKey(int i) {
        return RedisKey.getKey(i, BLOG_LIST);
    }


}

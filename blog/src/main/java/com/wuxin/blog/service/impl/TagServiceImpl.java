package com.wuxin.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuxin.blog.constant.Constants;
import com.wuxin.blog.exception.CustomException;
import com.wuxin.blog.mapper.BlogMapper;
import com.wuxin.blog.mapper.CategoryMapper;
import com.wuxin.blog.mapper.TagMapper;
import com.wuxin.blog.mapper.BlogTagMapper;
import com.wuxin.blog.pojo.Blog;
import com.wuxin.blog.pojo.BlogTag;
import com.wuxin.blog.pojo.Category;
import com.wuxin.blog.pojo.Tag;
import com.wuxin.blog.redis.CacheService;
import com.wuxin.blog.constant.RedisKey;
import com.wuxin.blog.redis.RedisService;
import com.wuxin.blog.service.TagService;
import com.wuxin.blog.utils.ThrowUtils;
import com.wuxin.blog.utils.mapper.MapperUtils;
import com.wuxin.blog.utils.string.ListUtil;
import com.wuxin.blog.utils.string.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @Author: wuxin001
 * @Date: 2021/10/01/11:08
 * @Description:
 */
@Service
@Transactional(rollbackFor = {Exception.class})
public class TagServiceImpl implements TagService {

    private static final String TAG_LIST = RedisKey.TAG_LIST;

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private BlogTagMapper blogTagMapper;


    @Autowired
    private BlogMapper blogMapper;


    @Autowired
    private RedisService redisService;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public void add(Tag tag) {
        ThrowUtils.ops(tagMapper.insert(tag), "标签添加失败！");
        deleteTagListCache();
    }


    @Override
    public void delete(Long id) {
        ThrowUtils.ops(tagMapper.deleteById(id), "删除失败！标签不存在");
        deleteTagListCache();
    }

    @Override
    public void update(Tag tag) {
        ThrowUtils.ops(tagMapper.updateById(tag), "修改失败！标签不存在");
        deleteTagListCache();
    }

    @Override
    public Tag findTagByName(String tagName) {
        Tag tag = null;
        // 从缓存中获取tagName，判断tag是否存在
        tag = cacheService.getTagCache(tagName);
        if (tag == null) {
            // 从数据库中读取name是否存在
            try {
                tag = new LambdaQueryChainWrapper<>(tagMapper).eq(Tag::getName, tagName).one();
            } catch (Exception e) {
                e.printStackTrace();
                throw new CustomException("该标签不唯一！请前往控制台删除！");
            }
            if (tag == null) {
                throw new CustomException("标签不存在！");
            }
        }
        return tag;
    }

    @Override
    public List<Tag> list() {
        return cacheService.getTagList();
        // return tagMapper.selectList(null);
    }

    @Override
    public List<Tag> selectBlogTag(Long blogId) {
        LambdaQueryChainWrapper<BlogTag> chain = new LambdaQueryChainWrapper<>(blogTagMapper);
        List<BlogTag> list = chain.eq(BlogTag::getBlogId, blogId).list();
        ArrayList<Tag> tags = new ArrayList<>();
        for (BlogTag blogTag : list) {
            Tag tag = tagMapper.selectById(blogTag.getTagId());
            tags.add(tag);
        }
        return tags;
    }

    @Override
    public Tag find(Long id) {

        return cacheService.getTagCacheById(id);
    }

    @Override
    public IPage<Tag> selectListByPage(Integer current, Integer limit) {
        return null;
    }

    @Override
    public IPage<Tag> selectListByPage(Integer current, Integer limit, String keywords) {
        LambdaQueryChainWrapper<Tag> queryChainWrapper = new LambdaQueryChainWrapper<>(tagMapper);
        queryChainWrapper.orderByDesc(Tag::getTagId).like(StringUtils.isNotEmpty(keywords), Tag::getName, keywords);
        return queryChainWrapper.page(new Page<>(current, limit));
    }


    @Override
    public void addBlogTag(Long blogId, List<Long> tagIds) {
        tagIds.forEach(tagId -> {
            BlogTag blogTag = new BlogTag();
            blogTag.setBlogId(blogId);
            blogTag.setTagId(tagId);
            blogTagMapper.insert(blogTag);
        });

    }

    @Override
    public void updateBlogTag(Long blogId, List<Long> tagIds) {
        // 比较是否改变了
        List<BlogTag> blogTags = MapperUtils.lambdaQueryWrapper(blogTagMapper).eq(BlogTag::getBlogId, blogId).list();
        List<Long> ids = new ArrayList<>();
        blogTags.forEach(blogTag -> {
            ids.add(blogTag.getTagId());
        });
        // 如果相等就不修改
        if (!ListUtil.listEqual(ids, tagIds)) {
            delBlogTagByBlogId(blogId);
            addBlogTag(blogId, tagIds);
        }

    }

    @Override
    public void delBlogTagByBlogId(Long blogId) {
        LambdaQueryWrapper<BlogTag> objectLambdaQueryWrapper = new LambdaQueryWrapper<BlogTag>();
        objectLambdaQueryWrapper.eq(BlogTag::getBlogId, blogId);
        blogTagMapper.delete(objectLambdaQueryWrapper);
    }


    @Override
    public IPage<Blog> findBlogByTagName(Integer current, Integer size, String tagName) {

        Tag tag = findTagByName(tagName);

        String key = RedisKey.getKey("tag:blog:page", current, size, tagName);
        boolean hasKey = redisService.hHasKey("tag:blog:page", key);
        if (hasKey) {
            Page<Blog> blogPage = (Page<Blog>) redisService.hget("tag:blog,page", key);
            if (StringUtils.isNotNull(blogPage) && blogPage.getRecords().size() != 0) {
                return blogPage;
            }
        }

        Page<Blog> blogPage = new Page<>(current, size);
        // 获取文章标签列表
        IPage<BlogTag> tagPage = new LambdaQueryChainWrapper<>(blogTagMapper).eq(BlogTag::getTagId, tag.getTagId()).page(new Page<>(current, size));
        List<Blog> blogList = new ArrayList<>();
        // 获取文章基本信息
        // 内容不为空！
        if (tagPage.getRecords().size() == 0) {
            return blogPage;
        }

        tagPage.getRecords().forEach(blogTag -> {
            Blog blogInfo = getBlogInfo(blogTag);
            blogList.add(blogInfo);
        });

        // 获取分页信息
        blogPage.setCurrent(tagPage.getCurrent());
        blogPage.setSize(tagPage.getSize());
        blogPage.setTotal(tagPage.getTotal());
        blogPage.setRecords(blogList);
        blogPage.setPages(tagPage.getPages());
        // 缓存有效期10分钟
        redisService.hset("tag:blog:page", key, blogPage, 600L);
        return blogPage;
    }


    @Override
    public List<Object> blogCountByTagName() {
        boolean hasKey = redisService.hasKey("tag:blog:count");
        if (hasKey) {
            List<Object> o = (List<Object>) redisService.get("tag:blog:count");
            if (StringUtils.isNotNull(o) && o.size() != 0) {
                return o;
            }
        }
        List<Tag> list = list();
        List<Object> arrayList = new ArrayList<>();
        list.forEach(tag -> {
            Integer count = MapperUtils.lambdaQueryWrapper(blogTagMapper).eq(BlogTag::getTagId, tag.getTagId()).count();
            Map<String, Object> map = new HashMap<>(Constants.HASH_MAP_INIT);
            map.put("name", tag.getName());
            map.put("value", count);
            arrayList.add(map);
        });
        // 缓存一个小时
        redisService.set("tag:blog:count", arrayList, 3600L);
        return arrayList;
    }

    public Blog getBlogInfo(BlogTag blogTag) {
        // 获取文章
        LambdaQueryChainWrapper<Blog> queryChainWrapper = new LambdaQueryChainWrapper<>(blogMapper);
        queryChainWrapper.select(Blog::getBlogId, Blog::getCreateTime, Blog::getCid, Blog::getDescription, Blog::getTop, Blog::getTitle, Blog::getViews);
        Blog blog = queryChainWrapper.eq(Blog::getBlogId, blogTag.getBlogId()).eq(Blog::isPublish, 1).orderByDesc(Blog::getTop).orderByDesc(Blog::getCreateTime).one();
        // 从缓存中读取分类
        Category category = cacheService.getCategoryCacheByCid(blog.getCid());
        if (category != null) {
            blog.setCategory(category);
        }
        List<Tag> tags = new ArrayList<>();
        // 读取该文章所有标签
        List<BlogTag> list = new LambdaQueryChainWrapper<>(blogTagMapper).eq(BlogTag::getBlogId, blog.getBlogId()).list();
        list.forEach(blogTag1 -> {
            Tag tag = cacheService.getTagCacheById(blogTag1.getTagId());
            if (tag != null) {
                tags.add(tag);
            }
        });
        blog.setTags(tags);
        return blog;
    }

    public void deleteTagListCache() {
        cacheService.deleteTagListCache();
    }


}


package com.wuxin.blog.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuxin.blog.constant.Constants;
import com.wuxin.blog.mapper.BlogMapper;
import com.wuxin.blog.mapper.BlogTagMapper;
import com.wuxin.blog.mapper.CategoryMapper;
import com.wuxin.blog.mapper.TagMapper;
import com.wuxin.blog.pojo.Blog;
import com.wuxin.blog.pojo.BlogTag;
import com.wuxin.blog.pojo.Category;
import com.wuxin.blog.pojo.Tag;
import com.wuxin.blog.redis.CacheService;
import com.wuxin.blog.constant.RedisKey;
import com.wuxin.blog.redis.RedisService;
import com.wuxin.blog.service.CategoryService;
import com.wuxin.blog.utils.ThrowUtils;
import com.wuxin.blog.utils.mapper.MapperUtils;
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
public class CategoryServiceImpl implements CategoryService {

    private static final String BLOG_CATEGORY = "blog:category";


    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private BlogMapper blogMapper;

    @Autowired
    private BlogTagMapper blogTagMapper;

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private RedisService redisService;

    @Override
    public void add(Category category) {
        ThrowUtils.ops(categoryMapper.insert(category), "操作失败");
        deleteCategoryCache();
    }

    @Override
    public void delete(Long id) {
        ThrowUtils.ops(categoryMapper.deleteById(id), "分类标签不存在！");
        deleteCategoryCache();
    }

    @Override
    public void update(Category category) {
        ThrowUtils.ops(categoryMapper.updateById(category), "操作失败！标签不存在");
        deleteCategoryCache();
    }

    @Override
    public List<Category> list() {
        return cacheService.getCategoryListCache();
    }

    @Override
    public Category findCategoryByName(String name) {
        return cacheService.getCategoryCacheByName(name);
    }

    @Override
    public IPage<Blog> findBlogByCategoryName(Integer current, Integer size, String categoryName) {

        String key = RedisKey.getKey(categoryName, current, size);
        boolean hasKey = redisService.hHasKey(BLOG_CATEGORY, key);
        if(hasKey){
            IPage<Blog> page = (IPage<Blog>) redisService.hget(BLOG_CATEGORY, key);
            if(StringUtils.isNotNull(page) && page.getRecords().size() != 0){
                return page;
            }
        }

        Page<Blog> page = new Page<>(current, size);
        Category category = findCategoryByName(categoryName);
        // 如果分类不为空
        if (category == null) {
            return page;
        }
        // 按照分类名分页获取文章
        Page<Blog> blogPage = new LambdaQueryChainWrapper<>(blogMapper)
                .eq(Blog::getCid, category.getCid())
                .orderByDesc(Blog::getTop)
                .orderByDesc(Blog::getCreateTime).eq(Blog::isPublish, 1)
                // 获取文章部分信息
                .select(Blog::getBlogId, Blog::getCid, Blog::getTop, Blog::getDescription, Blog::isPublish, Blog::getCreateTime, Blog::getTitle)
                .page(page);
        blogPage.getRecords().forEach(blog -> {
            blog.setCategory(category);
            List<BlogTag> list = MapperUtils.lambdaQueryWrapper(blogTagMapper).eq(BlogTag::getBlogId, blog.getBlogId()).list();
            List<Tag> tags = new ArrayList<>();
            // 遍历获取文章标签
            list.forEach(blogTag -> {
                // 从缓存中获取标签信息
                Tag tag = cacheService.getTagCacheById(blogTag.getTagId());
                tags.add(tag);
            });
            // 添加文章标签
            blog.setTags(tags);
        });

        redisService.hset(BLOG_CATEGORY,key,blogPage,600);

        // 存入redis// 有效时间为十分钟
        return blogPage;

    }

    @Override
    public Category find(Long id) {
        return cacheService.getCategoryCacheByCid(id);
    }

    @Override
    public IPage<Category> selectListByPage(Integer current, Integer limit, String keywords) {
        String key = RedisKey.getKey(keywords, current, limit);
        boolean hasKey = redisService.hHasKey("category:page:list", key);
        if(hasKey){
            Page<Category> page = (Page<Category>) redisService.hget("category:page:list", key);
            if(StringUtils.isNotNull(page) && page.getRecords().size() !=0){
                return page;
            }
        }
        LambdaQueryChainWrapper<Category> queryChainWrapper = new LambdaQueryChainWrapper<>(categoryMapper);
        Page<Category> page = queryChainWrapper.like(StringUtils.isNotEmpty(keywords), Category::getName, keywords).page(new Page<>(current, limit));
        redisService.hset("category:page:list",key,page,100L);
        return page;
    }


    @Override
    public List<Object> blogCountByCategoryName() {
        boolean hasKey = redisService.hasKey("category:blog:count");
        if(hasKey){
            List<Object> o = (List<Object>) redisService.get("category:blog:count");
            if(StringUtils.isNotNull(o) && o.size()!=0){
                return o;
            }
        }
        List<Category> list = categoryMapper.selectList(null);
        List<Object> arrayList = new ArrayList<>();
        list.forEach(category -> {
            Map<String, Object> map = new HashMap<>(Constants.HASH_MAP_INIT);
            int count = new LambdaQueryChainWrapper<>(blogMapper).eq(Blog::getCid, category.getCid()).count();
            map.put("name", category.getName());
            map.put("value", count);
            arrayList.add(map);
        });
        redisService.set("category:blog:count",arrayList,6000L);
        // 将数据统计如数据库中
        return arrayList;
    }

    @Override
    public IPage<Category> selectListByPage(Integer current, Integer limit) {
        return null;
    }


    private void deleteCategoryCache() {
        cacheService.deleteCategoryListCache();
    }

}

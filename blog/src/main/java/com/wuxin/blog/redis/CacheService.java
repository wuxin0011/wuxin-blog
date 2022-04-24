package com.wuxin.blog.redis;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.wuxin.blog.constant.RedisKey;
import com.wuxin.blog.exception.CustomException;
import com.wuxin.blog.mapper.BlogMapper;
import com.wuxin.blog.mapper.CategoryMapper;
import com.wuxin.blog.mapper.TagMapper;
import com.wuxin.blog.mapper.UserMapper;
import com.wuxin.blog.mode.UserComment;
import com.wuxin.blog.pojo.Blog;
import com.wuxin.blog.pojo.Category;
import com.wuxin.blog.pojo.Tag;
import com.wuxin.blog.pojo.User;
import com.wuxin.blog.utils.JsonFormatUtils;
import com.wuxin.blog.utils.mapper.MapperUtils;
import com.wuxin.blog.utils.string.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: wuxin001
 * @Date: 2022/03/15/9:50
 * @Description:
 */
@Component
public class CacheService {


    @Autowired
    private RedisService redisService;

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private BlogMapper blogMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CategoryMapper categoryMapper;


    private static final String USER_COMMENT_SUB = RedisKey.USER_COMMENT_SUB;
    private static final String TAG_LIST = RedisKey.TAG_LIST;
    private static final String CATEGORY_LIST = RedisKey.CATEGORY_LIST;
    private static final String BLOG_LIST = RedisKey.BLOG_LIST;
    private static final String ALL_BLOG_LIST = RedisKey.ALL_BLOG_LIST;



    public List<Tag> getTagList() {
        // 将tagLit存入redis中
        boolean b = redisService.hasKey(TAG_LIST);
        if (b) {
            List<Tag> list = JsonFormatUtils.objectToArr(redisService.get(TAG_LIST), Tag.class);
            if (list.size() != 0) {
                return list;
            }
        }
        // 从数据库中获取tagList
        List<Tag> tags = MapperUtils.lambdaQueryWrapper(tagMapper).orderByDesc(Tag::getTagId).list();
        redisService.set(TAG_LIST, tags);
        return tags;
    }

    /**
     * 根据id从缓存中获取tagName
     *
     * @param tagId
     * @return tag
     */
    public Tag getTagCacheById(Long tagId) {
        List<Tag> tagListCache = getTagList();
        for (Tag tag : tagListCache) {
            if (StringUtils.isNotNull(tagId) && tagIsNotNull(tag) && tagId.equals(tag.getTagId())) {
                return tag;
            }
        }
        Tag one = new LambdaQueryChainWrapper<>(tagMapper).eq(Tag::getTagId, tagId).one();
        if (tagIsNotNull(one)) {
            tagListCache.add(one);
            redisService.set(TAG_LIST, tagListCache);
        }
        return null;

    }

    /**
     * 从缓存中获取tagName
     *
     * @return tag
     */
    public Tag getTagCache(String tagName) {
        List<Tag> tagListCache = getTagList();
        for (Tag tag : tagListCache) {
            if (tagIsNotNull(tag) && StringUtils.isNotEmpty(tagName) && tag.getName().equals(tagName)) {
                return tag;
            }
        }
        Tag tag = new LambdaQueryChainWrapper<>(tagMapper).eq(Tag::getName, tagName).one();
        if (tagIsNotNull(tag)) {
            tagListCache.add(tag);
            redisService.set(TAG_LIST, tagListCache);
        }
        return tag;
    }


    /**
     * 删除tag缓存
     */
    public void deleteTagListCache() {
        redisService.del(TAG_LIST);
    }


    /**
     * 获取分类缓存
     *
     * @return
     */
    public List<Category> getCategoryListCache() {
        // 将tagLit存入redis中
        boolean b = redisService.hasKey(CATEGORY_LIST);
        if (b) {
            // 这里直接强转出错，因为存入redis经过序列化之后是JSON数据
            List<Category> list = JsonFormatUtils.objectToArr(redisService.get(CATEGORY_LIST), Category.class);
            if (list.size() != 0) {
                return list;
            }
        }
        // 从数据库中获取tagList
        List<Category> list = categoryMapper.selectList(null);
        redisService.set(CATEGORY_LIST, list);
        return list;
    }

    /**
     * 根据id从缓存中获取tagName
     */
    public Category getCategoryCacheByName(String name) {
        List<Category> categoryList = getCategoryListCache();
        for (Category category : categoryList) {
            if (categoryIsNotNull(category) && StringUtils.isNotEmpty(name) && category.getName().equals(name)) {
                return category;
            }
        }

        Category category = new LambdaQueryChainWrapper<>(categoryMapper).eq(Category::getName, name).one();
        if (categoryIsNotNull(category)) {
            categoryList.add(category);
            redisService.set(CATEGORY_LIST, categoryList);
        }
        return category;
    }


    /**
     * getCategoryCacheByCid
     */
    public Category getCategoryCacheByCid(Long cid) {
        List<Category> categoryList = getCategoryListCache();
        for (Category category : categoryList) {
            if (categoryIsNotNull(category) && StringUtils.isNotNull(cid) && cid.equals(category.getCid())) {
                return category;
            }
        }
        Category category = new LambdaQueryChainWrapper<>(categoryMapper).eq(Category::getCid, cid).one();
        if (categoryIsNotNull(category)) {
            categoryList.add(category);
            redisService.set(CATEGORY_LIST, categoryList);
        }
        return category;
    }


    /**
     * category
     */
    public void deleteCategoryListCache() {
        redisService.del(CATEGORY_LIST);
    }


    /**
     * 全部文章列表缓存
     *
     * @return
     */
    public List<Blog> getAllBlogList() {
        // 将tagLit存入redis中
        boolean b = redisService.hasKey(ALL_BLOG_LIST);
        if (b) {
            List<Blog> list = JSON.parseArray(redisService.get(ALL_BLOG_LIST).toString(), Blog.class);
            if (list.size() != 0) {
                return list;
            }
        }
        // 从数据库中获取tagList
        List<Blog> list = blogMapper.selectList(null);
        redisService.set(ALL_BLOG_LIST, list);
        return list;
    }

    /**
     * 获取分类缓存
     *
     * @return
     */
    public void deleteBlogAllListCache() {
        redisService.del(ALL_BLOG_LIST);
    }


    /**
     * 获取文章 从缓存中获取
     *
     * @param blogId
     * @return
     */
    public Blog getBlogByBlogId(Long blogId) {
        List<Blog> allBlogList = getAllBlogList();
        for (Blog blog : allBlogList) {
            if (StringUtils.isNotNull(blogId) && StringUtils.isNotNull(blog.getBlogId()) && blog.getBlogId().equals(blog)) {
                return blog;
            }
        }
        Blog blog = blogMapper.selectById(blogId);
        allBlogList.add(blog);
        redisService.set(ALL_BLOG_LIST, allBlogList);
        return blog;
    }

    /**
     * 根据用户id获取用户基本信息
     */
    public UserComment getUserCommentByUserId(Long userId) {
        String key = RedisKey.getKey(userId, RedisKey.USER_COMMENT_SUB);
        boolean hasKey = redisService.hHasKey(RedisKey.USER_COMMENT_SUB, key);
        if (hasKey) {
            UserComment userComment = JsonFormatUtils.toObject(redisService.hget(RedisKey.USER_COMMENT_SUB, key), UserComment.class);
            if (userIsNotNull(userComment)) {
                return userComment;
            }
        }
        // 用户不存在！
        User user = userMapper.selectById(userId);
        if (user == null) {
            return null;
        }
        // 返回用户同时将用户信息添加到缓存
        UserComment instance = UserComment.getInstance(user.getUserId(), user.getNickname(), user.getEmail(), user.getAvatar(), user.isSubscription());
        redisService.hset(USER_COMMENT_SUB, key, instance, 604800L);
        redisService.hset(USER_COMMENT_SUB, RedisKey.getKey(instance.getNickname(), USER_COMMENT_SUB), instance, 604800L);
        return instance;

    }


    /**
     * 根据用户名获取用户基本信息
     */
    public UserComment getUserCommentByUserName(String nickname) {
        String key = RedisKey.getKey(nickname, USER_COMMENT_SUB);
        boolean hasKey = false;
        try {
            hasKey = redisService.hHasKey(USER_COMMENT_SUB, key);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("e" + e.getMessage());
            throw new CustomException("id获取异常");
        }
        if (hasKey) {
            UserComment userComment = JsonFormatUtils.toObject(redisService.hget(USER_COMMENT_SUB, key), UserComment.class);
            if (userIsNotNull(userComment)) {
                return userComment;
            }
        }

        User user = new LambdaQueryChainWrapper<>(userMapper).eq(User::getNickname, nickname).one();
        if (user == null) {
            return null;
        }
        UserComment instance = UserComment.getInstance(user.getUserId(), user.getNickname(), user.getEmail(), user.getAvatar(), user.isSubscription());
        redisService.hset(USER_COMMENT_SUB, key, instance, 604800L);
        redisService.hset(USER_COMMENT_SUB, RedisKey.getKey(instance.getUserId(), USER_COMMENT_SUB), instance, 604800L);

        return instance;
    }


    public void cacheCommentSub(String nickname, String email, boolean subscription) {
        UserComment userComment = getUserCommentByUserName(nickname);
        // 判断缓存中是否有用户评论订阅消息
        if (userIsNotNull(userComment) && StringUtils.isNotEmpty(nickname) && StringUtils.isNotEmpty(email) && userComment.getNickname().equals(nickname) && userComment.getEmail().equals(email)) {
            // 判断有订阅通知是否改变
            if (userComment.isSubscription() != subscription) {
                userComment.setSubscription(subscription);
                redisService.hset(RedisKey.USER_COMMENT_SUB, RedisKey.getKey(userComment.getUserId(), USER_COMMENT_SUB), userComment, 604800L);
                redisService.hset(RedisKey.USER_COMMENT_SUB, RedisKey.getKey(nickname, USER_COMMENT_SUB), userComment, 604800L);
            }
        }

    }


    public UserComment cacheCheckUser(String nickname, String email, boolean subscription) {
        UserComment userComment = getUserCommentByUserName(nickname);
        // 判断缓存中是否有用户评论订阅消息
        if (userIsNotNull(userComment) && StringUtils.isNotEmpty(nickname) && StringUtils.isNotEmpty(email) && userComment.getNickname().equals(nickname) && userComment.getEmail().equals(email)) {
            // 判断有订阅通知是否改变
            if (userComment.isSubscription() != subscription) {
                userComment.setSubscription(subscription);
                redisService.hset(RedisKey.USER_COMMENT_SUB, RedisKey.getKey(userComment.getUserId(), USER_COMMENT_SUB), userComment, 604800L);
                redisService.hset(RedisKey.USER_COMMENT_SUB, RedisKey.getKey(nickname, USER_COMMENT_SUB), userComment, 604800L);
            }
            return userComment;
        }
        return null;

    }




    /*=====================================================================================*/
    public static boolean userIsNotNull(UserComment user) {
        return StringUtils.isNotNull(user)
                && StringUtils.isNotEmpty(user.getNickname())
                && StringUtils.isNotEmpty(user.getEmail())
                && StringUtils.isNotEmpty(user.getAvatar())
                && StringUtils.isNotNull(user.getUserId());
    }

    public static boolean tagIsNotNull(Tag tag) {
        return StringUtils.isNotNull(tag)
                && StringUtils.isNotEmpty(tag.getColor())
                && StringUtils.isNotEmpty(tag.getName())
                && StringUtils.isNotNull(tag.getTagId());
    }

    public static boolean categoryIsNotNull(Category category) {
        return StringUtils.isNotNull(category)
                && StringUtils.isNotEmpty(category.getColor())
                && StringUtils.isNotEmpty(category.getName())
                && StringUtils.isNotNull(category.getCid());
    }
}

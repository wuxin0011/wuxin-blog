package com.wuxin.blog;

import com.wuxin.blog.mode.UserComment;
import com.wuxin.blog.pojo.Category;
import com.wuxin.blog.pojo.Tag;
import com.wuxin.blog.redis.CacheService;
import com.wuxin.blog.redis.RedisService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: wuxin001
 * @Date: 2022/03/16/21:18
 * @Description:
 */
@SpringBootTest
public class CacheServicTest {
    //
    // @Autowired
    // private CacheService cacheService;
    //
    // @Autowired
    // private RedisService redisService;
    //
    // @Test
    // void test01() {
    //     List<Category> categoryListCache = cacheService.getCategoryListCache();
    //     categoryListCache.forEach(System.out::println);
    // }
    //
    //
    // @Test
    // void test02() {
    //     UserComment instance = UserComment.getInstance(100L, "admin", "test", "email", false);
    //     boolean user = redisService.set("user", instance);
    //     if (user) {
    //         UserComment user1 = (UserComment) redisService.get("user");
    //         System.out.println("user=====>" + user1);
    //     }
    // }
    //
    // @Test
    // void test03() {
    //     List<UserComment> list = new ArrayList<>();
    //     for (int i = 0; i < 10; i++) {
    //         UserComment userComment = new UserComment();
    //         userComment.setUserId(i * 10L);
    //         userComment.setEmail("email" + i);
    //         userComment.setNickname("username" + i);
    //         userComment.setAvatar("avatar" + i);
    //         userComment.setSubscription(false);
    //         list.add(userComment);
    //     }
    //     boolean userCommentList = redisService.set("userCommentList", list);
    //     if (userCommentList) {
    //         List<UserComment> userCommentList1 = (List<UserComment>) redisService.get("userCommentList");
    //         userCommentList1.forEach(System.out::println);
    //     }
    // }
    //
    // @Test
    // void test4() {
    //     UserComment userCommentByUserId = getUserCommentByUserId(20L);
    //     System.out.println(userCommentByUserId);
    // }
    //
    //
    // private UserComment getUserCommentByUserId(Long userId) {
    //     List<UserComment> userCommentList1 = (List<UserComment>) redisService.get("userCommentList");
    //     if (userCommentList1.size() != 0) {
    //         for (UserComment userComment : userCommentList1) {
    //             if (userComment.getUserId().equals(userId)) {
    //                 return userComment;
    //             }
    //
    //         }
    //     }
    //     return null;
    //
    // }
    //
    //
    // @Test
    // void testTagList(){
    //     // List<Tag> tagList = cacheService.getTagList();
    //     // tagList.forEach(System.out::println);
    //
    //     // Tag tagCacheById = cacheService.getTagCacheById(12L);
    //     // System.out.println(tagCacheById);
    //
    //     // Tag springBoot = cacheService.getTagCache("springboot");
    //     // System.out.println(springBoot);
    //
    //     // List<Category> categoryListCache = cacheService.getCategoryListCache();
    //     // categoryListCache.forEach(System.out::println);
    //
    //     // Category categoryCacheByCid = cacheService.getCategoryCacheByCid(1L);
    //     // System.out.println(categoryCacheByCid);
    //
    //     // Category cache = cacheService.getCategoryCacheByName("我的项目");
    //     // System.out.println(cache);
    // }

}

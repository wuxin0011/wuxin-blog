package com.wuxin.blog;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTHeader;
import cn.hutool.jwt.JWTPayload;
import cn.hutool.jwt.JWTUtil;
import com.wuxin.blog.mode.SearchBlog;
import com.wuxin.blog.redis.RedisKey;
import com.wuxin.blog.redis.RedisService;
import com.wuxin.blog.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
class DemoApplicationTests {
    //
    // @Autowired
    // private AccessLogService accessLogService;
    //
    // @Autowired
    // private LoginLogService loginLogService;
    //
    //
    // @Autowired
    // private BlogService blogService;
    //
    // @Autowired
    // private RedisService redisService;
    //
    // @Autowired
    // private CommentService commentService;
    //
    //
    // @Autowired
    // private FriendService friendService;
    //
    //
    // @Test
    // void test5() {
    //     // 测试redis
    //
    //     // tagList
    //     // List<Tag> list = (List<Tag>) redisService.hget(RedisKey.TAG_LIST, RedisKey.TAG_LIST);
    //     // System.out.println(list);
    //     // category
    //     // List<Category> categoryList = (List<Category>) redisService.hget(RedisKey.CATEGORY_LIST, RedisKey.CATEGORY_LIST);
    //     // System.out.println(categoryList);
    //     //    blog list
    //     // IPage<Blog> blogIPage = (IPage<Blog>) redisService.hget(RedisKey.BLOG_LIST, 1 + "");
    //     // for (Blog record : blogIPage.getRecords()) {
    //     //     System.out.println(record);
    //     // }
    //     // System.out.println(blogIPage);
    //     // System.out.println(friendService.findFriendMessage(FriendMessage.FRIEND_MESSAGE_ID));
    //
    //     Object o = redisService.get(RedisKey.USER_COMMENT_SUB);
    //     System.out.println(o);
    //
    // }
    //
    // @Test
    // void testUUID() {
    //     for (SearchBlog blog : blogService.searchBlog("服务器")) {
    //         System.out.println(blog);
    //     }
    //
    // }
    //
    // @Test
    // void testJwt() {
    //     DateTime now = DateTime.now();
    //     DateTime newTime = now.offsetNew(DateField.MINUTE, 10);
    //
    //     Map<String, Object> payload = new HashMap<String, Object>();
    //     Map<String, Object> headers = new HashMap<String, Object>();
    //
    //     //签发时间
    //     payload.put(JWTPayload.ISSUED_AT, now);
    //     //过期时间
    //     payload.put(JWTPayload.EXPIRES_AT, newTime);
    //     //生效时间
    //     payload.put(JWTPayload.NOT_BEFORE, now);
    //     //载荷
    //     payload.put("username", "zhangsan");
    //     payload.put("motto", "user motto");
    //     payload.put("avatar", "user avatar");
    //     payload.put("email", "user email");
    //     payload.put("introduction", " user introduction");
    //     payload.put("roles", " user roles");
    //
    //     headers.put("Authorization", payload);
    //     String key = "wuxin001";
    //     String token = JWTUtil.createToken(headers, payload, key.getBytes());
    //     System.out.println(token);
    //     validJwt(token);
    // }
    //
    // void validJwt(String token) {
    //     String key = "wuxin001";
    //     JWT jwt = JWT.create().parse(token);
    //     JWTHeader header = jwt.getHeader();
    //     System.out.println("header" + header);
    //     String algorithm = jwt.getAlgorithm();
    //     System.out.println("algorithm" + algorithm);
    //     JWTPayload payload = jwt.getPayload();
    //     System.out.println("payload" + payload);
    //
    //
    //     boolean verifyKey = jwt.setKey(key.getBytes()).verify();
    //     System.out.println(verifyKey);
    //
    //     boolean verifyTime = jwt.validate(0);
    //     System.out.println(verifyTime);
    // }
    //
    // @Test
    // void deleteRedisKeyCollection() {
    //     for (int i = 1; i < 100; i++) {
    //         boolean b = redisService.hHasKey(RedisKey.BLOG_LIST, i);
    //         System.out.println(i + "rediskey" + b);
    //         if (b) {
    //             // redisService.hdel(RedisKey.BLOG_LIST, i);
    //             System.out.println("rediskey" + b);
    //         } else {
    //             return;
    //         }
    //     }
    // }
    //
    //
    // @Test
    // void testCodeToString() {
    //     String a = "123456";
    //     String b = "" + 123456 + "";
    //     System.out.println(a.equals(b));
    // }
    //
    // @Test
    // void testCount() {
    //     Integer commentCount = commentService.commentCount();
    //     System.out.println(commentCount);
    //
    //
    //     Integer loginLog = loginLogService.selectTodayLoginLog();
    //     System.out.println(loginLog);
    //
    //     Integer accessLog = accessLogService.selectTodayAccessLog();
    //     System.out.println(accessLog);
    //
    // }
    //
    //
    // @Test
    // void accessLoginCount() {
    //     // 伪造数据
    //
    //
    //     List<Map<String, Object>> list;
    //     for (int i = 1; i < 14; i++) {
    //         Map<String, Object> map = new HashMap<>();
    //         boolean b = redisService.hasKey(RedisKey.ACCESS_LOGIN_COUNT);
    //         if (b) {
    //             list = (List<Map<String, Object>>) redisService.get(RedisKey.ACCESS_LOGIN_COUNT);
    //         } else {
    //             // 首次创建
    //             list = new ArrayList<>();
    //         }
    //         String date = "2-" + i;
    //         Object login = i + 1;
    //         Object access = i + 2;
    //         map.put("date", date);
    //         map.put("login", login);
    //         map.put("access", access);
    //         list.add(map);
    //         redisService.set(RedisKey.ACCESS_LOGIN_COUNT, list, 1209600L);
    //
    //     }
    //
    //     Object o = redisService.get(RedisKey.ACCESS_LOGIN_COUNT);
    //     System.out.println(o);
    // }


}

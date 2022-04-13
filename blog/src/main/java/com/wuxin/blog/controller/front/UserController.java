package com.wuxin.blog.controller.front;


import com.wuxin.blog.constant.GlobalConstant;
import com.wuxin.blog.mode.Blogger;
import com.wuxin.blog.redis.RedisKey;
import com.wuxin.blog.redis.RedisService;
import com.wuxin.blog.service.ChatUrlService;
import com.wuxin.blog.service.HobbyService;
import com.wuxin.blog.service.UserService;
import com.wuxin.blog.utils.JsonFormatUtils;
import com.wuxin.blog.utils.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


/***
 * @Description: 用户
 * @Author: wuxin001
 * @Date: 2021/8/23 0023
 */
@RestController
@RequestMapping("/user")
public class UserController {


    @Resource
    private UserService userService;


    @Resource
    private ChatUrlService chatUrlService;


    @Resource
    private HobbyService hobbyService;

    @Autowired
    private RedisService redisService;


    /**
     * 获取博主信息
     */
    @GetMapping("/blogger/info")
    public Result findAdminUserInfo() {
        boolean hasKey = redisService.hasKey(RedisKey.BLOGGER_INFO);
        if (hasKey) {
            Result result = JsonFormatUtils.toObject(redisService.get(RedisKey.BLOGGER_INFO), Result.class);
            if (result != null) {
                return result;
            }
        }
        Result result = Result.ok();
        result.put("chatUrl", chatUrlService.findChatUrlByUserId(GlobalConstant.ADMIN_USER_ID));
        result.put("hobby", hobbyService.selectListByUserId(GlobalConstant.ADMIN_USER_ID));
        result.put("info", Blogger.getBlogger(userService.findAdminUserInfo(GlobalConstant.ADMIN_USER_ID)));
        // 最大缓存有效期7天
        redisService.set(RedisKey.BLOGGER_INFO, result,604800L);
        return result;
    }
}

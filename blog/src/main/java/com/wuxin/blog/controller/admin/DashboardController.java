package com.wuxin.blog.controller.admin;

import com.wuxin.blog.annotation.AccessLimit;
import com.wuxin.blog.redis.RedisKey;
import com.wuxin.blog.redis.RedisService;
import com.wuxin.blog.service.*;
import com.wuxin.blog.task.ScheduleTask;
import com.wuxin.blog.utils.result.Result;
import com.wuxin.blog.utils.string.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: wuxin001
 * @Date: 2022/01/09/15:52
 * @Description:
 */

@RequestMapping("/dashboard")
@RestController
public class DashboardController {


    @Autowired
    private CategoryService categoryService;


    @Autowired
    private TagService tagService;

    @Autowired
    private BlogService blogService;

    @Autowired
    private CommentService commentService;
    @Autowired
    private LoginLogService loginLogService;

    @Autowired
    private AccessLogService accessLogService;

    @Autowired
    private ScheduleTask scheduleTask;

    private static Long loginExpire = 600L;


    @Autowired
    private RedisService redisService;

    /**
     * 统计数据展示
     */
    @AccessLimit(seconds = 60, limitCount = 10, msg = "操作频率过高！一分钟之后再试！")
    @GetMapping("/count")
    public Result count() {
        boolean hasKey = redisService.hasKey( RedisKey.SYSTEM_COUNT);
        if(hasKey){
            Result o = (Result) redisService.get( RedisKey.SYSTEM_COUNT);
            if(StringUtils.isNotNull(o)){
                return o;
            }
        }
        Result result = Result.ok();
        result.put("blog", blogService.blogCount());
        result.put("comment", commentService.commentCount());
        result.put("access", accessLogService.selectTodayAccessLog());
        result.put("login", loginLogService.selectTodayLoginLog());
        result.put("category", categoryService.blogCountByCategoryName());
        result.put("tag", tagService.blogCountByTagName());
        result.put("accessLogin", scheduleTask.getAccessLoginCount());
        redisService.set(RedisKey.SYSTEM_COUNT,result,1000L);
        return result;

    }
}

package com.wuxin.blog.controller.front;

import com.wuxin.blog.annotation.AccessLimit;
import com.wuxin.blog.annotation.VisitLogger;
import com.wuxin.blog.pojo.Moment;
import com.wuxin.blog.service.MomentService;
import com.wuxin.blog.utils.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: wuxin001
 * @Date: 2021/10/01/11:24
 * @Description:
 */
@RequestMapping("/moment")
@RestController
public class MomentController {

    @Autowired
    private MomentService momentService;


    @AccessLimit(seconds = 60, limitCount = 10, msg = "刷新频率过于频繁！一分钟之后再试！")
    @VisitLogger(value = "获取动态信息",name = "动态页")
    @GetMapping("/list/{current}/{limit}")
    public Result getMomentList(@PathVariable("current") int current,
                             @PathVariable("limit") int limit){
        return Result.ok(momentService.selectListByPage(current,limit));
    }


    @AccessLimit(seconds = 60, limitCount = 20, msg = "点赞操作过于频繁！一分钟之后再试！")
    @VisitLogger(value = "点赞",name = "动态页")
    @PostMapping("/like")
    public Result likeMoment(@RequestBody Moment moment){
        moment.setLikes(moment.getLikes()+1);
        momentService.update(moment);
        return Result.ok("操作成功");
    }
}

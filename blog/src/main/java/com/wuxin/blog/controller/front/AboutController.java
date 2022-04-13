package com.wuxin.blog.controller.front;

import com.wuxin.blog.annotation.AccessLimit;
import com.wuxin.blog.annotation.VisitLogger;
import com.wuxin.blog.pojo.About;
import com.wuxin.blog.service.AboutService;
import com.wuxin.blog.utils.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Author: wuxin001
 * @Date: 2021/10/01/11:24
 * @Description: 我的关于页面内容
 */
@RequestMapping("/about")
@RestController
public class AboutController {

    @Autowired
    private AboutService aboutService;


    /**
     * 显示关于我的内容
     */
    @AccessLimit(seconds = 120, limitCount = 10, msg = "操作频率过高！稍后再试！")
    @VisitLogger(value = "访问了关于我的内容",name = "关于页")
    @GetMapping("/detail")
    public Result findAbout() {
        return Result.ok(aboutService.queryById(About.ABOUT_ID));
    }


}

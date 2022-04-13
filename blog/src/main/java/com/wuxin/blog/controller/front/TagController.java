package com.wuxin.blog.controller.front;

import com.wuxin.blog.annotation.AccessLimit;
import com.wuxin.blog.annotation.VisitLogger;
import com.wuxin.blog.mode.PageVo;
import com.wuxin.blog.service.TagService;
import com.wuxin.blog.utils.result.Result;
import com.wuxin.blog.utils.string.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Author: wuxin001
 * @Date: 2021/08/27/15:51
 * @Description:
 */

@Slf4j
@RestController
@RequestMapping("/tag")
public class TagController {

    @Autowired
    private TagService tagService;


    @GetMapping("/list")
    public Result findTag() {
        return Result.ok(tagService.list());
    }


    @AccessLimit(seconds = 120, limitCount = 10, msg = "操作频率过高！两分钟之后再试！")
    @VisitLogger(value = "根据文章标签名获取文章列表", name = "标签页")
    @GetMapping("/blog/list")
    public Result findBlogByTagName( @RequestParam(value = "current", defaultValue = "1") Integer current,
                                     @RequestParam(value = "limit", defaultValue = "5") Integer limit,
                                     @RequestParam("name") String name) {
        if (StringUtils.isEmpty(name)) {
            return Result.error("请求失败,名称不能为空！");
        }
        return Result.ok(tagService.findBlogByTagName(current, limit, name));
    }


}

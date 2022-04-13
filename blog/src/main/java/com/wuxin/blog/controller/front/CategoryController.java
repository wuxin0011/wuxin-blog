package com.wuxin.blog.controller.front;

import com.wuxin.blog.annotation.AccessLimit;
import com.wuxin.blog.annotation.OperationLogger;
import com.wuxin.blog.annotation.VisitLogger;
import com.wuxin.blog.mode.PageVo;
import com.wuxin.blog.service.CategoryService;
import com.wuxin.blog.utils.result.Result;
import com.wuxin.blog.utils.string.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Author: wuxin001
 * @Date: 2021/08/27/15:36
 * @Description: 分类
 */
@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Resource
    private CategoryService categoryService;


    /**
     * 获取categoryList
     */
    @GetMapping("/list")
    public Result cateList() {
        return Result.ok(categoryService.list());
    }


    /**
     * 根据categoryName显示blog信息
     */
    @AccessLimit(seconds = 120, limitCount = 10, msg = "操作频率过高！两分钟之后再试！")
    @VisitLogger(value = "获取分类下的文章列表", name = "分类页")
    @GetMapping("/blog/list")
    public Result findBlogByCategoryName(
            @RequestParam(value = "current", defaultValue = "1") Integer current,
            @RequestParam(value = "limit", defaultValue = "5") Integer limit,
            @RequestParam("name") String name) {
        if (StringUtils.isEmpty(name)) {
            return Result.error("请求失败,名称不能为空！");
        }
        return Result.ok(categoryService.findBlogByCategoryName(current, limit, name));
    }


    @GetMapping("/count")
    public Result getCount(){
        return  Result.ok(categoryService.blogCountByCategoryName());
    }


}

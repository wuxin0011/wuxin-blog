package com.wuxin.blog.controller.admin;

import com.wuxin.blog.annotation.AccessLimit;
import com.wuxin.blog.annotation.OperationLogger;
import com.wuxin.blog.mode.PageVo;
import com.wuxin.blog.pojo.Tag;
import com.wuxin.blog.service.BlogTagService;
import com.wuxin.blog.service.TagService;
import com.wuxin.blog.utils.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: wuxin001
 * @Date: 2021/09/01/15:30
 * @Description: 标签管理
 */
@Slf4j
@RestController
@RequestMapping("/admin/tag")
public class AdminTagController {

    @Autowired
    private TagService tagService;

    @Autowired
    private BlogTagService blogTagService;


    /**
     * 标签搜索
     */
    @AccessLimit(seconds = 60, limitCount = 10, msg = "操作频率过高！一分钟之后再试！")
    @OperationLogger("查看文章标签")
    @PostMapping("/list")
    public Result selectTag(@RequestBody PageVo pageVo) {
        return Result.ok(tagService.selectListByPage(pageVo.getCurrent(), pageVo.getLimit(), pageVo.getKeywords()));
    }


    /**
     * 添加标签
     */
    @AccessLimit(seconds = 60, limitCount = 5, msg = "操作频率过高！一分钟之后再试！")
    @OperationLogger("添加文章标签")
    @RequiresRoles("root")
    @PostMapping("/add")
    public Result addTag(@RequestBody Tag tag) {
        System.err.println("tag1=>"+tag);
        Tag tagByName = tagService.findTagByName(tag.getName());
        if (tagByName != null) {
            return Result.error("该标签名已经存在");
        }
        tagService.add(tag);
        System.err.println("tag2=>"+tag);
        return Result.ok("添加成功").put("tagId",tag.getTagId());
    }

    /**
     * 修改标签名
     */
    @OperationLogger("修改文章标签")
    @RequiresRoles("root")
    @PostMapping("/update")
    public Result updateTag(@RequestBody Tag tag) {
        Tag tagByName = tagService.findTagByName(tag.getName());
        if (tagByName != null) {
            return Result.error("该标签名已经存在");
        }
        tagService.update(tag);
        return Result.ok("修改成功！");
    }


    /**
     * 修改标签颜色
     */
    @OperationLogger("修改文章标签颜色")
    @RequiresRoles("root")
    @PostMapping("/update/color")
    public Result updateTagColor(@RequestBody Tag tag) {
        tagService.update(tag);
        return Result.ok("修改成功！");
    }

    /**
     * 删除标签
     */
    @OperationLogger("删除文章标签")
    @RequiresRoles("root")
    @DeleteMapping("/del")
    public Result delTag(@RequestParam("tagId") Long tagId) {
        log.info("tagId:{}",tagId);
        tagService.delete(tagId);
        blogTagService.deleteByTagId(tagId);
        return Result.ok("删除成功！");
    }


}

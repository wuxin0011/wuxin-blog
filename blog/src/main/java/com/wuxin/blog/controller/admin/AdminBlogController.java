package com.wuxin.blog.controller.admin;

import com.wuxin.blog.annotation.AccessLimit;
import com.wuxin.blog.annotation.OperationLogger;
import com.wuxin.blog.mode.PageVo;
import com.wuxin.blog.pojo.Blog;
import com.wuxin.blog.pojo.User;
import com.wuxin.blog.service.BlogService;
import com.wuxin.blog.service.BlogTagService;
import com.wuxin.blog.service.CommentService;
import com.wuxin.blog.service.TagService;
import com.wuxin.blog.utils.result.Result;
import com.wuxin.blog.utils.security.MySecurityUtils;
import com.wuxin.blog.utils.string.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: wuxin001
 * @Date: 2021/09/01/15:29
 * @Description: 博客管理
 */
@Slf4j
@RestController
@RequestMapping("/admin/blog")
public class AdminBlogController {


    @Autowired
    private BlogService blogService;

    @Autowired
    private BlogTagService blogTagService;

    @Autowired
    private TagService tagService;

    @Autowired
    private CommentService commentService;


    @OperationLogger("获取文章列表")
    @PostMapping("/list")
    public Result findBlogPage(@RequestBody PageVo pageVo) {

        return Result.ok(blogService.findBlogPage
                (
                        pageVo.getCurrent(),
                        pageVo.getLimit(),
                        pageVo.getKeywords(),
                        pageVo.getStart(),
                        pageVo.getEnd(),
                        pageVo.getId()
                )
        );
    }


    /**
     * 添加blog
     */
    @AccessLimit(seconds = 60, limitCount = 1, msg = "操作频率过高！一分钟之后再试！")
    @RequiresRoles(value = {"user", "root", "admin"}, logical = Logical.OR)
    @OperationLogger("添加文章")
    @PostMapping("/add")
    public Result addBlog(@RequestBody Blog blog) {
        User loginUser = MySecurityUtils.getUser();
        if (StringUtils.isNull(loginUser.getUserId())) {
            return Result.error("添加失败，你还未登录！");
        }
        if (StringUtils.isEmpty(blog.getDescription())) {
            return Result.error("添加失败，摘要不能为空！");
        }
        if (StringUtils.isEmpty(blog.getTitle())) {
            return Result.error("添加失败，标题不能空！");
        }
        if (StringUtils.isEmpty(blog.getContent())) {
            return Result.error("添加失败，内容不能为空！");
        }
        if (blog.getCid() == null) {
            return Result.error("添加失败，获取不到分类！");
        }
        blog.setUserId(loginUser.getUserId());
        // 将得到的blogID返回给blogTag
        Long blogId = blogService.addBlog(blog);
        tagService.addBlogTag(blogId, blog.getTagIds());
        return Result.ok("文章添加成功！");

    }


    /**
     * 修改blog
     *
     * @param blog blogDTO
     * @return success
     */
    @AccessLimit(seconds = 60, limitCount = 3, msg = "操作频率过高！一分钟之后再试！")
    @OperationLogger("修改文章")
    @RequiresRoles(value = {"user", "root", "admin"}, logical = Logical.OR)
    @PutMapping("/update")
    public Result blogUpdate(@RequestBody Blog blog) {
        log.info("blog:{}", blog);
        User loginUser = MySecurityUtils.getUser();
        // 判断用户是否为root用户，非root用户只能修改自己信息
        if (MySecurityUtils.isNotPermission(loginUser.getRoleId(), loginUser.getUserId(), blog.getUserId())) {
            return Result.error("没有权限执行该操作");
        }
        // 如果开启了私密，需要开启密码
        if (blog.isSecrecy() && StringUtils.isEmpty(blog.getPassword())) {
            return Result.error("修改失败！私密文章需要设置密码！");
        }
        // 修改文章内容
        blogService.updateBlog(blog);
        // 修改文章标签
        // blogTagService.updateBlogTag(blog.getTagIds(),blog.getBlogId());
        return Result.ok("文章修改成功！");

    }


    /**
     * 删除blog
     *
     * @param blogId blogId
     * @return success
     */
    @OperationLogger("删除文章")
    @RequiresRoles(value = {"user", "root", "admin"}, logical = Logical.OR)
    @DeleteMapping("/del")
    public Result delBlog(@RequestParam("blogId") Long blogId) {
        User loginUser = MySecurityUtils.getUser();
        Long userIdByBlogId = blogService.getUserIdByBlogId(blogId);
        // 判断用户是否为root用户，非root用户只能修改自己信息
        if (MySecurityUtils.isNotPermission(loginUser.getRoleId(), loginUser.getUserId(), userIdByBlogId)) {
            return Result.error("没有权限执行该操作！");
        }
        // 删除文章
        blogService.delBlog(blogId);
        // 删除文章标签
        blogTagService.deleteByBlogId(blogId);
        // 删除评论
        commentService.delCommentByBlogId(blogId);
        // 删除回复
        commentService.delCommentReplyByBlogId(blogId);
        return Result.ok("文章删除成功！");

    }


    /**
     * 获取全部文章列表
     */
    @AccessLimit(seconds = 60, limitCount = 10, msg = "操作频率过高！一分钟之后再试！")
    @GetMapping("/all/list")
    public Result getAllBlogList() {
        return Result.ok(blogService.getAllBlogList());
    }


}

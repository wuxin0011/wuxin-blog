package com.wuxin.blog.controller.front;

import com.wuxin.blog.annotation.AccessLimit;
import com.wuxin.blog.annotation.OperationLogger;
import com.wuxin.blog.constant.GlobalConstant;
import com.wuxin.blog.mode.UserComment;
import com.wuxin.blog.pojo.Comment;
import com.wuxin.blog.pojo.CommentReply;
import com.wuxin.blog.service.CommentService;
import com.wuxin.blog.service.MailService;
import com.wuxin.blog.service.UserService;
import com.wuxin.blog.utils.result.Result;
import com.wuxin.blog.utils.string.StringUtils;
import com.wuxin.blog.utils.validate.ValidUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Author: wuxin001
 * @Date: 2022/02/23/22:48
 * @Description: 测试
 */
@RestController
public class TestController {


    @Resource
    private CommentService commentService;

    @Resource
    private UserService userService;


    @Autowired
    private MailService mailService;


    @GetMapping("/list")
    public Result queryCommentList(@RequestParam(value = "current", defaultValue = "1") Integer current,
                                   @RequestParam(value = "limit", defaultValue = "5") Integer limit,
                                   @RequestParam(value = "blogId", defaultValue = "", required = false) Long blogId,
                                   @RequestParam(value = "type", defaultValue = "") Integer type) {


        if (!(type.equals(Comment.BLOG_COMMENT) || type.equals(Comment.ABOUT_COMMENT) || type.equals(Comment.FRIEND_COMMENT))) {
            return Result.error("获取不到评论类型");
        }
        if ((type.equals(Comment.BLOG_COMMENT) && StringUtils.isNull(blogId))) {
            return Result.error("评论获取失败！");
        }
        return Result.ok()
                .put("commentList", commentService.queryCommentList(current, limit, blogId, type))
                .put("commentTotal", commentService.onlyCommentCount(blogId, type))
                .put("commentCount", commentService.findCommentCount(blogId, type));
    }

    @AccessLimit(msg = "30s仅内可发布三条评论", limitCount = 3)
    @OperationLogger(value = "添加回复")
    @PostMapping("/add")
    public Result addComment(@RequestBody Comment comment) {
        if (!((comment.getType().equals(Comment.BLOG_COMMENT))
                || (comment.getType().equals(Comment.ABOUT_COMMENT))
                || (comment.getType().equals(Comment.FRIEND_COMMENT))
        )) {
            return Result.error("添加失败，获取不到评论类型！");
        }
        if (comment.getType().equals(Comment.BLOG_COMMENT) && StringUtils.isNull(comment.getBlogId())) {
            return Result.error("添加失败,获取不到文章id！");
        }
        if (StringUtils.isEmpty(comment.getContent())) {
            return Result.error("添加添加失败！内容不能为空！");
        }
        if (!ValidUtil.validUsername(comment.getUsername())) {
            return Result.error("发布失败，用户名不合法！");
        }
        if (!ValidUtil.validEmail(comment.getEmail())) {
            return Result.error("发布失败，邮箱格式错误！！");
        }
        // 缓存是是否有该用户信息
        UserComment user = commentService.cacheCheckUser(comment.getUsername(), comment.getEmail(), comment.isSubscription());
        Long userId;
        if (StringUtils.isNull(user) || StringUtils.isNull(user.getUserId())) {
            // 从数据库中获取用户信息
            userId = userService.getCommentUserId(comment.getUsername(), comment.getEmail(), comment.isSubscription());
            if (StringUtils.isNull(userId)) {
                return Result.error("发布失败,请检查用户名或者邮箱是否输入正确!可能已被使用了哦!");
            }
        } else {
            // 从缓存中获取用户信息
            userId = user.getUserId();
        }
        if (StringUtils.isNotNull(userId)) {
            comment.setCommentUserId(userId);
            try {
                if (!comment.getCommentUserId().equals(GlobalConstant.ADMIN_USER_ID)) {
                    // 评论是否推送给我
                    mailService.pubMessage(comment.getUsername(), comment.getContent(), comment.getType(), comment.getBlogId());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            commentService.addComment(comment);
            return Result.ok("评论发布成功！");

        }
        return Result.error("发布失败，获取不到用户信息！");
    }


    /**
     * 博客评论回复
     */
    @AccessLimit(msg = "30s仅内可发布三条评论", limitCount = 3)
    @OperationLogger(value = "添加回复")
    @PostMapping("/reply/add")
    public Result addReply(@RequestBody CommentReply commentReply) {
        if (!((commentReply.getType().equals(Comment.BLOG_COMMENT))
                || (commentReply.getType().equals(Comment.ABOUT_COMMENT))
                || (commentReply.getType().equals(Comment.FRIEND_COMMENT))
        )) {
            return Result.error("添加添加失败！获取不到评论类型");
        }
        if (commentReply.getType().equals(Comment.BLOG_COMMENT) && StringUtils.isNull(commentReply.getBlogId())) {
            return Result.error("添加失败,获取不到文章id！");
        }
        if (StringUtils.isEmpty(commentReply.getReplyContent())) {
            return Result.error("添加添加失败！内容不能为空！");
        }
        if (!ValidUtil.validUsername(commentReply.getReplyUsername())) {
            return Result.error("添加添加失败！用户名长度不合法！");
        }
        if (!ValidUtil.validEmail(commentReply.getReplyEmail())) {
            return Result.error("添加添加失败！邮箱格式不正确！");
        }
        // 从缓存中获取评论用户信息
        UserComment user = commentService.cacheCheckUser(commentReply.getReplyUsername(), commentReply.getReplyEmail(), commentReply.isSubscription());
        Long userId;
        if (StringUtils.isNull(user) || StringUtils.isNull(user.getUserId())) {
            // 从数据库中获取用户信息 同时将用户信息缓存
            userId = userService.getCommentUserId(commentReply.getReplyUsername(), commentReply.getReplyEmail(), commentReply.isSubscription());
            if (StringUtils.isNull(userId)) {
                return Result.error("发布失败,请检查用户名或者邮箱是否输入正确!可能已被使用了哦!");
            }
        } else {
            // 从缓存中获取用户信息
            userId = user.getUserId();
        }
        if (StringUtils.isNotNull(userId)) {
            commentReply.setReplyUserId(userId);
            // 回复用户
            Long rid = commentReply.getReplyId();
            // 被回复用户
            Long cid = commentReply.getCommentUserId();
            // 博主
            Long aid = GlobalConstant.ADMIN_USER_ID;
            // 如果回复人是博主，并且回复评论也是自己的 邮件也不用发送了 直接return
            if (aid.equals(cid) && aid.equals(rid)) {
                commentService.addReply(commentReply);
                return Result.ok("评论发布成功！");
            }
            // 如果回复博主评论 并且这条评论不是博主评论 发送邮件之后直接return
            if (!aid.equals(rid) && aid.equals(cid)) {
                commentService.addReply(commentReply);
                mailService.pubMessage(commentReply.getReplyUsername(), commentReply.getReplyContent(), commentReply.getType(), commentReply.getBlogId());
                return Result.ok("评论发布成功！");
            }
            // 如果被回复人不是博主 并且 b评论人也不是博主
            if (!aid.equals(rid)) {
                mailService.pubMessage(commentReply.getReplyUsername(), commentReply.getReplyContent(), commentReply.getType(), commentReply.getBlogId());
            }
            // 回复用户和被回复用户不一致，需要判断是否发送
            if (!rid.equals(cid)) {
                UserComment userComment = commentService.cacheCommentSub(commentReply.getReplyUsername(), commentReply.getReplyEmail(), commentReply.isSubscription(), cid);
                // 如果被回复用户订阅了评论 将评论内容发送给ta
                if (StringUtils.isNotNull(userComment) && userComment.isSubscription()) {
                    // 获取被回复的内容 同时将内容发布给订阅用户
                    commentReply.setCommentUsername(userComment.getNickname());
                    mailService.pubMessage(commentReply, userComment.getEmail());
                }
            }
            commentService.addReply(commentReply);
            return Result.ok("评论发布成功！");
        }
        return Result.error("发布失败，获取不到用户信息");
    }
}

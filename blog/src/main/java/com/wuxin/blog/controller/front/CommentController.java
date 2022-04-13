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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Author: wuxin001
 * @Date: 2022/01/02/0:00
 * @Description:
 */
@Slf4j
@RestController
@RequestMapping("/comment")
public class CommentController {


    @Resource
    private CommentService commentService;

    @Resource
    private UserService userService;


    @Autowired
    private MailService mailService;


    @GetMapping("/list")
    public Result queryCommentList(@RequestParam(value = "current", defaultValue = "1") Integer current,
                                   @RequestParam(value = "limit", defaultValue = "5") Integer limit,
                                   @RequestParam(value = "blogId", defaultValue = "") Long blogId,
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
        if (user == null || user.getUserId() == null) {
            // 从数据库中获取用户信息
            userId = userService.getCommentUserId(comment.getUsername(), comment.getEmail(), comment.isSubscription());
            if (StringUtils.isNull(userId)) {
                return Result.error("发布失败,请检查用户名或者邮箱是否输入正确!可能已被使用了哦!");
            }
        } else {
            // 从缓存中获取用户信息
            userId = user.getUserId();
        }
        if (userId != null) {
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

        log.info("comment reply:{}",commentReply);
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
        if (user == null || user.getUserId() == null) {
            // 从数据库中获取用户信息 同时将用户信息缓存
            userId = userService.getCommentUserId(commentReply.getReplyUsername(), commentReply.getReplyEmail(), commentReply.isSubscription());
            if (StringUtils.isNull(userId)) {
                return Result.error("发布失败,请检查用户名或者邮箱是否输入正确!可能已被使用了哦!");
            }
        } else {
            // 从缓存中获取用户信息
            userId = user.getUserId();
        }
        if (userId != null) {
            commentReply.setReplyUserId(userId);
            // 是否将评论回复推送给我 文章评论回复 除了自己的回复其他人回复均需发送给我,如果是回复我，则不发送这种形式，以回复方式发送
            if (!commentReply.getReplyUserId().equals(GlobalConstant.ADMIN_USER_ID)
                    && !commentReply.getCommentUserId().equals(GlobalConstant.ADMIN_USER_ID)
            ) {
                try {
                    mailService.pubMessage(commentReply.getReplyUsername(), commentReply.getReplyContent(), commentReply.getType(), commentReply.getBlogId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // commentReply.getCommentUserId() 被回复人
            // commentReply.getReplyUserId() 本次回复该评论的用户
            // 如果回复是管理员不需要将内容发送，并且自己@自己内容也不需要发布
            if (!commentReply.getCommentUserId().equals(commentReply.getReplyUserId())) {

                UserComment userComment = commentService.cacheCommentSub(commentReply.getReplyUsername(), commentReply.getReplyEmail(), commentReply.isSubscription(), commentReply.getCommentUserId());
                // 如果被回复用户订阅了评论 将评论内容发送给ta
                if (userComment != null && userComment.isSubscription()) {
                    // 获取被回复的内容 同时将内容发布给订阅用户
                    commentReply.setCommentUsername(userComment.getNickname());
                    try {
                        // 判断replyId是否为null如果为null说明是回复评论，如果不是null说明回复 另外一个回复评论
                        mailService.pubMessage(commentReply,userComment.getEmail());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            commentService.addReply(commentReply);
            return Result.ok("评论发布成功！");
        }
        return Result.error("发布失败，获取不到用户信息");
    }


}

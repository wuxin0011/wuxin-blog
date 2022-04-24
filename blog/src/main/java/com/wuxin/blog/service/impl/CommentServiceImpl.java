package com.wuxin.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuxin.blog.mapper.BlogMapper;
import com.wuxin.blog.mapper.UserMapper;
import com.wuxin.blog.mapper.CommentMapper;
import com.wuxin.blog.mapper.CommentReplyMapper;
import com.wuxin.blog.mode.UserComment;
import com.wuxin.blog.pojo.Blog;
import com.wuxin.blog.pojo.Comment;
import com.wuxin.blog.pojo.CommentReply;
import com.wuxin.blog.pojo.User;
import com.wuxin.blog.redis.CacheService;
import com.wuxin.blog.constant.RedisKey;
import com.wuxin.blog.redis.RedisService;
import com.wuxin.blog.service.CommentService;
import com.wuxin.blog.utils.ThrowUtils;
import com.wuxin.blog.utils.mapper.MapperUtils;
import com.wuxin.blog.utils.string.StringUtils;
import com.wuxin.blog.utils.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author: wuxin001
 * @Date: 2021/10/01/11:08
 * @Description:
 */
@Service
@Transactional(rollbackFor = {Exception.class})
public class CommentServiceImpl implements CommentService {

    private final static String COMMENT_LIST = RedisKey.COMMENT_LIST;

    private final static String USER_COMMENT_SUB = RedisKey.USER_COMMENT_SUB;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private CommentReplyMapper blogCommentReplyMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BlogMapper blogMapper;


    @Autowired
    private RedisService redisService;

    @Autowired
    private CacheService cacheService;

    @Value("${comment.expireTime}")
    private Long expire;


    @Override
    public void addComment(Comment comment) {
        comment.setStatus(true);
        comment.setTop(0);
        ThrowUtils.ops(commentMapper.insert(comment), "评论添加失败");
        deleteCommentCache(comment.getBlogId(), comment.getType());
    }

    @Override
    public void delCommentAll() {
        commentMapper.delete(null);
        delReplyAll();
    }

    @Override
    public void delReplyAll() {
        blogCommentReplyMapper.delete(null);
    }

    @Override
    public Comment findCommentByCommentId(Long commentId) {
        return commentMapper.selectById(commentId);
    }

    @Override
    public void delComment(Long commentId) {
        Comment comment = getComment(commentId);
        deleteCommentCache(comment.getBlogId(), comment.getType());
        commentMapper.deleteById(commentId);
    }

    @Override
    public void delCommentByUserId(Long userId) {
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getCommentUserId, userId);
        // 删除缓存
        List<Comment> comments = commentMapper.selectList(queryWrapper);
        comments.forEach(comment -> {
            deleteCommentCache(comment.getBlogId(), comment.getType());
        });
        commentMapper.delete(queryWrapper);
    }

    @Override
    public void delCommentByBlogId(Long blogId) {
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getBlogId, blogId);
        commentMapper.delete(queryWrapper);
    }


    @Override
    public void updateComment(Comment comment) {
        ThrowUtils.ops(commentMapper.updateById(comment), "修改失败！评论不存在！");
        deleteCommentCache(comment.getBlogId(), comment.getType());
    }


    @Override
    public Long addReply(CommentReply reply) {
        reply.setStatus(true);
        reply.setTop(0);
        ThrowUtils.ops(blogCommentReplyMapper.insert(reply), "回复添加失败！");
        deleteCommentCache(reply.getBlogId(), reply.getType());
        return reply.getReplyId();
    }

    @Override
    public void delReply(Long replyId) {
        CommentReply reply = getCommentReply(replyId);
        deleteCommentCache(reply.getBlogId(), reply.getType());
        blogCommentReplyMapper.deleteById(replyId);
    }


    @Override
    public void delReplyByUserId(Long userId) {
        LambdaQueryWrapper<CommentReply> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CommentReply::getReplyUserId, userId);
        // 删除缓存
        List<CommentReply> commentReplies = blogCommentReplyMapper.selectList(queryWrapper);
        commentReplies.forEach(reply -> {
            deleteCommentCache(reply.getBlogId(), reply.getType());
        });
        blogCommentReplyMapper.delete(queryWrapper);
    }

    @Override
    public void updateComment(CommentReply reply) {
        blogCommentReplyMapper.updateById(reply);
        deleteCommentCache(reply.getBlogId(), reply.getType());
    }

    @Override
    public List<Comment> queryCommentList(Integer current, Integer limit, Long blogId, Integer type) {
        // 获取评论列表
        List<Comment> commentList = new LambdaQueryChainWrapper<>(commentMapper)
                .orderByDesc(Comment::getTop)
                .eq(StringUtils.isNotNull(blogId), Comment::getBlogId, blogId)
                .eq(StringUtils.isNotNull(type), Comment::getType, type)
                .orderByDesc(Comment::getCreateTime)
                .page(new Page<>(current, limit)).getRecords();
        // 遍历方式获取回复内容
        commentList.forEach(comment -> {
            // UserComment cacheInfo = getUserCacheInfo(comment.getCommentUserId());
            User user = userMapper.selectById(comment.getCommentUserId());
            if (user != null) {
                comment.setUsername(user.getNickname());
                comment.setAvatar(user.getAvatar());
                getReplyList(comment);
            }
        });
        System.out.println("================从数据库中获取评论================");
        return commentList;
    }

    @Override
    public void delCommentReplyByBlogId(Long blogId) {
        LambdaQueryWrapper<CommentReply> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CommentReply::getBlogId, blogId);
        blogCommentReplyMapper.delete(queryWrapper);
    }


    @Override
    public void delCommentReplyByCommentId(Long commentId) {
        Comment comment = getComment(commentId);
        deleteCommentCache(comment.getBlogId(), comment.getType());
        LambdaQueryWrapper<CommentReply> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CommentReply::getCommentId, commentId);
        ThrowUtils.ops(blogCommentReplyMapper.delete(queryWrapper), "删除失败,该回复不存在");
    }


    @Override
    public Integer findCommentCount(Long blogId, Integer type) {
        Integer commentCount = onlyCommentCount(blogId, type);
        LambdaQueryWrapper<CommentReply> replyLambdaQueryWrapper = new LambdaQueryWrapper<>();
        replyLambdaQueryWrapper.eq(StringUtils.isNotNull(blogId), CommentReply::getBlogId, blogId);
        replyLambdaQueryWrapper.eq(CommentReply::getType, type);
        replyLambdaQueryWrapper.eq(CommentReply::isStatus, true);
        Integer replyCount = blogCommentReplyMapper.selectCount(replyLambdaQueryWrapper);
        return commentCount + replyCount;
    }

    @Override
    public IPage<Comment> findBlogCommentByPage(Integer current, Integer limit, Integer type, String keywords, Long blogId, String start, String end) {
        LambdaQueryChainWrapper<Comment> queryChainWrapper = new LambdaQueryChainWrapper<>(commentMapper);
        queryChainWrapper.orderByDesc(Comment::getTop).orderByDesc(Comment::getCreateTime);
        Page<Comment> commentPage = new Page<>(current, limit);
        Page<Comment> page = queryChainWrapper
                .like(StringUtils.isNotEmpty(keywords), Comment::getContent, keywords)
                .eq(StringUtils.isNotNull(type), Comment::getType, type)
                .eq(StringUtils.isNotNull(blogId), Comment::getBlogId, blogId)
                .le(StringUtils.isNotEmpty(end), Comment::getCreateTime, end)
                .ge(StringUtils.isNotEmpty(start), Comment::getCreateTime, start)
                .page(commentPage);
        page.getRecords().forEach(comment -> {
            if (comment.getType().equals(Comment.BLOG_COMMENT)) {
                String title = MapperUtils.lambdaQueryWrapper(blogMapper).select(Blog::getBlogId, Blog::getTitle).eq(Blog::getBlogId, comment.getBlogId()).one().getTitle();
                comment.setTitle(title);
            }
            if (comment.getType().equals(Comment.ABOUT_COMMENT)) {
                comment.setTitle("关于我");
            }
            if (comment.getType().equals(Comment.FRIEND_COMMENT)) {
                comment.setTitle("友情链接");
            }
            UserComment user = getUserCacheInfo(comment.getCommentUserId());
            // User user = userMapper.selectById(comment.getCommentId());

            comment.setUsername(user.getNickname());
            comment.setAvatar(user.getAvatar());
            getReplyList(comment);

        });
        return page;

    }

    @Override
    public IPage<CommentReply> findBlogCommentReplyPage(Integer current, Integer limit, String keywords) {
        LambdaQueryChainWrapper<CommentReply> chainWrapper = new LambdaQueryChainWrapper<CommentReply>(blogCommentReplyMapper);
        Page<CommentReply> commentPage = new Page<>(current, limit);
        Page<CommentReply> page = new LambdaQueryChainWrapper<CommentReply>(blogCommentReplyMapper).like(!keywords.isEmpty(), CommentReply::getReplyContent, keywords).page(commentPage);
        page.getRecords().forEach(commentReply -> {
            // 获取评论用户名
            UserComment cacheInfo = getUserCacheInfo(commentReply.getReplyUserId());
            commentReply.setReplyUsername(cacheInfo.getNickname());
        });
        return page;
    }

    @Override
    public List<Comment> allBlogComment(Long blogId) {
        LambdaQueryChainWrapper<Comment> queryChainWrapper = new LambdaQueryChainWrapper<>(commentMapper);
        queryChainWrapper.orderByDesc(Comment::getCreateTime);
        List<Comment> blogCommentList = queryChainWrapper.eq(Comment::getBlogId, blogId).list();
        blogCommentList.forEach(comment -> {
            // 获取评论用户名
            UserComment commentUser = cacheService.getUserCommentByUserId(comment.getCommentUserId());
            if (commentUser != null) {
                comment.setUsername(commentUser.getNickname());
                comment.setAvatar(commentUser.getAvatar());
            }
            getReplyList(comment);

        });
        return blogCommentList;
    }


    @Override
    public Integer commentCount() {
        // 当前时间
        String startTime = DateUtils.todayStartTime();
        String localTime = DateUtils.localTime();
        Integer commentCount = new LambdaQueryChainWrapper<>(commentMapper).between(Comment::getCreateTime, startTime, localTime).count();
        Integer replyCount = new LambdaQueryChainWrapper<>(blogCommentReplyMapper).between(CommentReply::getCreateTime, startTime, localTime).count();
        return commentCount + replyCount;
    }

    public void getReplyList(Comment blogComment) {
        List<CommentReply> replyList = new LambdaQueryChainWrapper<>(blogCommentReplyMapper)
                .orderByDesc(CommentReply::getCreateTime)
                .orderByDesc(CommentReply::getTop)
                .eq(StringUtils.isNotNull(blogComment.getBlogId()), CommentReply::getBlogId, blogComment.getBlogId())
                .eq(StringUtils.isNotNull(blogComment.getType()), CommentReply::getType, blogComment.getType())
                .eq(CommentReply::getCommentId, blogComment.getCommentId())
                .list();

        // 判断是否含有评论信息
        if (replyList.size() != 0) {
            replyList.forEach(commentReply -> {
                // 获取回复人基本信息 用户名 头像等
                UserComment user = getUserCacheInfo(commentReply.getReplyUserId());
                // User user = userMapper.selectById(commentReply.getReplyUserId());
                if (user != null) {
                    commentReply.setReplyUsername(user.getNickname());
                    commentReply.setReplyAvatar(user.getAvatar());
                }
                // 被评论人信息
                UserComment user1 = getUserCacheInfo(commentReply.getCommentUserId());
                // User user1 = userMapper.selectById(commentReply.getCommentUserId());
                if (user1 != null) {
                    commentReply.setCommentUsername(user1.getNickname());
                }
            });
        }
        // 将reply list信息储存到comment中
        blogComment.setReplyList(replyList);
    }


    @Override
    public Integer onlyCommentCount(Long blogId, Integer type) {
        // 获取评论
        LambdaQueryWrapper<Comment> commentQueryChainWrapper = new LambdaQueryWrapper<>();
        commentQueryChainWrapper.eq(StringUtils.isNotNull(blogId), Comment::getBlogId, blogId);
        commentQueryChainWrapper.eq(Comment::getType, type);
        commentQueryChainWrapper.eq(Comment::isStatus, 1);
        return commentMapper.selectCount(commentQueryChainWrapper);
    }

    private Comment getComment(Long commentId) {
        return MapperUtils.lambdaQueryWrapper(commentMapper).eq(Comment::getCommentId, commentId).select(
                Comment::getCommentId, Comment::getBlogId, Comment::getType
        ).one();
    }

    private CommentReply getCommentReply(Long replyId) {
        return MapperUtils.lambdaQueryWrapper(blogCommentReplyMapper).eq(CommentReply::getReplyId, replyId).select(
                CommentReply::getReplyId, CommentReply::getBlogId, CommentReply::getType
        ).one();
    }


    @Override
    public UserComment cacheCheckUser(String username, String email, boolean subscription) {
        return cacheService.cacheCheckUser(username, email, subscription);
    }


    @Override
    public UserComment cacheCommentSub(String nickname, String email, boolean subscription, Long commentUserId) {
        cacheService.cacheCheckUser(nickname, email, subscription);
        return cacheService.getUserCommentByUserId(commentUserId);
    }

    @Override
    public void cacheCommentSub(String nickname, String email, boolean subscription) {
        cacheService.cacheCommentSub(nickname, email, subscription);
    }


    /**
     * 从缓存中获取用户信息
     *
     * @param userId userid
     * @return UserComment
     */
    public UserComment getUserCacheInfo(Long userId) {
        return cacheService.getUserCommentByUserId(userId);
    }


    /**
     * 删除缓存内容
     */
    void deleteCommentCache(Long blogId, Integer type) {
        // int commentCount = onlyCommentCount(blogId, type);
        // int limit = 5;
        // int totalPage = commentCount / limit + 1;
        // for (int i = 1; i <= totalPage; i++) {
        //     String hk = RedisKey.getKey(blogId, COMMENT_LIST, limit * i, type);
        //     redisService.hdel(COMMENT_LIST, hk);
        // }
    }


}

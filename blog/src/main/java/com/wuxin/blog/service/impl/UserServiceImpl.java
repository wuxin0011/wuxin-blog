package com.wuxin.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuxin.blog.exception.CustomException;
import com.wuxin.blog.mapper.ChatUrlMapper;
import com.wuxin.blog.mapper.UserMapper;
import com.wuxin.blog.mode.PageVo;
import com.wuxin.blog.mode.UserComment;
import com.wuxin.blog.mode.UserPass;
import com.wuxin.blog.pojo.ChatUrl;
import com.wuxin.blog.pojo.User;
import com.wuxin.blog.redis.CacheService;
import com.wuxin.blog.redis.RedisKey;
import com.wuxin.blog.redis.RedisService;
import com.wuxin.blog.service.UserService;
import com.wuxin.blog.utils.KeyUtil;
import com.wuxin.blog.utils.ThrowUtils;
import com.wuxin.blog.utils.mapper.MapperUtils;
import com.wuxin.blog.utils.security.ShiroUtil;
import com.wuxin.blog.utils.string.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


/**
 * @Author: wuxin001
 * @Date: 2021/10/01/11:08
 * @Description:
 */
@Service
@Transactional(rollbackFor = {Exception.class})
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    ChatUrlMapper chatUrlMapper;

    @Autowired
    private RedisService redisService;

    @Autowired
    private CacheService cacheService;

    @Override
    public Long addUser(User user) {
        // 默认为普通用户
        user.setRoleId(1L);
        // 默认不禁用
        user.setStatus(true);
        //默认昵称为用户名
        if(StringUtils.isEmpty(user.getNickname())){
            // 如果用户昵称为null默认用户重新赋值用户昵称为 username
            user.setNickname(user.getUsername());
        }
        // 随机图像
        user.setAvatar(KeyUtil.randomUrl());
        // 盐值加密之后的密码 默认密码为邮箱
        String salt = ShiroUtil.createSalt(user.getUsername());
        user.setPassword(ShiroUtil.salt(user.getPassword(), salt));
        user.setSalt(salt);
        // 添加用户
        userMapper.insert(user);
        //添加用户信息表
        ChatUrl chatUrl = new ChatUrl();
        chatUrl.setUserId(user.getUserId());
        chatUrlMapper.insert(chatUrl);
        return user.getUserId();
    }

    @Override
    public void delUser(Long id) {
        /*如果用户删除了,其用户关联的信息表应该删除！*/
        deleteCommentUserCache();
        ThrowUtils.ops(userMapper.deleteById(id), "用户不存在！");
        QueryWrapper<ChatUrl> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", id);
        chatUrlMapper.delete(queryWrapper);
    }

    @Override
    public void updateUser(User user) {
        ThrowUtils.ops(userMapper.updateById(user), "该用户不存在！");
        deleteCommentUserCache();
    }

    @Override
    public User findUserByEmail(String email) {
        return new LambdaQueryChainWrapper<>(userMapper).eq(User::getEmail, email).one();
    }

    @Override
    public User findUserByUsername(String username) {
        return new LambdaQueryChainWrapper<>(userMapper).eq(User::getUsername, username).one();
    }

    @Override
    public User finUserById(Long userId) {
        User user = userMapper.selectById(userId);
        ThrowUtils.isNull(user, "用户不存在！");
        return user;
    }

    @Override
    public User finUserByPhone(String phone) {
        return new LambdaQueryChainWrapper<>(userMapper).eq(User::getPhone, phone).one();
    }


    @Override
    public int countUser() {
        return userMapper.selectCount(null);
    }


    @Override
    public List<User> selectUser(String username) {
        return new LambdaQueryChainWrapper<>(userMapper).eq(User::getUsername, username).list();
    }

    @Override
    public IPage<User> finUserByKeywords(PageVo pageVo) {
        String key = RedisKey.getKey("user:page:list", pageVo.getCurrent(), pageVo.getLimit(), pageVo.getKeywords() + pageVo.getStart() + pageVo.getEnd());
        boolean hasKey = redisService.hHasKey("user:page:list", key);
        if (hasKey) {
            IPage<User> page = (IPage<User>) redisService.hget("user:page:list", key);
            if (StringUtils.isNotNull(page) && page.getRecords().size() != 0) {
                return page;
            }
        }
        Page<User> page = new LambdaQueryChainWrapper<>(userMapper).select(User::getUserId,
                        User::getUsername,
                        User::getEmail,
                        User::getNickname,
                        User::getMotto,
                        User::getPhone,
                        User::getRoleId,
                        User::getAvatar,
                        User::getCreateTime)
                .like(StringUtils.isNotEmpty(pageVo.getKeywords()), User::getUsername, pageVo.getKeywords())
                .or().like(StringUtils.isNotEmpty(pageVo.getKeywords()), User::getEmail, pageVo.getKeywords())
                .or().like(StringUtils.isNotEmpty(pageVo.getKeywords()), User::getNickname, pageVo.getKeywords())
                .between(StringUtils.isNotEmpty(pageVo.getStart()) && StringUtils.isNotEmpty(pageVo.getEnd()), User::getCreateTime, pageVo.getStart(), pageVo.getEnd())
                .page(new Page<>(pageVo.getCurrent(), pageVo.getLimit()));
        // 用户信息缓存一分钟
        redisService.hset("user:page:list", key, page, 60L);
        return page;

    }

    @Override
    public boolean updatePass(Long loginUserId, UserPass user) {
        // 获取数据库用户信息
        User loginUser = finUserById(loginUserId);
        // 判断旧密码和数据库密码是否一致
        String oldPassword = ShiroUtil.salt(user.getOldPassword(), loginUser.getSalt());
        String newPassword = ShiroUtil.salt(user.getNewPassword(), loginUser.getSalt());
        boolean valid = loginUser.getPassword().equals(oldPassword);
        // 如果密码输入错误
        if (!valid) {
            throw new CustomException("原密码校验不通过！");
        }
        // 用户名是否修改了 判断密码是否一致
        boolean usernameChange = loginUser.getUsername().equals(user.getUsername());
        if (usernameChange && user.getNewPassword().equals(user.getOldPassword())) {
            throw new CustomException("修改失败,原密码和新密码一致！");
        }
        if (usernameChange) {
            // 如果用户名没有修改，数据库中盐不需要修改直接修改用户密码就可以了
            loginUser.setPassword(newPassword);
        } else {
            // 用户名修改了，需要重新生成盐
            String salt = ShiroUtil.createSalt(user.getUsername());
            newPassword = ShiroUtil.salt(user.getNewPassword(), salt);
            loginUser.setUsername(user.getUsername());
            loginUser.setPassword(newPassword);
            loginUser.setSalt(salt);
        }
        // 删除缓存用户信息
        deleteCommentUserCache();
        return new LambdaUpdateChainWrapper<>(userMapper).eq(User::getUserId, loginUserId).update(loginUser);
    }

    @Override
    public boolean updatePasswordByEmail(String email, String newPassword) {
        User user = findUserByEmail(email);
        newPassword = ShiroUtil.salt(newPassword, user.getSalt());
        user.setPassword(newPassword);
        return new LambdaUpdateChainWrapper<>(userMapper).eq(User::getEmail, email).update(user);
    }


    @Override
    public User findAdminUserInfo(Long adminUserId) {
        return new LambdaQueryChainWrapper<>(userMapper).select(User::getNickname, User::getAvatar, User::getMotto)
                .eq(User::getUserId, adminUserId).one();
    }


    @Override
    public List<User> findCommentUserByUsernameOrEmail(String username, String email) {
        return new LambdaQueryChainWrapper<>(userMapper).eq(User::getUsername, username).or().eq(User::getNickname, username).or().eq(User::getEmail, email).
                select(
                        User::getUserId, User::getUsername, User::getNickname, User::getEmail, User::getAvatar
                ).list();

    }


    @Override
    public User checkUsernameAndEmail(String nickname, String email) {
        return new LambdaQueryChainWrapper<>(userMapper).eq(User::getNickname, nickname).eq(User::getEmail, email).select(
                User::getUserId, User::getUsername, User::getAvatar, User::getNickname, User::getEmail, User::isSubscription
        ).one();
    }


    @Override
    public User findUserDetail(Long userId) {
        User one = new LambdaQueryChainWrapper<>(userMapper).eq(User::getUserId, userId).select(
                User::getUserId, User::getUsername, User::getAvatar, User::getNickname, User::getEmail, User::getMotto, User::getPhone
        ).one();
        ThrowUtils.isNull(one, "用户不存在！");
        return one;
    }

    @Override
    public Long getCommentUserId(String username, String email, boolean subscription) {
        List<User> userOr = findCommentUserByUsernameOrEmail(username, email);
        // 判断用户是否存在
        Long userId = null;
        if (userOr.size() == 0) {
            User newUser = new User();
            // 默认用户登录名称为邮箱
            newUser.setUsername(email);
            newUser.setEmail(email);
            // 默认用户昵称为用户注册名
            newUser.setNickname(username);
            newUser.setSubscription(subscription);
            // 将初始密码设置为用户邮箱
            newUser.setPassword(email);
            userId = addUser(newUser);
        } else {
            // 用户名和邮箱是否输入正确 从缓存中获取
            UserComment userComment = cacheService.cacheCheckUser(username, email, subscription);
            if (StringUtils.isNotNull(userComment)) {
                return userComment.getUserId();
            }
            // 缓存中没有从数据库中获取
            User userAnd = checkUsernameAndEmail(username, email);
            if (StringUtils.isNotNull(userAnd)) {
                // 将用户信息添加到缓存中
                UserComment instance = UserComment.getInstance(userId, username, email, userAnd.getAvatar(), subscription);
                redisService.hset(RedisKey.USER_COMMENT_SUB, RedisKey.getKey(userAnd.getUserId(), RedisKey.USER_COMMENT_SUB), instance, 604800L);
                redisService.hset(RedisKey.USER_COMMENT_SUB, RedisKey.getKey(userAnd.getNickname(), RedisKey.USER_COMMENT_SUB), instance, 604800L);
                userId = userAnd.getUserId();
            }
        }
        return userId;
    }


    @Override
    public User selectCommentUserByUserId(Long commentUserId) {
        return new LambdaQueryChainWrapper<>(userMapper).eq(User::getUserId, commentUserId).select(User::getNickname, User::getEmail, User::getUserId, User::isSubscription).one();
    }

    @Override
    public IPage<User> selectUserRoleList(PageVo pageVo) {
        return new LambdaQueryChainWrapper<>(userMapper)
                .eq(StringUtils.isNotNull(pageVo.getId()), User::getRoleId, pageVo.getId())
                .like(StringUtils.isNotEmpty(pageVo.getKeywords()), User::getNickname, pageVo.getKeywords())
                .orderByDesc(User::getRoleId)
                .select(User::getUserId, User::getAvatar, User::getUsername, User::getNickname, User::getRoleId, User::isStatus)
                .page(new Page<>(pageVo.getCurrent(), pageVo.getLimit()));
    }

    @Override
    public boolean updateUserRole(User user) {
        deleteCommentUserCache();
        return MapperUtils.lambdaUpdateChainWrapper(userMapper).eq(User::getUserId, user.getUserId()).update(user);
    }

    @Override
    public User findUserByNickName(String nickname) {
        return new LambdaQueryChainWrapper<>(userMapper).eq(User::getNickname, nickname).one();
    }

    /**
     * 删除评论信息缓存
     */
    public void deleteCommentUserCache() {
        redisService.del(RedisKey.USER_COMMENT_SUB);
    }
}

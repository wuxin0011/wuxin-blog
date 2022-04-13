package com.wuxin.blog.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuxin.blog.mapper.MomentMapper;
import com.wuxin.blog.mapper.UserMapper;
import com.wuxin.blog.pojo.Moment;
import com.wuxin.blog.pojo.User;
import com.wuxin.blog.redis.RedisKey;
import com.wuxin.blog.redis.RedisService;
import com.wuxin.blog.service.MomentService;
import com.wuxin.blog.utils.ThrowUtils;
import com.wuxin.blog.utils.mapper.MapperUtils;
import com.wuxin.blog.utils.string.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author: wuxin001
 * @Date: 2021/10/03/1:35
 * @Description:
 */
@Service
@Transactional(rollbackFor = {Exception.class})
public class MomentServiceImpl implements MomentService {

    private final static String MESSAGE = "操作失败！该动态不存在";

    private final static String MOMENT_LIST = RedisKey.MOMENT_LIST;


    @Autowired
    private MomentMapper momentMapper;

    @Autowired
    private UserMapper userMapper;


    @Autowired
    private RedisService redisService;


    @Override
    public void add(Moment moment) {
        moment.setLikes(0);
        ThrowUtils.ops(momentMapper.insert(moment), "动态添加失败！");
        deleteMomentCache();

    }


    @Override
    public void update(Moment moment) {
        ThrowUtils.ops(momentMapper.updateById(moment), MESSAGE);
        deleteMomentCache();
    }

    @Override
    public void delete(Long momentId) {
        ThrowUtils.ops(momentMapper.deleteById(momentId), MESSAGE);
        deleteMomentCache();
    }

    @Override
    public IPage<Moment> selectListByPage(Integer current, Integer limit) {
        String hk = RedisKey.getKey(current, MOMENT_LIST);
        boolean b = redisService.hHasKey(MOMENT_LIST, hk);
        if (b) {
            IPage<Moment> page = (IPage<Moment>) redisService.hget(MOMENT_LIST, hk);
            if (StringUtils.isNotNull(page) && page.getRecords().size() != 0) {
                return page;
            }
        }
        // 从数据库获取动态信息同时存入到redis中
        LambdaQueryChainWrapper<Moment> wrapper = new LambdaQueryChainWrapper<>(momentMapper);
        Page<Moment> page = new Page<>(current, limit);
        Page<Moment> momentPage = wrapper.orderByDesc(Moment::getCreateTime).page(page);
        // 获取用户名 用户头像等信息
        getUserNameAndAvatar(momentPage);
        // Page<Moment> page = new Page<>(current, limit);
        // List<Moment> moments = momentMapper.queryMomentPage(current, limit);
        // page.setRecords(moments) ;
        // 存入redis 缓存有效期为7天
        redisService.hset(MOMENT_LIST, hk, page,604800L );
        return page;
    }


    @Override
    public Moment find(Long momentId) {
        return momentMapper.selectById(momentId);
    }

    @Override
    public List<Moment> list() {
        ThrowUtils.ops(0, "该功能还未实现哦");
        return null;
    }


    @Override
    public IPage<Moment> selectListByPage(Integer current, Integer limit, String keywords) {
        ThrowUtils.ops(0, "该功能还未实现哦");
        return null;
    }


    @Override
    public IPage<Moment> selectListByPage(Integer current, Integer limit, String keywords, String start, String end) {
        String key = RedisKey.getKey("moment:page", current, limit, keywords);
        boolean hasKey = redisService.hHasKey("moment:page", key);
        if(hasKey){
            Page<Moment> momentPage = (Page<Moment>) redisService.hget("moment:page", key);
            if(StringUtils.isNotNull(momentPage) && momentPage.getRecords().size() !=0){
                return momentPage;
            }
        }
        LambdaQueryChainWrapper<Moment> queryChainWrapper = new LambdaQueryChainWrapper<>(momentMapper);
        queryChainWrapper.orderByDesc(Moment::getCreateTime);
        queryChainWrapper.like(StringUtils.isNotEmpty(keywords), Moment::getContent, keywords);
        queryChainWrapper.between(StringUtils.isNotEmpty(end) && StringUtils.isNotEmpty(start),Moment::getCreateTime,start,end);
        Page<Moment> momentPage = queryChainWrapper.page(new Page<>(current, limit));

        // 获取用户名 用户头像等信息
        getUserNameAndAvatar(momentPage);

        // 缓存一分钟
        redisService.hset("moment:page",key,momentPage,60L);
        return momentPage;
    }


    /**
     * 获取用户信息
     *
     * @param page momentPage
     */
    public void getUserNameAndAvatar(Page<Moment> page) {
        page.getRecords().forEach(moment -> {
            // 从缓存获取用户信息 如果缓存中没有用户信息就从数据库中获取用户信心
            User user = new LambdaQueryChainWrapper<User>(userMapper)
                    .eq(User::getUserId, moment.getUserId())
                    .select(User::getUserId, User::getNickname, User::getAvatar).one();
            moment.setUsername(user.getNickname());
            moment.setAvatar(user.getAvatar());
        });
    }

    /**
     * 删除动态缓存
     */
    public void deleteMomentCache() {
        int count = momentMapper.selectCount(null);
        for (int i = 1; i < count; i++) {
            String hk = RedisKey.getKey(i, MOMENT_LIST);
            redisService.hdel(RedisKey.MOMENT_LIST, hk);
        }

    }

}

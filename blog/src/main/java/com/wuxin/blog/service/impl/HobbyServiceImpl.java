package com.wuxin.blog.service.impl;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.wuxin.blog.mapper.HobbyMapper;
import com.wuxin.blog.pojo.Hobby;
import com.wuxin.blog.constant.RedisKey;
import com.wuxin.blog.redis.RedisService;
import com.wuxin.blog.service.HobbyService;
import com.wuxin.blog.utils.ThrowUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
public class HobbyServiceImpl implements HobbyService {

    @Autowired
    private HobbyMapper hobbyMapper;

    @Autowired
    private RedisService redisService;


    @Override
    public void add(Hobby hobby) {
        hobbyMapper.insert(hobby);
        // 删除缓存
        deleteCache(hobby.getUserId());
    }

    @Override
    public void update(Hobby hobby) {
        hobbyMapper.updateById(hobby);
        // 删除缓存
        deleteCache(hobby.getUserId());
    }

    @Override
    public void delete(Long id) {
        hobbyMapper.deleteById(id);
        // 删除缓存
        deleteCache(id);
    }


    @Override
    public Hobby find(Long id) {
        return hobbyMapper.selectById(id);
    }

    @Override
    public List<Hobby> list() {
        ThrowUtils.ops(0, "该功能还未实现哦");
        return null;
    }

    @Override
    public List<Hobby> selectListByUserId(Long userId) {
        String key = RedisKey.getKey(userId);
        boolean hasKey = redisService.hHasKey(RedisKey.HOBBY_INFO, key);
        if (hasKey) {
            Object hget = redisService.hget(RedisKey.HOBBY_INFO, key);
            List<Hobby> list = JSON.parseArray(JSON.toJSONString(hget), Hobby.class);
            if (list.size() != 0) {
                return list;
            }
        }
        List<Hobby> list = new LambdaQueryChainWrapper<>(hobbyMapper).eq(Hobby::getUserId, userId).list();
        // 缓存有效期
        redisService.hset(RedisKey.HOBBY_INFO, key, list,86400L);
        return list;
    }

    public void deleteCache(Long userId) {
        List<Hobby> list = selectListByUserId(userId);
        if (list.size() == 0) {
            return;
        }
        // 删除缓存
        String key = RedisKey.getKey(userId);
        redisService.hdel(RedisKey.HOBBY_INFO, key);
    }
}

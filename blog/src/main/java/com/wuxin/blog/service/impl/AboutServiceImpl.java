package com.wuxin.blog.service.impl;

import com.alibaba.fastjson.JSON;
import com.wuxin.blog.mapper.AboutMapper;
import com.wuxin.blog.pojo.About;
import com.wuxin.blog.redis.RedisKey;
import com.wuxin.blog.redis.RedisService;
import com.wuxin.blog.service.AboutService;
import com.wuxin.blog.utils.JsonFormatUtils;
import com.wuxin.blog.utils.ThrowUtils;
import com.wuxin.blog.utils.string.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author: wuxin001
 * @Date: 2021/10/01/11:08
 * @Description:
 */
@Transactional(rollbackFor = {Exception.class})
@Service
public class AboutServiceImpl implements AboutService {

    private static final String ABOUT = RedisKey.ABOUT;


    @Autowired
    private AboutMapper aboutMapper;

    @Autowired
    private RedisService redisService;


    @Override
    public void updateById(About about) {
        about.setAboutId(About.ABOUT_ID);
        About ab = queryById(About.ABOUT_ID);
        if (StringUtils.isNull(ab)) {
            aboutMapper.insert(about);
        } else {
            ThrowUtils.ops(aboutMapper.updateById(about), "修改失败！");
        }
        // 删除缓存内容
        redisService.del(ABOUT);
    }


    @Override
    public About queryById(Long id) {
        boolean b = redisService.hasKey(ABOUT);
        if (b) {
            About about = JsonFormatUtils.toObject(redisService.get(ABOUT), About.class);
            if (about != null) {
                return about;
            }
        }
        About about = aboutMapper.selectById(id);
        // 将about内容存入redis中
        redisService.set(ABOUT, about,100000L);
        ThrowUtils.isNull(about, "内容不存在！");
        return about;
    }

}

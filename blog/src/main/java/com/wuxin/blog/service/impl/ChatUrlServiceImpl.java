package com.wuxin.blog.service.impl;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.wuxin.blog.constant.GlobalConstant;
import com.wuxin.blog.mapper.ChatUrlMapper;
import com.wuxin.blog.pojo.ChatUrl;
import com.wuxin.blog.constant.RedisKey;
import com.wuxin.blog.redis.RedisService;
import com.wuxin.blog.service.ChatUrlService;
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
@Service
@Transactional(rollbackFor = {Exception.class})
public class ChatUrlServiceImpl implements ChatUrlService {

    @Autowired
    private ChatUrlMapper chatUrlMapper;

    @Autowired
    private RedisService redisService;


    @Override
    public void updateChatUrl(ChatUrl chatUrl) {
        if(StringUtils.isNotNull(chatUrl.getUserId()) && chatUrl.getUserId().equals(GlobalConstant.ADMIN_USER_ID)){
            redisService.del(RedisKey.BLOGGER_INFO);
        }
        ThrowUtils.ops( chatUrlMapper.updateById(chatUrl),"修改失败！");
        redisService.del(RedisKey.CHAT_INFO,RedisKey.getKey(chatUrl.getUserId()));
    }


    @Override
    public  ChatUrl findChatUrlByUserId(Long userId) {
        String key = RedisKey.getKey(userId);
        boolean hasKey = redisService.hHasKey(RedisKey.CHAT_INFO, key);
        if(hasKey){
            ChatUrl chatUrl = JsonFormatUtils.toObject(redisService.hget(RedisKey.CHAT_INFO, key),ChatUrl.class);
            if(StringUtils.isNotNull(chatUrl)){
                return chatUrl;
            }
        }
        LambdaQueryChainWrapper<ChatUrl> queryChainWrapper = new LambdaQueryChainWrapper<>(chatUrlMapper);
        ChatUrl chatUrl = queryChainWrapper.eq(ChatUrl::getUserId, userId).one();
        ThrowUtils.isNull(chatUrl,"用户拓展信息不存在,用户可能不存在！");
        redisService.hset(RedisKey.CHAT_INFO,key,chatUrl);
        return chatUrl;
    }
}

package com.wuxin.blog.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuxin.blog.mapper.FriendMapper;
import com.wuxin.blog.pojo.Friend;
import com.wuxin.blog.pojo.FriendMessage;
import com.wuxin.blog.constant.RedisKey;
import com.wuxin.blog.redis.RedisService;
import com.wuxin.blog.service.FriendService;
import com.wuxin.blog.utils.JsonFormatUtils;
import com.wuxin.blog.utils.ThrowUtils;
import com.wuxin.blog.utils.mapper.MapperUtils;
import com.wuxin.blog.utils.string.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author: wuxin001
 * @Date: 2021/10/01/10:57
 * @Description:
 */
@Service
@Transactional(rollbackFor = {Exception.class})
public class FriendServiceImpl implements FriendService {

    @Autowired
    private FriendMapper friendMapper;

    @Autowired
    private RedisService redisService;

    private static final String REDIS_KEY = RedisKey.FRIEND_LIST;

    private static final String MESSAGE  = "friend_message";

    @Override
    public void add(Friend friend) {
        ThrowUtils.ops(friendMapper.insert(friend), "友情链接添加失败！");
        // 从list中获取数据
        List<Friend> list = list();
        list.add(friend);
        // 重新设置redis中数据
        redisService.set(REDIS_KEY,list);
    }

    @Override
    public void update(Friend friend) {
        ThrowUtils.ops(friendMapper.updateById(friend), "该链接不存在！");
        // 修改redis中数据
        List<Friend> list = list();
        for (int i = 0; i < list.size(); i++) {
            if(friend.getFriendId().equals(list.get(i).getFriendId())){
                // 移除指定内容
                list.remove(i);
                // 将新的内容添加到list中
                list.add(friend);
                // 将修改后的内容添加到redis中
                redisService.set(REDIS_KEY,list);
            }
        }

    }

    @Override
    public void delete(Long friendId) {
        ThrowUtils.ops(friendMapper.deleteById(friendId), "该链接不存在！");
        // 删除redis中指定连接
        List<Friend> list = list();
        for (int i = 0; i < list.size(); i++) {
            if(friendId.equals(list.get(i).getFriendId())){
                // 移除redis中指定list
                list.remove(i);
                // 重新设置redis中数据
                redisService.set(REDIS_KEY,list);
            }
        }
    }

    @Override
    public IPage<Friend> selectListByPage(Integer current, Integer limit, String keywords) {
        return MapperUtils.lambdaQueryWrapper(friendMapper)
                .like(StringUtils.isNotEmpty(keywords), Friend::getUsername, keywords)
                .page(new Page<>(current, limit));
    }


    @Override
    public Friend find(Long id) {
        return null;
    }

    @Override
    public List<Friend> list() {
        boolean b = redisService.hasKey(REDIS_KEY);
        if (b) {
            List<Friend> friendList = JsonFormatUtils.objectToArr(redisService.get(REDIS_KEY), Friend.class);
            if (friendList.size() != 0) {
                return friendList;
            }
        }
        // 从数据库中查找
        List<Friend> friendList = friendMapper.selectList(null);
        redisService.set(REDIS_KEY,friendList);
        return friendList;
    }

    @Override
    public IPage<Friend> selectListByPage(Integer current, Integer limit, String keywords, String start, String end) {
        String key = RedisKey.getKey("friend:page:list", current, limit, start + end + keywords);
        boolean hasKey = redisService.hHasKey("friend:page:list", key);
        if (hasKey) {
            IPage<Friend> page = (IPage<Friend>) redisService.hget("friend:page:list", key);
            if(StringUtils.isNotNull(page) && page.getRecords().size() !=0){
                return page;
            }
        }
        Page<Friend> page = new LambdaQueryChainWrapper<>(friendMapper).orderByDesc(Friend::getCreateTime)
                .like(StringUtils.isNotEmpty(keywords), Friend::getUsername, keywords)
                .between(StringUtils.isNotEmpty(end) && StringUtils.isNotEmpty(start), Friend::getCreateTime, start, end)
                .page(new Page<>(current, limit));

        // 缓存三分钟
        redisService.hset("friend:page:list",key,page,180L);
        return page;
    }


    @Override
    public IPage<Friend> selectListByPage(Integer current, Integer limit) {
        return null;
    }


    @Override
    public FriendMessage findFriendMessage(Long id) {
        return friendMapper.selectFriendMessage(id);
    }

    @Override
    public void updateFriendMessage(FriendMessage friendMessage) {
        // 初始化 如果不存在就创建对象
        friendMessage.setId(FriendMessage.FRIEND_MESSAGE_ID);
        if(findFriendMessage(friendMessage.getId())==null){
            friendMapper.addFriendMessage(friendMessage);
            return;
        }
        // 删除缓存
        redisService.hdel(MESSAGE);
        friendMapper.updateFriendMessage(friendMessage);
    }
}

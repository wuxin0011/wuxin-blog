package com.wuxin.blog.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuxin.blog.mapper.AccessLogMapper;
import com.wuxin.blog.pojo.AccessLog;
import com.wuxin.blog.constant.RedisKey;
import com.wuxin.blog.redis.RedisService;
import com.wuxin.blog.service.AccessLogService;
import com.wuxin.blog.utils.ThrowUtils;
import com.wuxin.blog.utils.string.StringUtils;
import com.wuxin.blog.utils.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author: wuxin001
 * @Date: 2022/01/06/21:51
 * @Description:
 */
@Service
@Transactional(rollbackFor = {Exception.class})
public class AccessLogServiceImpl implements AccessLogService {

    @Autowired
    AccessLogMapper accessLogMapper;

    @Autowired
    private RedisService redisService;

    @Override
    public void add(AccessLog accessLog) {
        ThrowUtils.ops(accessLogMapper.insert(accessLog), "访问日志添加失败");
    }


    @Override
    public List<AccessLog> list() {
        ThrowUtils.ops(0, "该功能还未实现哦！");
        return null;
    }

    @Override
    public void deleteAll() {
        accessLogMapper.delete(null);
    }

    @Override
    public void batchDelete() {
        ThrowUtils.ops(0, "该功能还未实现哦！");
    }

    @Override
    public void delete(Long id) {
        ThrowUtils.ops(accessLogMapper.deleteById(id), "日志不存在！");
    }


    @Override
    public IPage<AccessLog> selectListByPage(Integer current, Integer limit, String keywords, String start, String end) {
        String key = RedisKey.getKey("access:page:list", current, limit, start + end + keywords);
        boolean hasKey = redisService.hHasKey("access:page:list", key);
        if(hasKey){
            Page<AccessLog> page = (Page<AccessLog>) redisService.hget("access:page:list", key);
            if(StringUtils.isNotNull(page) && page.getRecords().size() !=0){
                return page;
            }
        }
        Page<AccessLog> page = new LambdaQueryChainWrapper<>(accessLogMapper)
                .orderByDesc(AccessLog::getCreateTime)
                .eq(StringUtils.isNotEmpty(keywords), AccessLog::getByCreate, keywords)
                .between(StringUtils.isNotEmpty(end) && StringUtils.isNotEmpty(start), AccessLog::getCreateTime, start, end).page(new Page<>(current, limit));

        // 访问日志缓存2分钟
        redisService.hset("access:page:list",key,page,120L);
        return page;
    }


    @Override
    public Integer selectTodayAccessLog() {
        String start = DateUtils.todayStartTime();
        String end = DateUtils.localTime();
        return accessLogMapper.todayAccessCount(start, end);
    }
}

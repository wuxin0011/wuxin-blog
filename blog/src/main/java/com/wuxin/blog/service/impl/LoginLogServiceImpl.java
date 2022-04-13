package com.wuxin.blog.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuxin.blog.mapper.LoginLogMapper;
import com.wuxin.blog.pojo.LoginLog;
import com.wuxin.blog.redis.RedisKey;
import com.wuxin.blog.redis.RedisService;
import com.wuxin.blog.service.LoginLogService;
import com.wuxin.blog.utils.ThrowUtils;
import com.wuxin.blog.utils.mapper.MapperUtils;
import com.wuxin.blog.utils.string.StringUtils;
import com.wuxin.blog.utils.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author: wuxin001
 * @Date: 2022/01/06/23:29
 * @Description:
 */
@Service
@Transactional(rollbackFor = {Exception.class})
public class LoginLogServiceImpl implements LoginLogService {

    @Autowired
    private LoginLogMapper loginLogMapper;


    @Autowired
    private RedisService redisService;

    @Override
    public void add(LoginLog loginLog) {
        ThrowUtils.ops(loginLogMapper.insert(loginLog), "添加失败");
    }


    @Override
    public List<LoginLog> list() {
        return null;
    }

    @Override
    public void delete(Long id) {
        ThrowUtils.ops(loginLogMapper.deleteById(id), "日志不存在！");
    }

    @Override
    public void deleteAll() {
        loginLogMapper.delete(null);
    }

    @Override
    public void batchDelete() {
        ThrowUtils.ops(0, "该接口功能还未实现哦");
    }

    @Override
    public IPage<LoginLog> selectListByPage(Integer current, Integer limit, String keywords, String start, String end) {
        String key = RedisKey.getKey("login:page:list", current, limit, start + end + keywords);
        boolean hasKey = redisService.hHasKey("login:page:list", key);
        if (hasKey) {
            IPage<LoginLog> page = (IPage<LoginLog>) redisService.hget("login:page:list", key);
            if (StringUtils.isNotNull(page) && page.getRecords().size() != 0) {
                return page;
            }
        }
        Page<LoginLog> page = new LambdaQueryChainWrapper<>(loginLogMapper)
                .orderByDesc(LoginLog::getCreateTime)
                .eq(StringUtils.isNotEmpty(keywords), LoginLog::getByCreate, keywords)
                .between(StringUtils.isNotEmpty(end) && StringUtils.isNotEmpty(start), LoginLog::getCreateTime, start,end)
                .page(new Page<>(current, limit));
        // 登录日志缓存一分钟
        redisService.hset("login:page:list",key,page,60L);
        return page;
    }


    @Override
    public Integer selectTodayLoginLog() {
        return loginLogMapper.todayLoginCount(DateUtils.todayStartTime(), DateUtils.localTime());
    }
}

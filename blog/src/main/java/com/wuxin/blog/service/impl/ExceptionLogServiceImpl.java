package com.wuxin.blog.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuxin.blog.mapper.ExceptionLogMapper;
import com.wuxin.blog.pojo.ExceptionLog;
import com.wuxin.blog.constant.RedisKey;
import com.wuxin.blog.redis.RedisService;
import com.wuxin.blog.service.ExceptionService;
import com.wuxin.blog.utils.ThrowUtils;
import com.wuxin.blog.utils.string.StringUtils;
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
public class ExceptionLogServiceImpl implements ExceptionService {


    @Autowired
    private ExceptionLogMapper exceptionLogMapper;

    @Autowired
    private RedisService redisService;


    @Override
    public void add(ExceptionLog exceptionLog) {
        ThrowUtils.ops(exceptionLogMapper.insert(exceptionLog),"异常日志添加失败！");
    }


    @Override
    public void delete(Long id) {
        ThrowUtils.ops(exceptionLogMapper.deleteById(id) ,"日志不存在！");
    }

    @Override
    public void deleteAll() {
        exceptionLogMapper.delete(null);
    }


    @Override
    public void batchDelete() {
        ThrowUtils.isNull(0,"该功能还未实现哦！");
    }


    @Override
    public List<ExceptionLog> list() {
        return null;
    }

    @Override
    public IPage<ExceptionLog> selectListByPage(Integer current, Integer limit, String keywords, String start, String end) {
        String key = RedisKey.getKey("exception:page:list", current, limit, start + end + keywords);
        boolean hasKey = redisService.hHasKey("exception:page:list", key);
        if (hasKey) {
            IPage<ExceptionLog> page = (IPage<ExceptionLog>) redisService.hget("exception:page:list", key);
            if(StringUtils.isNotNull(page) && page.getRecords().size() !=0){
                return page;
            }
        }
        LambdaQueryChainWrapper<ExceptionLog> queryChainWrapper = new LambdaQueryChainWrapper<>(exceptionLogMapper);
        queryChainWrapper.orderByDesc(ExceptionLog::getCreateTime);
        queryChainWrapper.eq(StringUtils.isNotEmpty(keywords), ExceptionLog::getByCreate, keywords);
        queryChainWrapper.between(StringUtils.isNotEmpty(end) && StringUtils.isNotEmpty(start),ExceptionLog::getCreateTime,start,end );
        Page<ExceptionLog> page = queryChainWrapper.page(new Page<>(current, limit));
        // 异常日志缓存十分钟
        redisService.hset("exception:page:list",key,page,600L);
        return page;
    }
}

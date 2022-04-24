package com.wuxin.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuxin.blog.mapper.VisitorMapper;
import com.wuxin.blog.mode.PageVo;
import com.wuxin.blog.pojo.Visitor;
import com.wuxin.blog.constant.RedisKey;
import com.wuxin.blog.redis.RedisService;
import com.wuxin.blog.service.VisitorService;
import com.wuxin.blog.utils.string.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: wuxin001
 * @Date: 2022/03/21/10:05
 * @Description: 访问用户实现
 */
@Service("VisitorService")
public class VisitorServiceImpl extends ServiceImpl<VisitorMapper, Visitor> implements VisitorService {

    @Autowired
    private VisitorMapper visitorMapper;

    @Autowired
    private RedisService redisService;


    @Override
    public void updateVisitorByUUID(Visitor visitor) {
        QueryWrapper<Visitor> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uuid", visitor.getUuid());
        visitorMapper.update(visitor, queryWrapper);
    }


    @Override
    public Visitor queryVisitorByUUID(String uuid) {
        return new LambdaQueryChainWrapper<>(visitorMapper).eq(Visitor::getUuid, uuid).one();
    }


    @Override
    public IPage<Visitor> queryVisitorPage(PageVo pageVo) {
        String k = "visitor:page:list";
        String hk = RedisKey.getKey(k, pageVo.getCurrent(), pageVo.getLimit(), pageVo.getStart() + pageVo.getEnd() + pageVo.getKeywords());
        boolean hasKey = redisService.hHasKey(k, hk);
        if(hasKey){
            IPage<Visitor> page = (IPage<Visitor>) redisService.hget(k, hk);
            if (StringUtils.isNotNull(page) && page.getRecords().size() != 0) {
                return page;
            }
        }

        LambdaQueryChainWrapper<Visitor> queryChainWrapper = new LambdaQueryChainWrapper<>(visitorMapper);
        queryChainWrapper.orderByDesc(Visitor::getUpdateTime);
        queryChainWrapper.like(StringUtils.isNotEmpty(pageVo.getKeywords()), Visitor::getUuid, pageVo.getKeywords());
        queryChainWrapper.between(StringUtils.isNotEmpty(pageVo.getStart()) && StringUtils.isNotEmpty(pageVo.getEnd()), Visitor::getUpdateTime, pageVo.getStart(), pageVo.getEnd());
        Page<Visitor> page = queryChainWrapper.page(new Page<>(pageVo.getCurrent(), pageVo.getLimit()));
        // 缓存有效时间两个小时
        redisService.hset(k,hk,page,7200L);
        return page;
    }
}

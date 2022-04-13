package com.wuxin.blog.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuxin.blog.mapper.UploadPictureMapper;
import com.wuxin.blog.pojo.UploadPicture;
import com.wuxin.blog.redis.RedisKey;
import com.wuxin.blog.redis.RedisService;
import com.wuxin.blog.service.UploadPictureService;
import com.wuxin.blog.utils.ThrowUtils;
import com.wuxin.blog.utils.mapper.MapperUtils;
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
public class UploadPictureServiceImpl implements UploadPictureService {

    private static final String TYPE = RedisKey.UPLOAD_TYPE;
    private static final Integer DEFAULT = 1;
    private static final Integer GTIEE = 2;

    @Autowired
    private UploadPictureMapper uploadPictureMapper;


    @Autowired
    private RedisService redisService;


    @Override
    public void add(UploadPicture uploadPicture) {
        ThrowUtils.ops(uploadPictureMapper.insert(uploadPicture), "图片记录添加失败");
    }


    @Override
    public void delete(Long id) {
        ThrowUtils.ops(uploadPictureMapper.deleteById(id), "仓库记录不存在！");
    }

    @Override
    public IPage<UploadPicture> selectListByPage(Integer current, Integer limit, String start, String end) {
        String key = RedisKey.getKey("upload:page:list", current, limit, start + end);
        boolean hasKey = redisService.hHasKey("upload:page:list", key);
        if (hasKey) {
            IPage<UploadPicture> page = (IPage<UploadPicture>) redisService.hget("upload:page:list", key);
            if (StringUtils.isNotNull(page) && page.getRecords().size() != 0) {
                return page;
            }

        }
        Page<UploadPicture> page = new LambdaQueryChainWrapper<>(uploadPictureMapper)
                .orderByDesc(UploadPicture::getCreateTime)
                .between(StringUtils.isNotEmpty(end) && StringUtils.isNotEmpty(start), UploadPicture::getCreateTime, start, end)
                .page(new Page<>(current, limit));
        // 图片日志缓存十分钟
        redisService.hset("upload:page:list",key,page,600L);
        return page;

    }

    @Override
    public Integer queryType() {
        boolean hasKey = redisService.hasKey(TYPE);
        if (hasKey) {
            return (Integer) redisService.get(TYPE);
        }
        return DEFAULT;
    }

    @Override
    public boolean updateType(Integer type) {
        if (type.equals(GTIEE)) {
            redisService.set(TYPE, GTIEE);
            return true;
        }
        if (type.equals(DEFAULT)) {
            redisService.set(TYPE, DEFAULT);
            return true;
        }
        return false;
    }


}

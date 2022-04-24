package com.wuxin.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuxin.blog.exception.CustomException;
import com.wuxin.blog.mapper.ArchiveMapper;
import com.wuxin.blog.pojo.Archive;
import com.wuxin.blog.constant.RedisKey;
import com.wuxin.blog.redis.RedisService;
import com.wuxin.blog.service.ArchiveService;
import com.wuxin.blog.utils.ThrowUtils;
import com.wuxin.blog.utils.mapper.MapperUtils;
import com.wuxin.blog.utils.string.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: wuxin001
 * @Date: 2021/10/01/9:25
 * @Description:
 */
@Service
@Transactional(rollbackFor = {Exception.class})
public class ArchiveServiceImpl implements ArchiveService {


    private static final String ARCHIVE_LIST = RedisKey.ARCHIVE_LIST;

    @Autowired
    private ArchiveMapper archiveMapper;

    @Autowired
    private RedisService redisService;

    public static final Integer TYPE_1 = 1;

    public static final Integer TYPE_2 = 2;


    @Override
    public void add(Archive archive) {
        ThrowUtils.ops( archiveMapper.insert(archive), "添加失败！");
        deleteArchiveCache();
    }

    @Override
    public void update(Archive archive) {
        LambdaUpdateChainWrapper<Archive> chainWrapper = new LambdaUpdateChainWrapper<>(archiveMapper);
        boolean update = chainWrapper.eq(Archive::getArchiveId, archive.getArchiveId()).update(archive);
        if (!update) {
            throw new CustomException("修改失败,内容不存在！");
        }
        // 删除缓存
        deleteArchiveCache();
    }

    @Override
    public void delete(Long archiveId) {
        ThrowUtils.ops(archiveMapper.deleteById(archiveId), "删除失败,内容不存在！");
        deleteArchiveCache();
    }

    @Override
    public void delArchiveByBlogId(Long blogId) {
        LambdaQueryWrapper<Archive> queryChainWrapper = new LambdaQueryWrapper<>();
        queryChainWrapper.eq(Archive::getBlogId, blogId);
        ThrowUtils.ops(archiveMapper.delete(queryChainWrapper), "删除失败！");
        deleteArchiveCache();
    }

    @Override
    public IPage<Archive> selectListByPage(Integer current, Integer limit) {
        LambdaQueryChainWrapper<Archive> queryChainWrapper = new LambdaQueryChainWrapper<>(archiveMapper);
        return queryChainWrapper.orderByDesc(Archive::getCreateTime).page(new Page<>(current, limit));
    }

    @Override
    public IPage<Archive> selectListByPage(Integer current, Integer limit, String keywords, String start, String end) {
        LambdaQueryChainWrapper<Archive> queryChainWrapper = new LambdaQueryChainWrapper<>(archiveMapper).orderByDesc(Archive::getCreateTime);
        queryChainWrapper.like(StringUtils.isNotEmpty(keywords), Archive::getTitle, keywords);
        queryChainWrapper.between(StringUtils.isNotEmpty(start) && StringUtils.isNotEmpty(end), Archive::getCreateTime, start, end);
        return queryChainWrapper.page(new Page<>(current, limit));
    }


    @Override
    public Archive findArchiveByBlogId(Long blogId) {
        return new LambdaQueryChainWrapper<>(archiveMapper).eq(Archive::getBlogId, blogId).one();
    }

    @Override
    public Archive findArchive(Archive archive) {
        if (archive.getType().equals(TYPE_1) && StringUtils.isNotNull(archive.getBlogId())) {
            return findArchiveByBlogId(archive.getBlogId());
        }
        // 如果是转载链接地址和文章url不能同时一致
        if (archive.getType().equals(TYPE_2) && StringUtils.isNotEmpty(archive.getUrl())) {
            return new LambdaQueryChainWrapper<>(archiveMapper).eq(Archive::getTitle, archive.getTitle()).eq(Archive::getUrl, archive.getUrl()).one();
        }
        return null;
    }


    @Override
    public Archive find(Long id) {
        Archive archive = new LambdaQueryChainWrapper<>(archiveMapper).eq(Archive::getBlogId, id).one();
        ThrowUtils.isNull(archive, "归档内容不存在！");
        return archive;
    }

    @Override
    public List<Archive> list() {
        List<String> strings = archiveMapper.queryTitleList();
        HashMap<String, List<Archive>> map = new HashMap<>(8);
        strings.forEach(s -> {
            List<Archive> list = new LambdaQueryChainWrapper<>(archiveMapper).eq(Archive::getArchiveTitle, s).list();
            map.put(s, list);
        });
        return new LambdaQueryChainWrapper<>(archiveMapper).orderByDesc(Archive::getCreateTime).list();
    }


    @Override
    public List<Map<String, Object>> listMap() {
        boolean b = redisService.hasKey(RedisKey.ARCHIVE_LIST);
        if (b) {
            List<Map<String, Object>> o = (List<Map<String, Object>>) redisService.get(RedisKey.ARCHIVE_LIST);
            if (o.size() != 0) {
                return o;
            }

        }
        List<Map<String, Object>> mapList = new ArrayList<>();
        List<String> strings = archiveMapper.queryTitleList();
        strings.forEach(s -> {
            Map<String, Object> map = new HashMap<>(8);
            List<Archive> list = new LambdaQueryChainWrapper<>(archiveMapper).eq(Archive::getArchiveTitle, s).list();
            map.put("list", list);
            map.put("title", s);
            mapList.add(map);
        });

        // 存入redis
        redisService.set(RedisKey.ARCHIVE_LIST, mapList,6000L);
        return mapList;
    }


    @Override
    public IPage<Archive> selectListByPage(Integer current, Integer limit, String keywords) {
        return MapperUtils.lambdaQueryWrapper(archiveMapper).orderByDesc(Archive::getCreateTime).page(new Page<>(current, limit));
    }

    @Override
    public Integer selectCount() {
        return archiveMapper.selectCount(null);
    }

    public void deleteArchiveCache() {
        redisService.del(ARCHIVE_LIST);
    }


}

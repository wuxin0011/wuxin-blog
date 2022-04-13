package com.wuxin.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wuxin.blog.pojo.Archive;
import com.wuxin.blog.mode.Base.PageService;

import java.util.List;
import java.util.Map;

/**
 * @Author: wuxin001
 * @Date: 2021/10/01/9:24
 * @Description:
 */
public interface ArchiveService extends PageService<Archive> {

    /**
     * 根据blogID删除归档
     *
     * @param blogId blogId
     */
    void delArchiveByBlogId(Long blogId);




    /**
     * 按照标题日期分类 一个月为一类
     * @return map
     */
    List<Map<String, Object>> listMap();

    /**
     * 根据blogID显示归档内容
     *
     * @param blogId blogID
     * @return DTO
     */
    Archive findArchiveByBlogId(Long blogId);


    /**
     * 根据blogID显示归档内容
     *
     * @param archive blogID
     * @return DTO
     */
    Archive findArchive(Archive archive);


    /**
     * 日期查询
     *
     * @param current
     * @param limit
     * @param keywords
     * @param start
     * @param end
     * @return
     */
    IPage<Archive> selectListByPage(Integer current, Integer limit, String keywords, String start, String end);


    /**
     * 统计收录文章总数
     */
    Integer selectCount();
}

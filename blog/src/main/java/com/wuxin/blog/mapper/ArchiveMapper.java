package com.wuxin.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wuxin.blog.pojo.Archive;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Author: wuxin001
 * @Date: 2021/10/01/10:00
 * @Description:
 */
@Mapper
public interface ArchiveMapper extends BaseMapper<Archive> {

    /**
     * 查询title类型
     * @return title list
     */
    @Select("select distinct archive_title from wx_archive order by archive_title desc")
    List<String> queryTitleList();
}


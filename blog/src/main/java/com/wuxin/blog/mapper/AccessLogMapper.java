package com.wuxin.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wuxin.blog.pojo.AccessLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @Author: wuxin001
 * @Date: 2021/12/11/17:40
 * @Description:
 */
@Mapper
public interface AccessLogMapper extends BaseMapper<AccessLog> {

    /**
     * 统计今日访问次数
     *
     * @param start 今日凌晨时间
     * @param end   当前时间
     * @return 次数
     */
    @Select("select count(distinct by_create ) from wx_access_log where  create_time>=#{start} and create_time<=#{end} ")
    Integer todayAccessCount(@Param("start") String start, @Param("end") String end);
}

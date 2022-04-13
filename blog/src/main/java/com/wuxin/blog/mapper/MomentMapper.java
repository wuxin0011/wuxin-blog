package com.wuxin.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wuxin.blog.pojo.Moment;
import com.wuxin.blog.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Author: wuxin001
 * @Date: 2021/10/03/1:40
 * @Description:
 */
@Mapper
public interface MomentMapper extends BaseMapper<Moment> {


    /**
     * 页码 获取动态列表
     * @param current 当前页
     * @param limit 大小
     * @return list
     */
    @Select("select u.nickname,u.avatar,m.* from wx_moment m left join wx_user u on m.user_id = u.user_id   order by create_time desc limit #{current},#{limit}")
    List<Moment> queryMomentPage(@Param("current") int current, @Param("limit") int limit);

}

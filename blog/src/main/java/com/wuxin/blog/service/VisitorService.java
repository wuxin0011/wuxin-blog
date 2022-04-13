package com.wuxin.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wuxin.blog.mode.PageVo;
import com.wuxin.blog.pojo.Visitor;

/**
 * @Author: wuxin001
 * @Date: 2022/03/21/10:05
 * @Description:
 */
public interface VisitorService extends IService<Visitor> {

    /**
     * 根据访客标识修改访问时间
     * @param visitor
     */
    void updateVisitorByUUID(Visitor visitor);


    /**
     * 根据uuid查询用户
     * @param uuid uuid
     * @return Visitor
     */
    Visitor queryVisitorByUUID(String uuid);


    /**
     * 分页获取用户访问信息
     * @param pageVo pagevo
     * @return
     */
    IPage<Visitor> queryVisitorPage(PageVo pageVo);
}

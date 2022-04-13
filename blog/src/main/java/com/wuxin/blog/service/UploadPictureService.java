package com.wuxin.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wuxin.blog.pojo.UploadPicture;


/**
 * @Author: wuxin001
 * @Date: 2021/10/01/9:24
 * @Description:
 */
public interface UploadPictureService  {

    /**
     * 添加一条记录
     */
    void add(UploadPicture uploadPicture);

    /**
     * 删除
     */
    void delete(Long id);


    /**
     * 分页记录
     */
    IPage<UploadPicture> selectListByPage(Integer current, Integer limit,String start,String end);


    /**
     * 获取上传类型
     */
    Integer queryType();


    /**
     * 修改上传类型
     */
    boolean updateType(Integer type);

}

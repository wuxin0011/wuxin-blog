package com.wuxin.blog.service;

import com.wuxin.blog.pojo.About;


/**
 * @Author: wuxin001
 * @Date: 2021/10/01/11:08
 * @Description:
 */
public interface AboutService {

    void updateById(About about);

    About queryById(Long id);


}

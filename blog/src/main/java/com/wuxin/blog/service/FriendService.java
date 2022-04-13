package com.wuxin.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wuxin.blog.pojo.Friend;
import com.wuxin.blog.mode.Base.PageService;
import com.wuxin.blog.pojo.FriendMessage;

/**
 * @Author: wuxin001
 * @Date: 2021/10/01/10:57
 * @Description:
 */
public interface FriendService extends PageService<Friend> {

    IPage<Friend> selectListByPage(Integer current, Integer limit,String keywords, String start, String end);

    FriendMessage findFriendMessage(Long id);

    void updateFriendMessage(FriendMessage friendMessage);


}

package com.wuxin.blog.utils.security;

import com.wuxin.blog.pojo.User;
import com.wuxin.blog.utils.string.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthorizedException;


/**
 * @Author: wuxin001
 * @Date: 2022/01/03/16:47
 * @Description: 用户安全管理工具类
 */
public class MySecurityUtils {

    private static final Long ROOT = 3L;


    /**
     * 是否为Root用户
     *
     * @return 结果
     */
    public static boolean isRoot(Long roleId) {
        return ROOT.equals(roleId);
    }

    /**
     * 是否有权限修改信息
     */
    public static boolean isPermission(Long roleId, Long userId, Long updateUserId) {
        return isRoot(roleId) || (StringUtils.isNotNull(userId) && userId.equals(updateUserId));
    }


    /**
     * 是否有权限修改信息
     */
    public static boolean isNotPermission(Long roleId, Long userId, Long updateUserId) {
        return !isPermission(roleId, userId, updateUserId);
    }

    /**
     * 获取当前登录用户信息
     *
     * @return 登录用户
     */
    public static User getUser() {
        try {
            User principal = (User) SecurityUtils.getSubject().getPrincipal();
            if (principal == null) {
                throw new UnauthorizedException("未登录！");
            }
            return principal;
        } catch (Exception e) {
            throw new UnauthorizedException("用户信息获取失败！");
        }

    }


    public static String getUsername() {

        try {
            return getUser().getUsername();
        } catch (Exception e) {
            throw new UnauthorizedException("用户名获取失败！");
        }
    }


    public static Long getUserId() {

        try {
            return getUser().getUserId();
        } catch (Exception e) {
            throw new UnauthorizedException("用户id获取失败！");
        }
    }

}

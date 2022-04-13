package com.wuxin.blog.shiro;


import com.wuxin.blog.constant.GlobalConstant;
import com.wuxin.blog.pojo.User;
import com.wuxin.blog.service.RoleService;
import com.wuxin.blog.service.UserService;
import com.wuxin.blog.utils.security.MySecurityUtils;
import com.wuxin.blog.utils.string.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;


/**
 * 自定义realm
 *
 * @author wuxin001
 */
public class UserRealm extends AuthorizingRealm {

    private static final Logger logger = LoggerFactory.getLogger(UserRealm.class);

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;


    /**
     * 授权
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        // 获取登录用户名
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        User user = MySecurityUtils.getUser();
        List<String> roleList = new ArrayList<>();
        String roleName = roleService.find(user.getRoleId()).getRoleName();
        user.setRoleName(roleName);
        roleList.add(roleName);
        logger.info("缓存中获取不到用户角色信息从数据库中获取：{}", roleList);
        // 添加权限
        info.addRoles(roleList);
        return info;
    }


    /**
     * 认证
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        // 自定义实现UsernamePasswordToken接口
        UsernamePasswordToken jwtToken = (UsernamePasswordToken) token;
        User user = userService.findUserByUsername(jwtToken.getPrincipal().toString());
        if (StringUtils.isNull(user)) {
            logger.error("用户不存在");
            throw new UnknownAccountException("用户不存在");
        }
        // if (user.getStatus().equals(0)) {
        //     logger.error("账号已锁定");
        //     throw new LockedAccountException("账号已锁定！");
        // }
        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        // shiro 中session储存用户信息
        session.setAttribute(GlobalConstant.USER_LOGIN_SESSION, user);
        // 返回密码验证
        return new SimpleAuthenticationInfo(user, user.getPassword(), getName());
    }



}

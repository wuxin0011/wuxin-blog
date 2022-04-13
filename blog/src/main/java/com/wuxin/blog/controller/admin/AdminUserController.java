package com.wuxin.blog.controller.admin;

import com.wuxin.blog.annotation.AccessLimit;
import com.wuxin.blog.annotation.OperationLogger;
import com.wuxin.blog.constant.GlobalConstant;
import com.wuxin.blog.exception.CustomException;
import com.wuxin.blog.mode.PageVo;
import com.wuxin.blog.mode.UserEmailPassword;
import com.wuxin.blog.mode.UserPass;
import com.wuxin.blog.pojo.User;
import com.wuxin.blog.redis.RedisKey;
import com.wuxin.blog.redis.RedisService;
import com.wuxin.blog.service.*;
import com.wuxin.blog.utils.result.Result;
import com.wuxin.blog.utils.result.ValidResult;
import com.wuxin.blog.utils.security.MySecurityUtils;
import com.wuxin.blog.utils.string.StringUtils;
import com.wuxin.blog.utils.validate.ValidUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: wuxin001
 * @Date: 2021/09/01/15:34
 * @Description: 用户管理
 */
@Slf4j
@RestController
@RequestMapping("/admin/user")
public class AdminUserController {

    @Autowired
    private UserService userService;

    @Autowired
    private HobbyService hobbyService;

    @Autowired
    private ChatUrlService chatUrlService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private BlogService blogService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private MailService mailService;


    @OperationLogger("获取登录用户信息")
    @GetMapping("/find/login/user/info")
    public Result getLoginUserInfo() {
        User loginUser = MySecurityUtils.getUser();
        Map<String, Object> map = new HashMap<>();
        List<String> roles = new ArrayList<>();
        roles.add(roleService.find(loginUser.getRoleId()).getRoleName());
        // 用户信息
        map.put("name", URLEncoder.encode(loginUser.getUsername()));
        map.put("nickname", URLEncoder.encode(loginUser.getNickname()));
        map.put("introduction", loginUser.getMotto());
        map.put("userId", loginUser.getUserId());
        map.put("roles", roles);
        map.put("avatar", loginUser.getAvatar());
        return Result.ok(map);

    }

    @OperationLogger("获取登录用户详情信息")
    @GetMapping("/detail")
    public Result findLoginUserDetail() {
        Long userId = MySecurityUtils.getUser().getUserId();
        Map<Object, Object> map = new HashMap<>();
        // 获取用户基本信息
        map.put("user", userService.findUserDetail(userId));
        // 获取用户用户拓展信息
        map.put("chatUrl", chatUrlService.findChatUrlByUserId(userId));
        // 获取用户爱好信息
        map.put("hobbies", hobbyService.selectListByUserId(userId));
        return Result.ok(map);
    }


    /**
     * 用户列表
     *
     * @param pageVo DTO
     * @return 成功消息
     */
    @AccessLimit(seconds = 60, limitCount = 10, msg = "操作频率过高！一分钟之后再试！")
    @OperationLogger("查看用户列表")
    @PostMapping("/list")
    public Result findUser(@RequestBody PageVo pageVo) {
        return Result.ok(userService.finUserByKeywords(pageVo));
    }

    /**
     * 用户修改信息
     *
     * @param user user
     * @return 成功消息
     */
    @OperationLogger("修改用户信息")
    @RequiresRoles(value = {"user", "root", "admin"}, logical = Logical.OR)
    @PostMapping("/update")
    public Result updateUser(@RequestBody User user) {

        User updateUser = userService.finUserById(user.getUserId());
        User loginUser = MySecurityUtils.getUser();
       
        // 判断用户是否为root用户，非root用户只能修改自己信息
        if (MySecurityUtils.isNotPermission(loginUser.getRoleId(), loginUser.getUserId(), user.getUserId())) {
            return Result.error("没有权限执行该操作！");
        }

        // 如果用户账号已经锁定
        if (!updateUser.isStatus()) {
            return Result.error("该账号已锁定，不支持修改！");
        }

        try {
            if (StringUtils.notEqual(updateUser.getUsername(), user.getUsername())) {
                if (StringUtils.isNotNull(userService.findUserByUsername(user.getUsername()))) {
                    return Result.error("昵称已经存在！");
                }

            }
            if (StringUtils.notEqual(updateUser.getNickname(), user.getNickname())) {
                if (StringUtils.isNotNull(userService.findUserByNickName(user.getNickname()))) {
                    return Result.error("昵称已经存在！");
                }
            }
            if (StringUtils.notEqual(updateUser.getEmail(), user.getEmail())) {
                if (StringUtils.isNotNull(userService.findUserByEmail(user.getEmail()))) {
                    return Result.error("邮箱已存在");
                }
            }

            if (StringUtils.isNotEmpty(user.getPhone()) && StringUtils.notEqual(user.getPhone(), updateUser.getPhone())) {
                // 用户手机号是否存在
                if (StringUtils.isNotNull(userService.finUserByPhone(user.getPhone()))) {
                    return Result.error("邮箱已存在");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("用户信息修改失败");
        }
        userService.updateUser(user);
        return Result.ok("修改成功！");
    }

    /**
     * 禁言或者解禁用户
     *
     * @param user user
     * @return 成功消息
     */
    @AccessLimit(limitCount = 10, msg = "操作频繁！请一分钟之后在尝试！", seconds = 60)
    @OperationLogger("修改用户状态")
    @RequiresRoles("root")
    @PostMapping("/update/status")
    public Result updateUserComment(@RequestBody User user) {
        userService.updateUser(user);
        return Result.ok("修改成功！");
    }

    /**
     * 删除用户信息
     *
     * @param userId 删除用户信息id
     * @return 成功消息
     */
    @OperationLogger("删除用户")
    @RequiresRoles("root")
    @DeleteMapping("/del")
    public Result delUser(@RequestParam("userId") Long userId) {
        Long loginUserId = MySecurityUtils.getUserId();
        if (!loginUserId.equals(GlobalConstant.ADMIN_USER_ID)) {
            return Result.error("删除失败,该操作仅对开发者开放！");
        }
        blogService.delBlogByUserId(userId);
        userService.delUser(userId);
        return Result.ok("删除成功！");
    }

    /**
     * 修改密码
     */
    @RequiresRoles(value = {"user", "root", "admin"}, logical = Logical.OR)
    @OperationLogger("修改密码")
    @PostMapping("/update/pass")
    public Result updatePass(@RequestBody UserPass user) {
        User loginUser = MySecurityUtils.getUser();
        if (!loginUser.isStatus()) {
            return Result.error("该账号已锁定，不支持修改！");
        }
        boolean b = userService.updatePass(loginUser.getUserId(), user);
        return ValidResult.isOk(b, "密码修改成功", "密码修改失败");
    }

    /**
     * 验证用户输入邮箱是否正确
     *
     * @param email 邮箱
     * @return message
     */
    @AccessLimit(limitCount = 3, msg = "验证码获取频繁，请一分钟之后在尝试！", seconds = 60)
    @OperationLogger("修改密码.邮箱校验")
    @RequiresRoles(value = {"user", "root", "admin"}, logical = Logical.OR)
    @GetMapping("/update/pass/valid/email")
    public Result validUserEmail(@RequestParam("email") String email) {
        User loginUser = MySecurityUtils.getUser();
        if (!loginUser.isStatus()) {
            return Result.error("该账号已锁定，不支持修改！");
        }
        if (loginUser.getEmail().equals(email)) {
            mailService.sendMimeMail(email);
            return Result.ok("验证码已发送到邮箱中了,请及时查收！");
        }
        return Result.error("邮箱校验不通过！");
    }

    /**
     * 检验验证码是否输入正确
     */
    @RequiresRoles(value = {"user", "root", "admin"}, logical = Logical.OR)
    @OperationLogger("通过邮箱获取验证码方式修改密码")
    @PostMapping("/update/pass/by/email")
    public Result validUserCode(@RequestBody UserEmailPassword user) {
        boolean hasCode = redisService.hHasKey(RedisKey.EMAIL_CODE, user.getEmail());
        if (!hasCode) {
            return Result.error("获取不到邮箱验证码！请重新获取验证码！");
        }
        String code = (String) redisService.hget(RedisKey.EMAIL_CODE, user.getEmail());
        if (!code.equals(user.getCode())) {
            return Result.error("验证失败！请重新获取验证码！！");
        }
        boolean b = userService.updatePasswordByEmail(user.getEmail(), user.getPassword());
        return ValidResult.result(b, "密码修改成功", "密码修改失败");
    }


    @RequiresRoles(value = {"user", "root", "admin"}, logical = Logical.OR)
    @OperationLogger(value = "查看了用户角色表")
    @PostMapping("/role/list")
    public Result selectUserRoleList(@RequestBody PageVo pageVo) {
        return Result.ok(userService.selectUserRoleList(pageVo));
    }


    @OperationLogger(value = "修改用户角色")
    @RequiresRoles("root")
    @PutMapping("/update/role")
    public Result updateUserRole(@RequestBody User user) {
        return ValidResult.result(userService.updateUserRole(user), "用户角色信息修改成功！", "用户角色信息修改失败！");
    }


}

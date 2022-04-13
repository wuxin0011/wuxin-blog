package com.wuxin.blog.controller.admin;

import com.wuxin.blog.annotation.AccessLimit;
import com.wuxin.blog.annotation.OperationLogger;
import com.wuxin.blog.pojo.Moment;
import com.wuxin.blog.mode.PageVo;
import com.wuxin.blog.pojo.User;
import com.wuxin.blog.service.MomentService;
import com.wuxin.blog.utils.result.Result;
import com.wuxin.blog.utils.security.MySecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Author: wuxin001
 * @Date: 2021/10/01/11:24
 * @Description: 动态
 */
@Slf4j
@RestController
@RequestMapping("/admin/moment")
public class AdminMomentController {

    @Resource
    private MomentService momentService;

    @AccessLimit(msg = "发布失败！操作频繁，请稍后再试！",limitCount = 5)
    @OperationLogger("发布一条动态")
    @RequiresRoles(value = {"user", "root", "admin"}, logical = Logical.OR)
    @PostMapping("/add")
    public Result addMoment(@RequestBody Moment moment) {
        log.info("add moment :{}", moment);
        moment.setUserId(MySecurityUtils.getUserId());
        momentService.add(moment);
        return Result.ok("添加成功!");
    }

    @OperationLogger("修改动态")
    @RequiresRoles(value = {"user", "root", "admin"}, logical = Logical.OR)
    @PostMapping("/update")
    public Result updateMoment(@RequestBody Moment moment) {
        User loginUser = MySecurityUtils.getUser();
        // 判断用户是否为root用户，非root用户只能修改自己信息
        if (MySecurityUtils.isNotPermission(loginUser.getRoleId(), loginUser.getUserId(), moment.getUserId())) {
            return Result.error("没有权限执行该操作");
        }
        momentService.update(moment);
        return Result.ok("修改成功!");
    }

    @OperationLogger("删除动态")
    @RequiresRoles(value = {"user", "root", "admin"}, logical = Logical.OR)
    @GetMapping("/del")
    public Result delMoment(@RequestParam("momentId") Long momentId,@RequestParam("userId") Long userId) {
        User loginUser = MySecurityUtils.getUser();
        // 判断用户是否为root用户，非root用户只能修改自己信息
        if (MySecurityUtils.isNotPermission(loginUser.getRoleId(), loginUser.getUserId(), userId)) {
            return Result.error("没有权限执行该操作");
        }
        momentService.delete(momentId);
        return Result.ok("删除成功!");
    }


    @AccessLimit(seconds = 60, limitCount = 10, msg = "操作频率过高！一分钟之后再试！")
    @OperationLogger("查看动态详情")
    @GetMapping("/detail")
    public Result detailMoment(@RequestParam("momentId") Long momentId) {
        return Result.ok(momentService.find(momentId));
    }


    @AccessLimit(seconds = 60, limitCount = 10, msg = "操作频率过高！一分钟之后再试！")
    @OperationLogger("查看动态列表")
    @PostMapping("/list")
    public Result findMoment(@RequestBody PageVo pageVo) {
        return Result.ok(momentService.selectListByPage
                        (
                                pageVo.getCurrent(),
                                pageVo.getLimit(),
                                pageVo.getKeywords(),
                                pageVo.getStart(),
                                pageVo.getEnd()
                        )
        );
    }


}

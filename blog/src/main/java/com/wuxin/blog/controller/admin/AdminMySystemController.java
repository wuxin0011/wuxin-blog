package com.wuxin.blog.controller.admin;

import com.wuxin.blog.annotation.AccessLimit;
import com.wuxin.blog.annotation.OperationLogger;
import com.wuxin.blog.pojo.GithubSetting;
import com.wuxin.blog.pojo.MySystem;
import com.wuxin.blog.pojo.WebFooterLabel;
import com.wuxin.blog.service.MySystemService;
import com.wuxin.blog.utils.result.Result;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: wuxin001
 * @Date: 2021/12/21/11:24
 * @Description:
 */
@RestController
@RequestMapping("/admin/system")
public class AdminMySystemController {

    @Resource
    private MySystemService mySystemService;


    @AccessLimit(seconds = 60, limitCount = 10, msg = "操作频率过高！一分钟之后再试！")
    @GetMapping("/info")
    public Result findMySystem()
    {
        return Result.ok().put("system",mySystemService.findMySystem(MySystem.SYSTEM_ID)).put("label",mySystemService.findWebFooterLabel());
    }



    @RequiresRoles("root")
    @OperationLogger("系统信息")
    @PutMapping("/update")
    public Result updateMySystem(@RequestBody MySystem mySystem) {
        mySystemService.updateMySystem(mySystem);
        return Result.ok("修改成功");
    }


    /**
     * 添加或者修改标签
     */
    @RequiresRoles("root")
    @OperationLogger("修改或者发布底部标签")
    @PutMapping("/update/footer/label")
    public Result updateWebFooterLabel(@RequestBody WebFooterLabel webFooterLabel) {

        // 根据标签id查找 如果存在就修改,不存在就添加
        List<WebFooterLabel> list = mySystemService.findWebFooterLabel(webFooterLabel.getId());
        if (list.size() == 0) {
            mySystemService.addWebFooterLabel(webFooterLabel);
            return Result.ok("标签添加成功");
        }
        if (list.size() == 1) {
            mySystemService.updateWebFooterLabel(webFooterLabel);
            return Result.ok("标签修改成功");
        } else {
            return Result.error("操作失败！");
        }

    }


    @OperationLogger("删除底部标签")
    @RequiresRoles("root")
    @DeleteMapping("/delete/footer/label")
    public Result delWebFooterLabel(@RequestParam("id") Integer id) {
        mySystemService.delWebFooterLabel(id);
        return Result.ok("标签删除成功");
    }


    @AccessLimit(seconds = 60, limitCount = 10, msg = "操作频率过高！一分钟之后再试！")
    @OperationLogger("查看仓库配置")
    @RequiresRoles("root")
    @GetMapping("/find/repo/setting")
    public Result getRepoInfo(@RequestParam("type") Integer type) {
        if( type.equals(GithubSetting.GITHUB_REPOSITORY_ID) || type.equals(GithubSetting.GITEE_REPOSITORY_ID)){
            return Result.ok(mySystemService.findRepoSetting(type));
        }
        return Result.error("请求失败，获取不到仓库类型");
    }


    @OperationLogger("修改仓库配置")
    @RequiresRoles("root")
    @PutMapping("/update/repo/setting")
    public Result updateRepoSetting(@RequestBody GithubSetting githubSetting) {
        // if (!MySecurityUtils.getUserId().equals(GlobalConstant.ADMIN_USER_ID)) {
        //     return Result.error("修改失败！该操作仅对开发者开放！");
        // }
        mySystemService.updateGithubSetting(githubSetting);
        return Result.ok("仓库配置修改成功！");
    }


}

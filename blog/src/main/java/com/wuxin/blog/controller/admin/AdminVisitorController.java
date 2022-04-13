package com.wuxin.blog.controller.admin;

import com.wuxin.blog.mode.PageVo;
import com.wuxin.blog.service.VisitorService;
import com.wuxin.blog.utils.result.Result;
import com.wuxin.blog.utils.result.ValidResult;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: wuxin001
 * @Date: 2022/01/26/17:34
 * @Description:
 */
@RequestMapping("/admin/visitor")
@RestController
public class AdminVisitorController {

    @Autowired
    private VisitorService visitorService;

    @PostMapping("/list")
    public Result queryVisitorPage(@RequestBody PageVo pageVo) {
        return Result.ok(visitorService.queryVisitorPage(pageVo));
    }

    @RequiresRoles("root")
    @DeleteMapping("/delete/{id}")
    public Result deleteVisitor(@PathVariable("id") Long id) {
        return ValidResult.result(visitorService.removeById(id), "删除成功！", "删除失败！");
    }


}

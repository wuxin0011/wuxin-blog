package com.wuxin.blog.controller.admin;

import com.wuxin.blog.annotation.AccessLimit;
import com.wuxin.blog.annotation.OperationLogger;
import com.wuxin.blog.mode.PageVo;
import com.wuxin.blog.service.ExceptionService;
import com.wuxin.blog.utils.result.Result;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: wuxin001
 * @Date: 2021/12/17/21:15
 * @Description:
 */
@RestController
@RequestMapping("/admin/exception/log")
public class AdminExceptionController {

    @Resource
    private ExceptionService exceptionService;

    @AccessLimit(seconds = 60, limitCount = 10, msg = "操作频率过高！一分钟之后再试！")
    @OperationLogger("获取异常日志信息")
    @PostMapping("/list")
    public Result findExceptionLog(@RequestBody PageVo pageVo)
    {
        return Result.ok(exceptionService.selectListByPage(pageVo.getCurrent(), pageVo.getLimit(), pageVo.getKeywords(), pageVo.getStart(), pageVo.getEnd()));
    }
    @OperationLogger("删除一条异常日志")
    @RequiresRoles(value = "root")
    @DeleteMapping("/del")
    public Result delException(@RequestParam("id") Long id)
    {
        exceptionService.delete(id);
        return Result.ok("异常日志删除成功！");
    }
    @OperationLogger("删除全部异常日志")
    @RequiresRoles(value = "root")
    @DeleteMapping("/del/all")
    public Result delAllExceptionLog()
    {
        exceptionService.deleteAll();
        return Result.ok("异常日志全部删除成功");
    }
    @OperationLogger("批量删除异常日志")
    @RequiresRoles(value = "root")
    @DeleteMapping("/del/part")
    public Result delPartExceptionLogByIds(@RequestParam("ids") List<Long> ids)
    {
        exceptionService.batchDelete();
        return Result.ok("异常日志批量删除成功！");
    }
}

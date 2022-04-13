package com.wuxin.blog.controller.admin;

import com.wuxin.blog.annotation.AccessLimit;
import com.wuxin.blog.annotation.OperationLogger;
import com.wuxin.blog.mode.PageVo;
import com.wuxin.blog.service.OperationLogService;
import com.wuxin.blog.utils.result.Result;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: wuxin001
 * @Date: 2022/01/06/21:45
 * @Description:
 */
@RestController
@RequestMapping("/admin/operation/log")
public class AdminOperationLogController {
    
    @Autowired 
    private OperationLogService operationLogService;


    @AccessLimit(seconds = 60, limitCount = 10, msg = "操作频率过高！一分钟之后再试！")
    @OperationLogger("获取操作日志列表")
    @PostMapping("/list")
    public Result findOperationLog(@RequestBody PageVo pageVo)
    {
        return Result.ok(operationLogService.selectListByPage(pageVo.getCurrent(), pageVo.getLimit(), pageVo.getKeywords(), pageVo.getStart(), pageVo.getEnd()));
    }

    @OperationLogger("删除一条操作日志")
    @RequiresRoles(value = "root")
    @DeleteMapping("/del")
    public Result delOperationLog(@RequestParam("id") Long id)
    {
        operationLogService.delete(id);
        return Result.ok("操作日志删除成功！");
    }

    @OperationLogger("删除全部操作日志")
    @RequiresRoles(value = "root")
    @DeleteMapping("/del/all")
    public Result delAllOperationLog()
    {
        operationLogService.deleteAll();
        return Result.ok("操作日志全部删除成功！");
    }

    @OperationLogger("批量删除操作日志")
    @RequiresRoles(value = "root")
    @DeleteMapping("/del/part")
    public Result delPartOperationLog(@RequestParam("ids") List<Long> ids)
    {
        operationLogService.batchDelete();
        return Result.ok("操作日志批量删除成功！");
    }
}

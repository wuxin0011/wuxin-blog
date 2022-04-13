package com.wuxin.blog.controller.admin;


import com.wuxin.blog.annotation.AccessLimit;
import com.wuxin.blog.annotation.OperationLogger;
import com.wuxin.blog.pojo.UpdateQuestion;
import com.wuxin.blog.mode.PageVo;
import com.wuxin.blog.service.UpdateQuestionService;
import com.wuxin.blog.utils.result.Result;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author Administrator
 */
@RestController
@RequestMapping("/admin/question")
public class AdminUpdateQuestionController {

    @Resource
    private UpdateQuestionService updateQuestionService;



    @AccessLimit(seconds = 60, limitCount = 10, msg = "操作频率过高！一分钟之后再试！")
    @OperationLogger("查看发布问题")
    @PostMapping("/list")
    public Result findUpdateQuestion(@RequestBody PageVo pageVo){
        return Result.ok(updateQuestionService.selectListByPage(pageVo.getCurrent(), pageVo.getLimit(), pageVo.getKeywords(),pageVo.getStart(), pageVo.getEnd()));
    }

    @RequiresRoles("root")
    @OperationLogger("添加网站更新内容")
    @PostMapping("/add")
    public Result addQuestion(@RequestBody UpdateQuestion updateQuestion){
        updateQuestionService.add(updateQuestion);
        return Result.ok("添加成功！");
    }
    @RequiresRoles("root")
    @OperationLogger("修改网站更新内容")
    @PostMapping("/update")
    public Result updateQuestion(@RequestBody UpdateQuestion updateQuestion){
        updateQuestionService.update(updateQuestion);
        return Result.ok("添加成功！");
    }


    @RequiresRoles("root")
    @OperationLogger("删除网站更新内容")
    @GetMapping("/del/{id}")
    public Result delQuestion(@PathVariable Long id){
        updateQuestionService.delete(id);
        return Result.ok("删除成功！");
    }

}

package com.wuxin.blog.controller.admin;


import com.wuxin.blog.annotation.AccessLimit;
import com.wuxin.blog.annotation.OperationLogger;
import com.wuxin.blog.mode.PageVo;
import com.wuxin.blog.service.UploadPictureService;
import com.wuxin.blog.utils.result.Result;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Administrator
 */
@RequestMapping("/admin/upload/picture")
@RestController
public class UploadPictureController {

    @Autowired
    private UploadPictureService uploadPictureService;

    @OperationLogger("查看文件上传")


    @AccessLimit(seconds = 60, limitCount = 10, msg = "操作频率过高！一分钟之后再试！")
    @PostMapping("/list")
    public Result findUploadPictureList(@RequestBody PageVo pageVo){
        return Result.ok(uploadPictureService.selectListByPage(pageVo.getCurrent(), pageVo.getLimit(),pageVo.getStart(),pageVo.getEnd()));
    }

    @OperationLogger("删除一条文件上传记录")
    @RequiresRoles("root")
    @GetMapping("/del/{id}")
    public Result delUploadPicture(@PathVariable Long id){
        uploadPictureService.delete(id);
        return Result.ok("删除成功！");
    }


    @AccessLimit(seconds = 60, limitCount = 10, msg = "操作频率过高！一分钟之后再试！")
    @GetMapping("/type")
    public Result queryType(){
        return Result.ok(uploadPictureService.queryType());
    }

    @OperationLogger("修改文件上传类型")
    @RequiresRoles("root")
    @GetMapping("/update/type")
    public Result updateType(@RequestParam("type") Integer type){
        boolean b = uploadPictureService.updateType(type);
        if(b){
            return Result.ok("修改成功！");
        }
        return Result.error("修改失败,类型不匹配！");
    }
}

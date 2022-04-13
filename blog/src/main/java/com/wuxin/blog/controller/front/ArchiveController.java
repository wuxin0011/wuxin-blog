package com.wuxin.blog.controller.front;

import com.wuxin.blog.annotation.AccessLimit;
import com.wuxin.blog.annotation.VisitLogger;
import com.wuxin.blog.service.ArchiveService;
import com.wuxin.blog.utils.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: wuxin001
 * @Date: 2021/10/01/9:19
 * @Description:
 */
@Slf4j
@RestController
@RequestMapping("/archive")
public class ArchiveController {


    @Autowired
    private ArchiveService archiveService;


    @AccessLimit(seconds = 120, limitCount = 10, msg = "操作频率过高！稍后再试！")
    @VisitLogger(value = "访问我的归档",name = "归档页")
    @GetMapping("/list")
    public Result findArchive(){
        return  Result.ok(archiveService.listMap());
    }


}

package com.wuxin.blog.controller.front;

import com.wuxin.blog.utils.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: wuxin001
 * @Date: 2022/04/11/8:21
 * @Description: 错误路径
 */
@RestController
public class ErrorPathController {

    @GetMapping("/404")
    public Result notFound() {
        return Result.create(404, "路径未找到！");
    }

    @GetMapping("")
    public Result welcome() {
        return Result.create(200, "欢迎访问wuxin-api,邮箱2191377759@qq.com,Github: https://github.com/wuxin0011/wuxin-blog");
    }

    @GetMapping("/wuxin-api")
    public Result welcome1() {
        return Result.create(200, "欢迎访问wuxin-api,邮箱2191377759@qq.com,Github: https://github.com/wuxin0011/wuxin-blog");
    }
}

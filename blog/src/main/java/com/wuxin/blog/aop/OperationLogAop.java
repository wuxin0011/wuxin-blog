package com.wuxin.blog.aop;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wuxin.blog.annotation.OperationLogger;
import com.wuxin.blog.mode.Log;
import com.wuxin.blog.pojo.OperationLog;
import com.wuxin.blog.pojo.User;
import com.wuxin.blog.service.OperationLogService;
import com.wuxin.blog.utils.logUtil.LogUtil;
import com.wuxin.blog.utils.security.MySecurityUtils;
import com.wuxin.blog.utils.security.ShiroUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @Author: wuxin001
 * @Date: 2022/03/21/9:31
 * @Description:
 */
@Component
@Aspect
public class OperationLogAop {

    // private static final Logger logger = LoggerFactory.getLogger(OperationLogAop.class);
    //
    //
    // @Autowired
    // private OperationLogService operationLogService;
    //
    // /**
    //  * 操作日志aop
    //  */
    // @Around("@annotation(operationLogger)")
    // public Object operationLogAopControllerMethod(ProceedingJoinPoint proceedingJoinPoint, OperationLogger operationLogger) throws Throwable {
    //     // 获取请求用户基本参数 用户sessionID，请求参数，
    //     try {
    //         logger.info("======================操作日志捕获中========================");
    //         OperationLog operationLog = new OperationLog();
    //         User user = MySecurityUtils.getUser();
    //         operationLog.setUsername(user.getNickname());
    //         // 获取注解信息
    //         operationLog.setDescription(operationLogger.value());
    //         // 获取基本操作信息
    //         LogUtil.setLogInfo(operationLog, proceedingJoinPoint);
    //         // 操作日志标识 用户名 方法名 ip地址 ip来源 操作系统
    //         String operationIdCard = user.getNickname() + operationLog.getMethod() + operationLog.getIp() + operationLog.getIp() + operationLog.getOs();
    //         operationLog.setByCreate(ShiroUtil.createSalt(operationIdCard));
    //         // 获取请求结果
    //         handleResult(operationLog, proceedingJoinPoint);
    //         // 添加到数据库中
    //         operationLogService.add(operationLog);
    //
    //     } catch (Throwable e) {
    //         e.printStackTrace();
    //         logger.debug("操作日志捕获失败！ ");
    //     }
    //     return proceedingJoinPoint.proceed();
    // }
    //
    //
    // public void handleResult(Log log, ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
    //     try {
    //         JSONObject jsonObject = JSONUtil.parseObj(proceedingJoinPoint.proceed());
    //         Integer code = (Integer) jsonObject.get("code");
    //         String message = (String) jsonObject.get("message");
    //         log.setCode(code);
    //         log.setResult(message);
    //     } catch (Throwable e) {
    //         e.printStackTrace();
    //         logger.debug("日志结果捕获异常");
    //     }
    // }


}

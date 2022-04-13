package com.wuxin.blog.aop;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wuxin.blog.annotation.LoginLogger;
import com.wuxin.blog.annotation.VisitLogger;
import com.wuxin.blog.constant.HttpStatus;
import com.wuxin.blog.mode.Log;
import com.wuxin.blog.mode.LoginBody;
import com.wuxin.blog.pojo.AccessLog;
import com.wuxin.blog.pojo.LoginLog;
import com.wuxin.blog.service.AccessLogService;
import com.wuxin.blog.service.LoginLogService;
import com.wuxin.blog.utils.logUtil.LogUtil;
import com.wuxin.blog.utils.security.ShiroUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: wuxin001
 * @Date: 2022/02/12/10:12
 * @Description:
 */


@Component
public class LogAop {
    //
    // private static final Logger logger = LoggerFactory.getLogger(LogAop.class);
    //
    // @Autowired
    // private LoginLogService loginLogService;
    //
    // @Autowired
    // private AccessLogService accessLogService;
    //
    //
    //
    // /**
    //  * 登录日志aop
    //  */
    // @Around("@annotation(loginLogger)")
    // public Object loginLogAopControllerMethod(ProceedingJoinPoint proceedingJoinPoint, LoginLogger loginLogger) throws Throwable {
    //     try {
    //         logger.info("==========================登录日志捕获中=======================");
    //         //获取参数
    //         LoginLog loginLog = new LoginLog();
    //         Object[] args = proceedingJoinPoint.getArgs();
    //         LoginBody loginUser = (LoginBody) args[0];
    //         // 登录用户信息
    //         loginLog.setUsername(loginUser.getUsername());
    //         // 登录其他信息 比如地点 ip 地址 浏览器型号等信息
    //         LogUtil.setLogInfo(loginLog, proceedingJoinPoint);
    //         loginLog.setDescription(loginLogger.value());
    //         // 登录结果
    //         handleResult(loginLog, proceedingJoinPoint);
    //         // 生成访问标识 用户+ip+来源+浏览器型号+操作型号
    //         String userIdCard = ShiroUtil.createSalt(loginLog.getUsername() + loginLog.getBrowser() + loginLog.getOs() + loginLog.getIp() + loginLog.getIpSource());
    //         // 用户标识
    //         loginLog.setByCreate(userIdCard);
    //         // 登录日志结果存入数据库
    //         loginLogService.add(loginLog);
    //     } catch (Throwable e) {
    //         e.printStackTrace();
    //         logger.debug("登录日志捕获异常！ ");
    //     }
    //     logger.info("用户登录日志捕获中=>{}", proceedingJoinPoint.proceed());
    //     return proceedingJoinPoint.proceed();
    // }
    //
    //
    // @Around("@annotation(visitLogger)")
    // public Object accessLogAopControllerMethod(ProceedingJoinPoint proceedingJoinPoint, VisitLogger visitLogger) throws Throwable {
    //     // 获取请求用户基本参数 用户sessionID，请求参数，
    //     try {
    //         logger.info("===========================访问日志捕获中=======================");
    //         AccessLog accessLog = new AccessLog();
    //         // 获取访问日志注解信息
    //         accessLog.setDescription(visitLogger.value());
    //         // 获取访问日志基本信息
    //         LogUtil.setLogInfo(accessLog, proceedingJoinPoint);
    //         // 添加访问日志标识 ip ip来源 os
    //         String byCreate = ShiroUtil.createSalt(accessLog.getIp() + accessLog.getIpSource() + accessLog.getOs());
    //         accessLog.setByCreate(byCreate);
    //         accessLog.setCode(HttpStatus.SUCCESS);
    //         accessLog.setResult("请求成功");
    //         //处理访问结果
    //         // handleResult(accessLog, proceedingJoinPoint);
    //         accessLogService.add(accessLog);
    //
    //     } catch (Throwable e) {
    //         e.printStackTrace();
    //         logger.debug("访问日志捕获失败！ ");
    //     }
    //     return proceedingJoinPoint.proceed();
    // }
    //
    //
    //
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

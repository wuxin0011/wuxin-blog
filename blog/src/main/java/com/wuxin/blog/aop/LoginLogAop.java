package com.wuxin.blog.aop;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wuxin.blog.annotation.LoginLogger;
import com.wuxin.blog.mode.Log;
import com.wuxin.blog.mode.LoginBody;
import com.wuxin.blog.pojo.LoginLog;
import com.wuxin.blog.pojo.Visitor;
import com.wuxin.blog.redis.RedisService;
import com.wuxin.blog.service.LoginLogService;
import com.wuxin.blog.service.VisitorService;
import com.wuxin.blog.utils.JsonFormatUtils;
import com.wuxin.blog.utils.logUtil.LogUtil;
import com.wuxin.blog.utils.security.ShiroUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @Author: wuxin001
 * @Date: 2022/03/21/9:48
 * @Description:
 */
@Aspect
@Component
public class LoginLogAop {

    private static final Logger logger = LoggerFactory.getLogger(LoginLogAop.class);

    @Autowired
    private LoginLogService loginLogService;

    @Autowired
    private VisitorService visitorService;


    @Autowired
    private RedisService redisService;

    private final static String VISITOR = "visitor";


    /**
     * 登录日志aop
     */
    @Around("@annotation(loginLogger)")
    public Object loginLogAopControllerMethod(ProceedingJoinPoint proceedingJoinPoint, LoginLogger loginLogger) throws Throwable {
        try {
            logger.info("==========================登录日志捕获中=======================");
            //获取参数
            LoginLog loginLog = new LoginLog();
            Object[] args = proceedingJoinPoint.getArgs();
            LoginBody loginUser = (LoginBody) args[0];
            // 登录用户信息
            loginLog.setUsername(loginUser.getUsername());
            // 登录其他信息 比如地点 ip 地址 浏览器型号等信息
            LogUtil.setLogInfo(loginLog, proceedingJoinPoint);
            loginLog.setDescription(loginLogger.value());
            // 登录结果
            handleResult(loginLog, proceedingJoinPoint);
            // 生成访问标识 用户+ip+来源+浏览器型号+操作型号
            String userIdCard = ShiroUtil.createSalt(loginLog.getBrowser() + loginLog.getOs() + loginLog.getIp());
            // 用户标识
            loginLog.setByCreate(userIdCard);
            // 登录日志结果存入数据库
            loginLogService.add(loginLog);
        } catch (Throwable e) {
            e.printStackTrace();
            logger.debug("登录日志捕获异常！ ");
        }
        logger.info("用户登录日志捕获中=>{}", proceedingJoinPoint.proceed());
        return proceedingJoinPoint.proceed();
    }


    // 检验访客id是否存在数据库中
    public void checkId(String uuid, Log log) {
        boolean hasKey = redisService.hHasKey(VISITOR, uuid);
        if (hasKey) {
            Object o = redisService.hget(VISITOR, uuid);
            Visitor cache = JsonFormatUtils.toObject(o, Visitor.class);
            // 更新时间
            assert cache != null;
            cache.setUpdateTime(new Date());
            // 更新 访问日志记录 获取mysql中记录 存入redis 一天
            redisService.hset(VISITOR, uuid, cache, 86400L);

        } else {
            Visitor visitor = new Visitor();
            visitor.setUuid(uuid);
            visitor.setOs(log.getOs());
            visitor.setBrowser(log.getBrowser());
            visitor.setAddress(log.getIpSource());
            visitorService.saveOrUpdate(visitor);
            redisService.hset(VISITOR, uuid, visitor, 86400L);
        }


    }

    public void add() {

    }


    public void handleResult(Log log, ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        try {
            JSONObject jsonObject = JSONUtil.parseObj(proceedingJoinPoint.proceed());
            Integer code = (Integer) jsonObject.get("code");
            String message = (String) jsonObject.get("message");
            log.setCode(code);
            log.setResult(message);
        } catch (Throwable e) {
            e.printStackTrace();
            logger.debug("日志结果捕获异常");
        }
    }
}

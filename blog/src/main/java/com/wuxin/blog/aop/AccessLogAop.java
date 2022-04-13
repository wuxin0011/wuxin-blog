package com.wuxin.blog.aop;

import com.wuxin.blog.annotation.VisitLogger;
import com.wuxin.blog.constant.HttpStatus;
import com.wuxin.blog.pojo.AccessLog;
import com.wuxin.blog.pojo.Visitor;
import com.wuxin.blog.redis.RedisService;
import com.wuxin.blog.service.AccessLogService;
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
import java.util.UUID;

/**
 * @Author: wuxin001
 * @Date: 2022/01/21/21:36
 * @Description: 访问日志注解实现，使用该注解，记录访问操作记录
 */
@Aspect
@Component
public class AccessLogAop {

    private static final Logger logger = LoggerFactory.getLogger(LogAop.class);

    @Autowired
    private AccessLogService accessLogService;

    @Autowired
    private VisitorService visitorService;

    @Autowired
    private RedisService redisService;

    private static final String ACCESS = "access:log:count";


    @Around("@annotation(visitLogger)")
    public Object accessLogAopControllerMethod(ProceedingJoinPoint proceedingJoinPoint, VisitLogger visitLogger) throws Throwable {
        // 获取请求用户基本参数 用户sessionID，请求参数，
        try {
            logger.info("===========================访问日志捕获中=======================");
            AccessLog accessLog = new AccessLog();
            // 获取访问日志注解信息
            accessLog.setDescription(visitLogger.value());
            LogUtil.setLogInfo(accessLog, proceedingJoinPoint);
            // 添加访问日志标识 ip ip来源 os
            String uuid = ShiroUtil.createSalt(accessLog.getIp() + accessLog.getIpSource() + accessLog.getOs());
            // uuid生成用户唯一标识
            // UUID uuid = UUID.fromString(accessLog.getIp() + accessLog.getIpSource() + accessLog.getOs());
            // String byCreate = uuid.toString();
            accessLog.setByCreate(uuid);
            accessLog.setCode(HttpStatus.SUCCESS);
            accessLog.setResult("请求成功");
            accessLogService.add(accessLog);
            updateVisitor(uuid,accessLog);

        } catch (Throwable e) {
            e.printStackTrace();
            logger.debug("访问日志捕获失败！ ");
        }
        return proceedingJoinPoint.proceed();
    }


    // 获取用户访问记录
    public void updateVisitor(String uuid, AccessLog accessLog) {
        boolean hasKey = redisService.hHasKey(ACCESS, uuid);
        if (hasKey) {
            // 缓存中是否存在用户信息
            Visitor visitor = JsonFormatUtils.toObject(redisService.hget(ACCESS, uuid), Visitor.class);
            if (visitor != null) {
                // 更新用户访问时间
                visitor.setUpdateTime(new Date());
                // 缓存记录添加一天
                redisService.hset(ACCESS, uuid, visitor, 86400L);
                return;
            }
        }

        // 缓存中没有用户信息从mysql中获取用户信息
        Visitor visitorByDTO = visitorService.queryVisitorByUUID(uuid);
        if (visitorByDTO != null) {
            visitorByDTO.setUpdateTime(new Date());
            // 将用户记录添加到缓存缓存
            redisService.hset(ACCESS, uuid, visitorByDTO, 86400L);
        } else {
            // 用户记录不存在 添加用户记录
            Visitor visitor1 = new Visitor();
            visitor1.setUuid(uuid);
            visitor1.setUpdateTime(new Date());
            visitor1.setAddress(accessLog.getIpSource());
            visitor1.setIp(accessLog.getIp());
            visitor1.setBrowser(accessLog.getBrowser());
            visitor1.setOs(accessLog.getOs());
            visitorService.saveOrUpdate(visitor1);
            // 添加到缓存
            redisService.hset(ACCESS, uuid, visitor1, 86400L);
        }
    }

}

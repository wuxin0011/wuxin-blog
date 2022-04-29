package com.wuxin.blog.aop;

import com.wuxin.blog.annotation.VisitLogger;
import com.wuxin.blog.constant.HttpStatus;
import com.wuxin.blog.constant.RedisKey;
import com.wuxin.blog.constant.RedisKeyExpire;
import com.wuxin.blog.mode.Log;
import com.wuxin.blog.pojo.AccessLog;
import com.wuxin.blog.pojo.Visitor;
import com.wuxin.blog.redis.RedisService;
import com.wuxin.blog.redis.impl.BlogViewsCacheService;
import com.wuxin.blog.service.AccessLogService;
import com.wuxin.blog.service.VisitorService;
import com.wuxin.blog.utils.JsonFormatUtils;
import com.wuxin.blog.utils.ip.AddressUtils;
import com.wuxin.blog.utils.ip.IpUtils;
import com.wuxin.blog.utils.logUtil.LogUtil;
import com.wuxin.blog.utils.result.ValidResult;
import com.wuxin.blog.utils.security.ShiroUtil;
import com.wuxin.blog.utils.servlet.ServletUtils;
import com.wuxin.blog.utils.string.StringUtils;
import eu.bitwalker.useragentutils.UserAgent;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @Author: wuxin001
 * @Date: 2022/01/21/21:36
 * @Description: 访问日志注解实现，使用该注解，记录访问操作记录
 */
@Aspect
@Component
public class AccessLogAop {

    private static final Logger logger = LoggerFactory.getLogger(AccessLogAop.class);

    @Autowired
    private AccessLogService accessLogService;

    @Autowired
    private VisitorService visitorService;

    @Autowired
    private RedisService redisService;


    @Autowired
    private BlogViewsCacheService viewsCacheService;

    private List<Visitor> visitorVoList;

    private static final String VISITOR_IDENTIFICATION = "identification";

    private static final String UNKNOWN = "unknown";


    @Around("@annotation(visitLogger)")
    public Object accessLogAopControllerMethod(ProceedingJoinPoint proceedingJoinPoint, VisitLogger visitLogger) throws Throwable {
        // 获取请求用户基本参数 用户sessionID，请求参数，
        try {
            logger.info("===========================访问日志捕获中=======================");
            AccessLog accessLog = new AccessLog();
            // 获取访问日志注解信息
            accessLog.setDescription(visitLogger.value());
            setLogInfo(accessLog, proceedingJoinPoint);
            // uuid生成用户唯一标识
            accessLog.setCode(HttpStatus.SUCCESS);
            accessLog.setResult("请求成功");
            accessLogService.add(accessLog);
        } catch (Throwable e) {
            e.printStackTrace();
            logger.debug("访问日志捕获失败！ ");
        }
        return proceedingJoinPoint.proceed();
    }


    public void setLogInfo(AccessLog log, ProceedingJoinPoint joinPoint) {

        logger.info("========================访问日志=============================");
        //原始的HTTP请求和响应的信息
        HttpServletRequest request = ServletUtils.getRequest();
        HttpServletResponse response = ServletUtils.getResponse();
        // 获取方法等其他参数信息
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method targetMethod = methodSignature.getMethod();
        // 获取一些基本信息 地址 浏览器 型号 userAgent 操作系统等等
        String controller = targetMethod.getDeclaringClass().getTypeName();
        String method = targetMethod.getName().toLowerCase();
        String type = request.getMethod().toLowerCase();
        log.setMethod(controller + "." + method + "()");
        log.setType(type);
        // IP地址
        String ipAddress = IpUtils.getIpAddr(request);
        // ip地址
        log.setIp(ipAddress);
        // ip来源
        log.setIpSource(getDetailInfo(AddressUtils.getRealAddressByIP(ipAddress)));
        // 获取请求头
        String header = request.getHeader("User-Agent");
        // 解析请求头
        UserAgent userAgent = UserAgent.parseUserAgentString(header);
        // 获取操作系统
        log.setOs(getDetailInfo(userAgent.getOperatingSystem()));
        // 获取浏览器型号
        log.setBrowser(getDetailInfo(userAgent.getBrowser()));
        // 获取请求来源uuid
        String uuid = validAccessUUID(request, response, log);
        // 请求标识
        log.setByCreate(uuid);
        // 获取文章访问量
        getViews(request, uuid);

    }

    /**
     * 校验uuid
     * 如果存在 刷新访问时间
     * 如果不存在 派发 UUID
     *
     * @param request
     */
    private String validAccessUUID(HttpServletRequest request, HttpServletResponse response, AccessLog accessLog) {
        String identification = request.getHeader(VISITOR_IDENTIFICATION);
        if (identification == null) {
            //请求头没有uuid，签发uuid并保存到数据库和Redis
            identification = saveUUID(response, accessLog);
        } else {
            //校验Redis中是否存在uuid  如果不存在从mysql中获取 如果mysql中不存在
            if (!uuidIsExist(identification)) {
                identification = saveUUID(response, accessLog);
            }
        }
        return identification;
    }


    /**
     * 签发UUID，并保存至数据库和Redis
     */
    private String saveUUID(HttpServletResponse response, AccessLog accessLog) {
        // 访客标识
        String uuid = ShiroUtil.createSalt(accessLog.getIp());
        //添加访客标识码UUID至响应头
        response.addHeader(VISITOR_IDENTIFICATION, uuid);
        //暴露自定义header供页面资源使用
        response.addHeader("Access-Control-Expose-Headers", VISITOR_IDENTIFICATION);

        if (!uuidIsExist(uuid)) {
            //保存至数据库
            Visitor visitor = new Visitor(null, uuid, accessLog.getIp(), accessLog.getIpSource(), accessLog.getBrowser(), accessLog.getOs(), new Date());
            visitorService.saveOrUpdate(visitor);
            // 保存redis  // 更新
            visitorVoList.add(visitor);
            updateVisitorList();
        }
        return uuid;
    }


    /**
     * 判断uuid是否存在
     */
    private boolean uuidIsExist(String uuid) {
        if (StringUtils.isEmpty(uuid)) {
            return false;
        }
        // 获取list
        getVisitorList();

        for (Visitor visitor : visitorVoList) {
            if (visitor != null && uuid.equals(visitor.getUuid())) {
                // 缓存更新
                visitor.setUpdateTime(new Date());
                updateVisitorList();
                return true;
            }
        }

        // 从mysql
        Visitor visitor = visitorService.queryVisitorByUUID(uuid);
        if (StringUtils.isNotNull(visitor) && uuid.equals(visitor.getUuid())) {
            // 添加到redis
            visitor.setUpdateTime(new Date());
            visitorVoList.add(visitor);
            // 缓存到redis
            updateVisitorList();
            return true;
        }
        return false;
    }


    /**
     * 获取list
     */
    private void getVisitorList() {
        boolean hasKey = redisService.hasKey(RedisKey.VISITOR_IDENTIFICATION);
        if (hasKey) {
            // 从redis
            this.visitorVoList = JsonFormatUtils.objectToArr(RedisKey.VISITOR_IDENTIFICATION, Visitor.class);
        } else {
            this.visitorVoList = new ArrayList<>();
        }
    }

    /**
     * 获取list
     */
    private void getBlogList() {
        boolean hasKey = redisService.hasKey(RedisKey.VISITOR_IDENTIFICATION);
        if (hasKey) {
            // 从redis
            this.visitorVoList = JsonFormatUtils.objectToArr(RedisKey.VISITOR_IDENTIFICATION, Visitor.class);
        } else {
            this.visitorVoList = new ArrayList<>();
        }
    }


    /**
     * 更新缓存Visitorlist
     */
    private void updateVisitorList() {
        redisService.set(RedisKey.VISITOR_IDENTIFICATION, this.visitorVoList, RedisKeyExpire.DAY_1);
    }

    /**
     * 更新缓存bloglist
     */
    private void updateBlogList() {
        redisService.set(RedisKey.VISITOR_IDENTIFICATION, this.visitorVoList, RedisKeyExpire.DAY_1);
    }


    public String getDetailInfo(Object info) {
        if (StringUtils.isNull(info)) {
            return UNKNOWN;
        }
        return String.valueOf(info).toUpperCase(Locale.ROOT);
    }

    // 根据uuid获取文章访问量
    public void getViews(HttpServletRequest request, String uuid) {
        String requestURI = request.getRequestURI();
        // 判断是不是访问文章路径
        if (requestURI.contains("/blog/detail")) {
            //    是文章路径 获取文章id 根据文章id获取缓存信息
            String blogId = request.getParameter("blogId");
            if (blogId != null) {
                viewsCacheService.uuidIsCache(Long.parseLong(blogId), uuid);
            }
        }


    }


}

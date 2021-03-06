package com.wuxin.blog.task;

import com.wuxin.blog.pojo.Visitor;
import com.wuxin.blog.constant.RedisKey;
import com.wuxin.blog.redis.RedisService;
import com.wuxin.blog.redis.impl.BlogViewsCacheService;
import com.wuxin.blog.service.AccessLogService;
import com.wuxin.blog.service.LoginLogService;
import com.wuxin.blog.service.VisitorService;
import com.wuxin.blog.utils.JsonFormatUtils;
import com.wuxin.blog.utils.string.StringUtils;
import com.wuxin.blog.utils.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: wuxin001
 * @Date: 2022/02/13/12:50
 * @Description: 定时任务执行 缓存统计每天访问量
 */
@Component
public class ScheduleTask {


    private static final Logger logger = LoggerFactory.getLogger(ScheduleTask.class);


    /**
     * 过期时间 14天
     */
    private static final long EXPIRE_TIME = 1209600L;

    @Autowired
    private RedisService redisService;


    @Autowired
    private AccessLogService accessLogService;

    @Autowired
    private VisitorService visitorService;

    @Autowired
    private LoginLogService loginLogService;


    @Autowired
    private BlogViewsCacheService blogViewsCacheService;


    /**
     * 将今日访问量缓存
     */
    public void saveCountTask() {
        Map<String, Object> map = new HashMap<>();
        map.put("date", DateUtils.simpleDate());
        List<Visitor> visitors;
        boolean hasKey = redisService.hasKey(RedisKey.VISITOR_IDENTIFICATION);
        if (hasKey) {
            visitors = JsonFormatUtils.objectToArr(redisService.get(RedisKey.VISITOR_IDENTIFICATION), Visitor.class);
        } else {
            visitors = new ArrayList<>();
        }
        map.put("access", visitors.size());
        map.put("login", loginLogService.selectTodayLoginLog());
        logger.info("今日访问量统计中 visit:{}", map);
        final List<Map<String, Object>> list = getAccessLoginCount();
        list.add(map);
        boolean set = redisService.set(RedisKey.ACCESS_LOGIN_COUNT, list, EXPIRE_TIME);
        if (set) {
            logger.info("今日访问量统计成功:{}", list);
        } else {
            logger.error("今日访问量统计失败！{}", list);
        }
    }


    /**
     * 获取访问量统计
     */
    public List<Map<String, Object>> getAccessLoginCount() {
        List<Map<String, Object>> list;
        boolean b = redisService.hasKey(RedisKey.ACCESS_LOGIN_COUNT);
        if (b) {
            list = (List<Map<String, Object>>) redisService.get(RedisKey.ACCESS_LOGIN_COUNT);
        } else {
            // 首次执行需要创建对象
            list = new ArrayList<>();
        }
        return list;
    }


    /**
     * 缓存中用户记录同步到数据库
     */
    public void saveCacheVisitor() {
        boolean hasKey = redisService.hasKey(RedisKey.VISITOR_IDENTIFICATION);
        if (hasKey) {
            List<Visitor> list = JsonFormatUtils.objectToArr(redisService.get(RedisKey.VISITOR_IDENTIFICATION), Visitor.class);
            for (Visitor visitor : list) {
                try {
                    visitorService.saveOrUpdate(visitor);
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error("visitor log 保存失败！e:{}", e.getMessage());
                }
            }
        }
    }

    /**
     * 缓存中用户记录同步到数据库
     */
    public void blogViewsSave() {
        blogViewsCacheService.blogViewsSave();
    }


}

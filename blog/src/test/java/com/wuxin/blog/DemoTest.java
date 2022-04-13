package com.wuxin.blog;

import cn.hutool.core.util.IdUtil;
import com.wuxin.blog.mode.TestDemo;
import com.wuxin.blog.pojo.AccessLog;
import com.wuxin.blog.pojo.OperationLog;
import com.wuxin.blog.redis.RedisKey;
import com.wuxin.blog.redis.RedisService;
import com.wuxin.blog.service.AccessLogService;
import com.wuxin.blog.service.OperationLogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @Author: wuxin001
 * @Date: 2022/03/14/13:57
 * @Description:
 */
@SpringBootTest
public class DemoTest {

//     @Autowired
//     private TestDemo demo;
//
//     @Autowired
//     private AccessLogService accessLogService;
//
//     @Autowired
//     private OperationLogService operationLogService;
//
//     @Test
//     public void test(){
//         System.out.println(demo.getName());
//         System.out.println(demo.getIp());
//     }
//
//     @Test
//     public void test1(){
//
//         // long workerId = IdUtil.getSnowflake(10L).getWorkerId(10L);
//         // long workerId1 = IdUtil.getSnowflake(10L).getWorkerId(10L);
//         // long workerId2 = IdUtil.getSnowflake(10L).getWorkerId(10L);
//         // System.out.println(workerId);
//         // System.out.println(workerId1);
//         // System.out.println(workerId2);
//         //
//
//
//         //参数1为终端ID
// //参数2为数据中心ID
// //         Snowflake snowflake = IdUtil.createSnowflake(1, 1);
// //         long id = snowflake.nextId();
//
//         long workerId = IdUtil.getSnowflake().getWorkerId(10L);
//         System.out.println(workerId);
//         long l = IdUtil.getSnowflake().nextId();
//         long l2 = IdUtil.getSnowflake().nextId();
//         long l1= IdUtil.getSnowflake().nextId();
//         System.out.println(l);
//         System.out.println(l2);
//         System.out.println(l1);
//     }
//
//
//     @Test
//     public void test2(){
//         for (int i = 0; i < 20; i++) {
//             AccessLog accessLog = new AccessLog();
//             accessLog.setId(IdUtil.getSnowflake().nextId());
//             accessLog.setBrowser("chrome");
//             accessLog.setCode(100);
//             accessLogService.add(accessLog);
//         }
//
//     }
//
//     @Test
//     public void test3(){
//         for (int i = 0; i < 20; i++) {
//             OperationLog accessLog = new OperationLog();
//             accessLog.setId(IdUtil.getSnowflake().nextId());
//             accessLog.setBrowser("chrome");
//             accessLog.setCode(100);
//             operationLogService.add(accessLog);
//         }
//
//     }


}

package com.wuxin.blog;

import com.wuxin.blog.pojo.Visitor;
import com.wuxin.blog.service.VisitorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Author: wuxin001
 * @Date: 2022/05/20/22:47
 * @Description:
 */
@SpringBootTest
public class SpringbootTest {

    @Autowired
    private VisitorService visitorService;

    @Test
    void test01(){
        // Visitor visitor = new Visitor();
        // visitor.setIp("localhost");
        // visitor.setId(12L);
        // visitor.setUuid("12321312313");
        //
        // Visitor visitor2 = new Visitor();
        // visitor2.setIp("localhost");
        // visitor2.setId(13L);
        // visitor2.setUuid("123213LL12313");
        //
        // visitorService.saveOrUpdate(visitor);
        // visitorService.saveOrUpdate(visitor2);
    }
}

package com.wuxin.blog.mode;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author: wuxin001
 * @Date: 2022/03/20/20:22
 * @Description:
 */
@Data
@Component
@ConfigurationProperties(prefix = "demo")
public class TestDemo {
    private String ip;
    private String name;
}

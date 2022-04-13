package com.wuxin.blog.config;


import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.wuxin.blog.mybaits_plus.MetaHandler;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * mybatis-plus config 参考 ：官网 https://www.mybatis-plus.com/config/#metaobjecthandler
 * @author Administrator
 */
@Configuration
public class MybatisPlusConfig {

    /**
     * 分页插件 MybatisPlusInterceptor
     * @return interceptor
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    /**
     * 注册填充策略
     * @return  GlobalConfig
     */
    @Bean
    public GlobalConfig globalconfig() {
        GlobalConfig globalconfig = new GlobalConfig();
        globalconfig.setMetaObjectHandler(new MetaHandler());
        return globalconfig;
    }

    /**
     * mapper包扫描
     * @return  mapperScannerConfigurer
     */
    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setBasePackage("com.wuxin.blog.mapper");
        return mapperScannerConfigurer;
    }

}

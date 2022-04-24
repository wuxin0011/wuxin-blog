package com.wuxin.blog.service.impl;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.wuxin.blog.constant.Constants;
import com.wuxin.blog.mapper.BlogMapper;
import com.wuxin.blog.pojo.Blog;
import com.wuxin.blog.pojo.CommentReply;
import com.wuxin.blog.constant.RedisKey;
import com.wuxin.blog.redis.RedisService;
import com.wuxin.blog.service.MailService;
import com.wuxin.blog.utils.KeyUtil;
import com.wuxin.blog.utils.ip.AddressUtils;
import com.wuxin.blog.utils.ip.IpUtils;
import com.wuxin.blog.utils.servlet.ServletUtils;
import com.wuxin.blog.utils.time.DateUtils;
import eu.bitwalker.useragentutils.UserAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


/**
 * @Author: wuxin001
 * @Date: 2021/10/01/11:08
 * @Description:
 */
@Service
public class MailServiceImpl implements MailService {

    private final static Logger logger = LoggerFactory.getLogger(MailServiceImpl.class);

    @Autowired
    JavaMailSender mailSender;


    @Value("${web.admin.url}")
    private String webAdminUrl;

    @Value("${web.url}")
    private String webUrl;


    @Autowired
    private TemplateEngine templateEngine;


    @Autowired
    private RedisService redisService;


    @Value("${spring.mail.username}")
    private String from;


    @Autowired
    private BlogMapper blogMapper;

    @Override
    public void sendMimeMail(String email) {
        String code = KeyUtil.keyUtils();
        // 缓存有效验证码10分钟
        redisService.hset(RedisKey.EMAIL_CODE, email, code, 600L);
        String subject = "验证码邮件";
        logger.info("验证码:{}", code);
        String text = "您收到的验证码是: 【" + code + " 】,有效时间为10分钟,如果非本人操作,请忽略!";
        simpleMail(email, subject, text);
    }


    @Override
    public void pubMessage(String username, String content, Integer type, Long blogId) {
        String subject = "文章新评论";
        String templatePath = "mine.html";
        String path;
        String title;
        String date = DateUtils.localTime();

        switch (type) {
            case 1:
                path = "/blog/" + blogId;
                title = new LambdaQueryChainWrapper<>(blogMapper).eq(Blog::getBlogId, blogId).select(Blog::getTitle).one().getTitle();
                break;
            case 2:
                path = "/about";
                title = "关于我";
                break;
            case 3:
                path = "/friends";
                title = "友情链接";
                break;
            default:
                path = "/error";
                title = "发送失败";
                break;
        }


        // 获取浏览器信息、IP地址ip来源等信息
        HttpServletRequest request = ServletUtils.getRequest();
        String ip = IpUtils.getIpAddr(request);
        String address = AddressUtils.getRealAddressByIP(ip);
        String header = request.getHeader("User-Agent");
        UserAgent userAgent = UserAgent.parseUserAgentString(header);
        String os = userAgent.getOperatingSystem().toString().toLowerCase();
        // logger.info("评论用户信息 ip:{},address:{},os:{}", ip, address, os);

        Map<String, Object> map = new HashMap<>(Constants.HASH_MAP_INIT);
        map.put("date", date);
        map.put("title", title);
        map.put("url", webUrl + path);
        map.put("adminUrl", webAdminUrl);
        map.put("username", username);
        map.put("content", content);
        map.put("ip", ip);
        map.put("address", address);
        map.put("os", os);
        // 邮件发送
        mailTemplateEngine("1019395329@qq.com", subject, map, templatePath);
    }


    @Override
    public void pubMessage(CommentReply commentReply, String email) {
        String subject = "你的评论有了新的回复:";
        String templatePath = "email.html";
        String path;
        String title;
        String date = DateUtils.localTime();

        switch (commentReply.getType()) {
            case 1:
                path = "/blog/" + commentReply.getBlogId();
                title = new LambdaQueryChainWrapper<>(blogMapper).eq(Blog::getBlogId, commentReply.getBlogId()).select(Blog::getTitle).one().getTitle();
                break;
            case 2:
                path = "/about";
                title = "关于我";
                break;
            case 3:
                path = "/friends";
                title = "友情链接";
                break;
            default:
                path = "/error";
                title = "发送失败";
                break;
        }


        Map<String, Object> map = new HashMap<>(Constants.HASH_MAP_INIT);
        map.put("url", webUrl + path);
        map.put("title", title);
        map.put("commentUsername", commentReply.getCommentUsername());
        map.put("commentContent", commentReply.getCommentContent());
        map.put("replyUsername", commentReply.getReplyUsername());
        map.put("replyContent", commentReply.getReplyContent());
        map.put("date", date);
        // mailTemplateEngine("1019395329@qq.com", subject, map, templatePath);
        mailTemplateEngine(email, subject, map, templatePath);
    }


    /**
     * 发送简单邮箱
     */
    public void simpleMail(String email, String subject, String content) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setSubject(subject);
            mailMessage.setText(content);
            mailMessage.setFrom(from);
            mailMessage.setTo(email);
            mailSender.send(mailMessage);
            logger.info("邮件发送成功！");
        } catch (MailException e) {
            e.printStackTrace();
            logger.error("邮箱发送失败！ 错误信息:{}", e.getMessage());
        }

    }


    /**
     * 使用模板引擎发送html模板邮件信息
     */
    public void mailTemplateEngine(String email, String subject, Map<String, Object> map, String templatePath) {

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            Context context = new Context();
            context.setVariables(map);
            String text = templateEngine.process(templatePath, context);
            messageHelper.setFrom(from);
            messageHelper.setTo(email);
            messageHelper.setSubject(subject);
            messageHelper.setText(text, true);
            // 邮件发送
            mailSender.send(mimeMessage);
            logger.info("==============邮件发送成功！==============");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("邮箱发送失败！ 错误信息:{}", e.getMessage());
        }
    }


}

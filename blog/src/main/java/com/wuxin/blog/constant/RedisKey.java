package com.wuxin.blog.constant;

import com.wuxin.blog.utils.security.ShiroUtil;
import com.wuxin.blog.utils.servlet.ServletUtils;
import com.wuxin.blog.utils.string.StringUtils;

import java.util.UUID;

/**
 * @Author: wuxin001
 * @Date: 2022/01/01/22:33
 * @Description:
 */
public class RedisKey {

    /**
     * 文章列表
     */
    public static final String BLOG_LIST = "blog_list";


    /**
     * 文章列表
     */
    public static final String ALL_BLOG_LIST = "all_blog_list";

    /**
     * 文章列表
     */
    public static final String USER_COMMENT_LIST = "usercomment_list";

    /**
     * 文章评论
     */
    public static final String COMMENT_LIST = "comment_list";


    /**
     * 文章列表
     */
    public static final String BLOG = "blog";


    /**
     * 文章列表
     */
    public static final String USER_ROLES = "user_roles";

    /**
     * 文章统计
     */
    public static final String BLOG_COUNT = "blog_count";


    /**
     * 最新文章列表
     */
    public static final String NEW_BLOG_LSIT = "new_blog_list";


    /**
     * 友情链接
     */
    public static final String FRIEND_LIST = "friend_list";

    /**
     * 关于我的内容
     */
    public static final String ABOUT = "about";


    /**
     * category 全部
     */
    public static final String CATEGORY_LIST = "category_list";

    /**
     * catetgory 统计
     */
    public static final String CATEGORY_COUNT = "category_count";


    /**
     * category 全部
     */
    public static final String TAG_LIST = "tag_list";


    /**
     * 后台首页tag统计
     */
    public static final String TAG_COUNT = "tag_count";


    /**
     * 网站底部标签
     */
    public static final String SYSTEM_FOOTER_LABEL = "system_footer_label";


    /**
     * 博主信息
     */
    public static final String USER_INFO = "user_info";


    public static final String HOBBY_INFO = "hoobby_info";


    /**
     * 博主信息
     */
    public static final String BLOGGER_INFO = "blogger_info";


    /**
     * 博主信息
     */
    public static final String CHAT_INFO = "chat_info";


    /**
     * 网站更新修改内容
     */
    public static final String UPDATE_INFO = "update_info";


    /**
     * 首页壁纸
     */
    public static final String HOME_BACKGROUND_IMAGE = "home_background_image";

    /**
     * 动态
     */
    public static final String MOMENT_LIST = "moment_list";

    /**
     * 邮箱验证码接收
     */
    public static final String EMAIL_CODE = "email_code";

    /**
     * 上传图片类型默认gitee
     */
    public static final String UPLOAD_TYPE = "upload_type";


    /**
     * 归档列表一级标题
     */
    public static final String ARCHIVE_TITLE_LIST = "archive_title_list";


    /**
     * 归档列表二级标题
     */
    public static final String ARCHIVE_LIST = "archive_list";


    public static final Long EXPIRE = 60 * 60 * 3L;


    public static final String ACCESS_LOGIN_COUNT = "access_login_count";


    public static final String USER_COMMENT_SUB = "user_comment_sub";

    public static final String SYSTEM_COUNT = "system:count";


    /**
     * 访客身份标识
     */
    public static final String VISITOR_IDENTIFICATION = "visitor_identification";


    public static String getKey(Object id) {
        return id + "_id";
    }


    public static String getKey(Object id, String keyName) {
        if (StringUtils.isEmpty(keyName)) {
            keyName = "redis_default_key";
        }
        // UUID uuid = UUID.fromString(id.toString());
        return id + "_" + keyName;
    }


    public static String getKey(Long blogId, String keyName, Integer current, Integer type) {
        if (blogId == null || blogId == 0) {
            return keyName + "_" + type + "_" + current;
        }
        return keyName + "_" + type + "_" + current + "_" + blogId;
    }

    public static String getKey(Long blogId, String keyName, Integer current, Integer limit, Integer type) {
        if (StringUtils.isEmpty(keyName)) {
            keyName = "default:name";
        }
        if (blogId == null || blogId == 0) {
            return keyName + "_" + type + "_" + current + "_" + limit;
        }
        return keyName + "_" + type + "_" + current + "_" + limit + "_" + blogId;
    }


    public static String getKey(String keyName, Integer current, Integer limit) {
        if (StringUtils.isEmpty(keyName)) {
            keyName = "default:name";
        }
        return keyName + "_" + "_" + current + "_" + limit;
    }

    public static String getKey(String keyName, Integer current, Integer limit, String keywords) {
        if (StringUtils.isEmpty(keyName)) {
            keyName = "default:name";
        }
        if (StringUtils.isEmpty(keywords)) {
            keywords = "default:keywords";
        }
        return keyName + "_" + "_" + current + "_" + limit + "_" + keywords;
    }

}

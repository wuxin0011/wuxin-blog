package com.wuxin.blog.utils.GiteeImg;

/**
 * @Author: wuxin001
 * @Date: 2021/09/16/20:45
 * @Description: https://docs.github.com/cn/rest/reference/repos#get-repository-content
 */
public class GitHubUtils {

    /**
     * 文件上传地址
     * %s =>仓库所属空间地址(企业、组织或个人的地址path)  (owner)
     * %s => 仓库路径(repo)默认上传到master分支如需其他需要指定！在 body 中
     * %s => 文件的路径(path)
     */
    public static String CREATE_REPOS_URL = "https://api.github.com/repos/%s/%s/contents/%s";

    /**
     * 访问路径 由于github访问速度较慢，使用CND加速访问
     * %s =>仓库所属空间地址(企业、组织或个人的地址path)  (owner)
     * %s => 仓库路径(repo) 如果分支不唯一，一定要加上分支！ 格式仓库名@分支名
     * %s => 文件的路径(path)文件路径名称
     */
    public static String ACCESS_URL = "https://fastly.jsdelivr.net/gh/%s/%s/%s";

}

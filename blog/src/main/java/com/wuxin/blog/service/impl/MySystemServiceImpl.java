package com.wuxin.blog.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.wuxin.blog.exception.CustomException;
import com.wuxin.blog.mapper.GithubSettingMapper;
import com.wuxin.blog.mapper.MySystemMapper;
import com.wuxin.blog.mapper.WebFooterLabelMapper;
import com.wuxin.blog.pojo.GithubSetting;
import com.wuxin.blog.pojo.MySystem;
import com.wuxin.blog.pojo.WebFooterLabel;
import com.wuxin.blog.redis.RedisKey;
import com.wuxin.blog.redis.RedisService;
import com.wuxin.blog.service.MySystemService;
import com.wuxin.blog.utils.JsonFormatUtils;
import com.wuxin.blog.utils.ThrowUtils;
import com.wuxin.blog.utils.string.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author: wuxin001
 * @Date: 2021/12/21/11:26
 * @Description:
 */
@Service
@Transactional(rollbackFor = {Exception.class})
public class MySystemServiceImpl implements MySystemService {


    @Autowired
    private MySystemMapper mySystemMapper;

    @Autowired
    private WebFooterLabelMapper webFooterLabelMapper;

    @Autowired
    private GithubSettingMapper githubSettingMapper;


    private static final String LABEL = RedisKey.SYSTEM_FOOTER_LABEL;

    private static final String SYSTEM_INFO = "system_info";

    private static final String GITHUB_REPO_INFO = "github_repo_info";

    private static final Long EXPIRE = 60 * 60 * 24 * 15L;

    @Autowired
    private RedisService redisService;

    @Override
    public MySystem findMySystem(Integer id) {
        // 从redis中获取系统信息
        boolean b = redisService.hasKey(SYSTEM_INFO);
        if (b) {
            // 判断系统信息是否为空
            MySystem mySystem = JsonFormatUtils.toObject(redisService.get(SYSTEM_INFO), MySystem.class);
            if (StringUtils.isNotNull(mySystem)) {
                // 返回从redis数据库获取的的系统信息
                return mySystem;
            }
        }
        MySystem mySystem = mySystemMapper.selectById(id);
        ThrowUtils.isNull(mySystem, "系统配置信息不存在！");
        // 将系统信息存入redis中 有效期15天
        redisService.set(SYSTEM_INFO, mySystem, EXPIRE);
        return mySystem;
    }


    @Override
    public void updateMySystem(MySystem mySystem) {
        // 判断我的系统配置信息是否存在 存在 修改,不能存在就添加
        mySystem.setId(MySystem.SYSTEM_ID);
        if (mySystemMapper.selectById(MySystem.SYSTEM_ID) == null) {
            // 添加
            System.out.println("add");
            ThrowUtils.ops(mySystemMapper.insert(mySystem), "修改失败！系统配置信息不存在！");
        } else {
            // 修改
            System.out.println("update");
            ThrowUtils.ops(mySystemMapper.updateById(mySystem), "修改失败！系统配置信息不存在！");
        }
        // 删除原来info设置
        redisService.del(SYSTEM_INFO);
    }

    @Override
    public List<WebFooterLabel> findWebFooterLabel() {
        boolean b = redisService.hasKey(LABEL);
        if (b) {
            List<WebFooterLabel> list = JsonFormatUtils.objectToArr(redisService.get(LABEL), WebFooterLabel.class);
            if (list.size() != 0) {
                return list;
            }

        }
        LambdaQueryChainWrapper<WebFooterLabel> webFooterLabelLambdaQueryChainWrapper = new LambdaQueryChainWrapper<>(webFooterLabelMapper);
        List<WebFooterLabel> list = webFooterLabelLambdaQueryChainWrapper.list();
        // 将list数据存入redis中
        redisService.set(LABEL, list, EXPIRE);
        return list;
    }

    @Override
    public List<WebFooterLabel> findWebFooterLabel(int id) {
        LambdaQueryChainWrapper<WebFooterLabel> webFooterLabelLambdaQueryChainWrapper = new LambdaQueryChainWrapper<>(webFooterLabelMapper);
        return webFooterLabelLambdaQueryChainWrapper.eq(WebFooterLabel::getId, id).list();
    }

    @Override
    public void delWebFooterLabel(Integer id) {
        deleteLabelCache();
        ThrowUtils.ops(webFooterLabelMapper.deleteById(id), "删除失败！标签不存在！");
    }

    @Override
    public void addWebFooterLabel(WebFooterLabel webFooterLabel) {
        ThrowUtils.ops(webFooterLabelMapper.insert(webFooterLabel), "添加失败！标签不存在！");
        deleteLabelCache();
    }

    @Override
    public void updateWebFooterLabel(WebFooterLabel webFooterLabel) {
        ThrowUtils.ops(webFooterLabelMapper.updateById(webFooterLabel), "添加失败！标签不存在！");
        deleteLabelCache();
    }

    @Override
    public void updateGithubSetting(GithubSetting githubSetting) {
        if (githubSetting.getType().equals(GithubSetting.GITHUB_REPOSITORY_ID) || githubSetting.getType().equals(GithubSetting.GITEE_REPOSITORY_ID)) {
            githubSetting.setId(githubSetting.getType());
            // 默认只有两个配置，如果获取不到，就添加配置信息
            if (githubSettingMapper.selectById(githubSetting.getId()) == null) {
                ThrowUtils.ops(githubSettingMapper.insert(githubSetting), "仓库配置修改失败！");
            } else {
                // 修改配置信息
                ThrowUtils.ops(githubSettingMapper.updateById(githubSetting), "仓库配置修改失败！");
            }
            // 删除缓存中仓库信息信息
            String key = RedisKey.getKey(githubSetting.getId());
            redisService.hdel(GITHUB_REPO_INFO, key);
            return;
        }
        throw new CustomException("修改失败！获取不到仓库类型");
    }

    @Override
    public GithubSetting findRepoSetting(Integer id) {
        // 检查redis中是否有respo的key
        String key = RedisKey.getKey(id);
        boolean b = redisService.hHasKey(GITHUB_REPO_INFO, key);
        if (b) {
            // 检查信息是否为空
            GithubSetting o = JsonFormatUtils.toObject(redisService.hget(GITHUB_REPO_INFO, key),GithubSetting.class);
            if (StringUtils.isNotNull(o)) {
                return o;
            }
        }
        GithubSetting githubSetting = githubSettingMapper.selectById(id);
        ThrowUtils.isNull(githubSetting, "仓库配置不能为空！");
        // 将github配置信息存入redis中
        redisService.hset(GITHUB_REPO_INFO, key, githubSetting, EXPIRE);
        return githubSetting;
    }

    private void deleteLabelCache() {
        redisService.del(LABEL);
    }
}

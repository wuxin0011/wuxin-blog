import axios from 'axios'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import {
    Message
} from 'element-ui'
import store from '@/store/index';

const request = axios.create({
    baseURL: 'https://api.github.com/',
    timeout: 30000
})

// request interceptor
request.interceptors.request.use(config => {
        NProgress.start()
        if (/get/i.test(config.method)) {
            // get请求添加时间戳防止响应缓存
            config.params = config.params || {}
            config.params.t = new Date().getTime()
        }
        // 从本地缓存中读取用户token 用户创建文件或则删除文件
        const user = store.state.upload.user
        const token = (user && user.token) ? user.token : ''
        if (token) {
            // 根据github文档要求此处必须要有空格否则会报错！
            config.headers.Authorization = `token ${token}`
        }
        return config
    },
    error => {
        console.info(error)
        return Promise.reject(error)
    }
)

request.interceptors.response.use(
    response => {
        NProgress.done()
        const {
            status
        } = response
        if (status == 200 || status === 201) {
            return response.data
        }
        Message.error('上传失败！')

    },
    error => {
        NProgress.done()
        console.log(error.message)
        Message.error(error.message)
        return Promise.reject(error.message)
    }
)

/**
 * 根据用户token获取用户信息
 */
export function getGithubUserInfo(token) {
    return request({
        url: `/user`,
        method: 'GET',
        headers: {
            Authorization: `token ${token}`
        }
    })
}

/**
 *  获取仓库列表
 */
export function getUserRepos(owner) {
    return request({
        url: `/users/${owner}/repos`,
        method: 'GET'
    })
}

/**
 * 获取用户指定仓库内容
 */
export function getReposContents(owner, repo, path) {
    return request({
        url: `/repos/${owner}/${repo}/contents/${path}`,
        method: 'GET'
    })
}


/**
 * 文件上传到github
 */
export function upload(owner, repo, path, fileName, data) {
    console.log('url=>', `https://api.github.com/repos/${owner}/${repo}/contents${path}/${fileName}`, 'branch', data.branch)
    return request({
        url: `/repos/${owner}/${repo}/contents/${path}/${fileName}`,
        method: 'PUT',
        data
    })
}
import axios from 'axios'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import {
    Message
} from 'element-ui'



const request = axios.create({
    baseURL: 'https://gitee.com/api/v5',
    timeout: 30000
})

// request interceptor
request.interceptors.request.use(config => {
        NProgress.start()
        // if (/get/i.test(config.method)) {
        //     config.params = config.params || {}
        //     config.params.t = new Date().getTime()
        // }
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
        
        return response.data
    },
    error => {
        Message.error(error.message)
        return Promise.reject(error.message)
    }
)


// 获取用户信息
export function getGiteeUserInfo(access_token) {
    return request({
        url: `/user?access_token=${access_token.trim()}`,
        method: 'GET',
    })
}

/**
 * 获取用户所有仓库列表
 */
export function getUserRepos(access_token) {
    return request({
        url: `/user/repos`,
        method: 'GET',
        params: {
            access_token
        }
    })
}

// 获取用户仓库下指定目录的文件列表
export function getReposContents(name, repos, path) {
    return request({
        url: `/repos/${name}/${repos}/contents${path}`,
        method: 'GET'
    })
}


// 上传文件至仓库指定目录下
export function upload(name, repos, path, fileName, data) {
    return request({
        url: `/repos/${name}/${repos}/contents${path}/${fileName}`,
        method: 'POST',
        data
    })
}
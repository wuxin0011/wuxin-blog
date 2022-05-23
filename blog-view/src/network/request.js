import axios from 'axios'
import {
    Message
} from "element-ui";
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
const instance = axios.create({
    baseURL: 'http://1.117.46.114:8888/wuxin-api/',
    // baseURL: 'http://wuxin:8888/wuxin-api/',
    // baseURL: 'http://localhost:8888/',
    // baseURL: '/wuxin-api',
    timeout: 30000
})

// request interceptor
instance.interceptors.request.use(config => {
        NProgress.start()
        if (/get/i.test(config.method)) {
            config.params = config.params || {}
            config.params.t = new Date().getTime()
        }
        return config
    },
    error => {
        return Promise.reject(error)
    }
)




// 响应拦截
instance.interceptors.response.use(
    response => {
        NProgress.done()
        const res = response.data
        if (res.code !== 200) {
            Message({
                message: res.message || 'Error',
                type: 'error',
                duration: 5 * 1000
            })
            return Promise.reject(new Error(res.message || 'Error'))
        } else {
            return res
        }
    },
    error => {
        NProgress.done()
        Message({
            message: error.message,
            type: 'error',
            duration: 5 * 1000
        })
        return Promise.reject(error)
    }
)


export default instance
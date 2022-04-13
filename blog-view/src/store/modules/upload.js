import {
    SET_TOKEN_TYPE,
    TOKEN_KEY_TYPE,
    TOKEN_USER_INFO
} from "../mutations-type";
import { Message } from "element-ui";
import {
    defaultSetting
} from "@/utils/setting";

import {
    getGiteeUserInfo,
} from "@/api/gitee";


import {
    getGithubUserInfo,
} from "@/api/github";

import {
    setSetStore,
    getStore,
    removeStore
} from "@/utils/session";
export const state = {

    // 用户仓库信息
    user: {
        username: '',
        token: '',
        url: '',
        avatar: '',
    },
    // 默认仓库类型为github
    tokenType: getStore(TOKEN_KEY_TYPE)?getStore(TOKEN_KEY_TYPE):'github'

}

const mutations = {

   
    // 保存token类型
    [TOKEN_KEY_TYPE]: (state, tokenType) => {
        state.tokenType = tokenType
        setSetStore(TOKEN_KEY_TYPE, tokenType)
    },

    // 保存用户信息
    [TOKEN_USER_INFO]: (state, user) => {
        state.user = user
        setSetStore(TOKEN_USER_INFO, user)
    },

}

const actions = {


    /**
     * 根据token类型从gitee或者github仓库获取用户信息
     */
    getInfo({commit}, data) {
        return new Promise((resolve, reject) => {
            console.log("JSON.data",JSON.stringify(data))
            let user = {}
            if (data.tokenType === 'github') {
                getGithubUserInfo(data.token).then(res => {
                    const {login, avatar_url, html_url} = res
                    user = {username: login, avatar: avatar_url, token: data.token, url: html_url}
                    resolve(user)
                }).catch(error => {
                    reject(error)
                })
            } else {
                getGiteeUserInfo(data.token).then(res => {
                    const {login, avatar_url, html_url} = res
                    user = {username: login, avatar: avatar_url, token: data.token.trim(), url: html_url}
                    resolve(user)
                }).catch(error => {
                    reject(error)
                })
            }
            commit(TOKEN_USER_INFO, {'user': user, 'type': data.tokenType})
            commit(TOKEN_KEY_TYPE, data.tokenType)
        })
    },

    /**
     * 获取登录用户信息
     */
    getUser({commit}, type) {
        return new Promise((resolve) => {
            console.log("JSON.type",JSON.stringify(type))
            const user = getStore(TOKEN_USER_INFO + type)
            const u = user && user.token && user.username && user.avatar ? user : {}
            commit(TOKEN_USER_INFO, {'user': user, 'type': type})
            commit(TOKEN_KEY_TYPE, type)
            resolve(u)
        })
    },

    saveUser({commit}, data) {
        return new Promise((resolve) => {
            const {user, tokenType} = data
            setSetStore(TOKEN_USER_INFO + tokenType, user)
            setSetStore(TOKEN_KEY_TYPE, tokenType)
            commit(TOKEN_USER_INFO, {'user': user, 'type': tokenType})
            commit(TOKEN_KEY_TYPE, tokenType)
            Message.success('用户配置保存成功！')
            resolve()
        }).catch(() => {
            Message.error('保存失败获取不到用户信息！')
        })
    },

    removeUser({commit}, type) {
        return new Promise((resolve) => {
            commit(TOKEN_USER_INFO, {'user': {}, 'type': type})
            commit(TOKEN_KEY_TYPE, type)
            removeStore(SET_USER + type)
            removeStore(type)
            Message.success('清空成功！')
            resolve()
        }).catch(() => {
            Message.error('清空失败获取不到用户信息！')
        })
    },
    


}


const getters = {
   
    loginUser(state) {
        return  getStore(TOKEN_USER_INFO + state.tokenType)
    },
    tokenType(state) {
        
        return state.tokenType
    }
}

export default {
    namespaced: true,
    state,
    getters,
    mutations,
    actions
}
import {
    SET_SITE_INFO,
    SET_TAG_LIST,
    SET_CATEGORY_LIST,
    SET_ADMIN_USER
} from "../mutations-type";


export const state = {

    site:{},
    categoryList:[],
    tagList:[],
    blogger:{}
}

const mutations = {

    // 标签信息
    [SET_SITE_INFO]: (state,site) => {
        state.site = site
     },


    // 标签信息
    [SET_TAG_LIST]: (state,tagList) => {
       state.tagList = tagList
    },

    // 设置分类信息
    [SET_CATEGORY_LIST]: (state,categoryList) => {
       state.categoryList = categoryList
    },

      // 设置分类信息
      [SET_ADMIN_USER]: (state,user) => {
        state.blogger = user
     },


}

export default {
    namespaced: true,
    state,
    mutations,
}
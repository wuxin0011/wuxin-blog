import request from "../network/request";

/**
 * 获取blogList
 * @param {页码} current 
 * @returns 
 */
export function getBlogList(current) {
    return request({
        url: '/blog/list',
       method: 'GET',
        params:{
            current:current,
            limit:5
        }
    })
}

/**
 * 获取详情
 * @param {id} blogId 
 * @returns 
 */
export function getDetailBlog(blogId) {
    return request({
        url: '/blog/detail',
       method: 'GET',
        params:{
            blogId
        }
    })
}


/**
 * 获取博客赞赏图片
 * @return getChatUrl
 */
 export function getChatUrl(userId) {
    return request({
        url: '/chat/url/find',
       method: 'GET',
        params:{
            userId
        }
    })
}

/**
 * 上一篇
 * @return before
 */
 export function beforeBlog(blogId) {
    return request({
        url: '/blog/before',
       method: 'GET',
        params:{
            blogId
        }
    })
}


/**
 * 下一篇
 * @return next
 */
 export function nextBlog(blogId) {
    return request({
        url: '/blog/next',
       method: 'GET',
        params:{
            blogId
        }
    })
}


/**
 * 搜索
 * @return next
 */
export function searchBlog(keywords) {
    return request({
        url: '/blog/keywords',
       method: 'GET',
        params:{
            keywords
        }
    })
}
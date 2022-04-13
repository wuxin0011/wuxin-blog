import request from "../network/request";


/**
 * 根据tagName获取该标签下的blogList
 */
export function getBlogListByTagName(current,name) {
    return request({
        url: '/tag/blog/list',
        method: 'GET',
        params:{
            current,name
        }
    })
}



/**
 * 根据cateogryName获取该标签下的blogList
 * @param {pageVo} data
 * @returns 
 */
export function getBlogByCategoryName(current,name) {
    return request({
        url: '/category/blog/list',
        method: 'GET',
        params:{
            current,name
        }
    })
}
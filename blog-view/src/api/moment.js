import request from "@/network/request";


/**
 * 获取动态列表
 */
export function getMomentList(current, limit) {
    return request({
        url: `/moment/list/${current}/${limit}`,
       method: 'GET',

    })
}

/**
 * 点赞
 */
export function likeMoment(data) {
    return request({
        url: `/moment/like`,
        method: 'POST',
        data
    })
}


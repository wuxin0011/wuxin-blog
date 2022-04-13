import request from '@/utils/request'


/**
 * 修改关于我的内容
 * @param data
 * @returns {AxiosPromise}
 */
export function updateChatUrl(data) {
  return request({
    url: '/admin/chat/url/update',
    method: 'POST',
    data
  })
}





/**
 * 修改关于我的内容
 * @param data
 * @returns {AxiosPromise}
 */
export function findChatUrl(userId) {
  return request({
    url: '/admin/chat/url/find',
   method: 'GET',
    params:{
      userId
    }
  })
}



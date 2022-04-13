import request from '@/utils/request'


/**
 * 修改关于我的内容
 * @param data
 * @returns {AxiosPromise}
 */
export function updateAbout(data) {
  return request({
    url: '/admin/about/update',
   method: 'PUT',
    data
  })
}

/**
 * 显示关于我的内容
 * @returns {AxiosPromise}
 */
export function findAbout() {
  return request({
    url: '/about/detail',
   method: 'GET',
  })
}


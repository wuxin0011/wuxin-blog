import request from '@/utils/request'
// import axios from "axios";


/**
 * 获取动态列表
 * @param data
 * @returns {AxiosPromise}
 */
export function getMomentList(data) {
  return request({
    url: '/admin/moment/list',
    method: 'POST',
    data
  })
}

/**
 * 修改动态内容
 * @param data
 * @returns {AxiosPromise}
 */
export function updateMoment(data) {
  return request({
    url: '/admin/moment/update',
    method: 'POST',
    data
  })
}

/**
 * 添加动态
 * @param data
 * @returns {AxiosPromise}
 */
export function createMoment(data) {
  return request({
    url: '/admin/moment/add',
    method: 'POST',
    data
  })
}

/**
 * 删除动态
 * @param data
 * @returns {AxiosPromise}
 */
export function delMoment(momentId) {
  return request({
    url: '/admin/moment/del',
   method: 'GET',
    params: {
      momentId
    }
  })
}

/**
 * 获取动态详情
 * @param momentId
 * @returns {AxiosPromise}
 */
export function detailMoment(momentId) {
  return request({
    url: '/admin/moment/detail',
   method: 'GET',
    params: {
      momentId
    }
  })
}


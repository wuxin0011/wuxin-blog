import request from '@/utils/request'

/**
 * 获取动态列表
 * @returns {list}
 */
export function getBackgroundMap() {
  return request({
    url: '/background/list',
    method: 'get'
  })
}

/**
 * 获取动态列表
 * @param id
 * @returns {msg}
 */
export function delBackgroundMap(id) {
  return request({
    url: '/background/del',
   method: 'DELETE',
    params: {
      id
    }
  })
}

/**
 * 获取系统信息
 * @returns {info}
 */
export function getMySystemInfo() {
  return request({
    url: '/admin/system/info',
    method: 'get'
  })
}

/**
 * 修改系统信息
 * @param data data
 * @returns {message} success message
 */
export function updateMySystemInfo(data) {
  return request({
    url: '/admin/system/update',
   method: 'PUT',
    data
  })
}

/**
 * 获取系统底部标签
 * @param data data
 * @returns {message} success message
 */
export function getWebFooterLabel() {
  return request({
    url: '/system/find/footer/label',
    method: 'get'

  })
}

/**
 * 删除系统底部标签
 * @param id id
 * @returns {message} success message
 */
export function delWebFooterLabel(id) {
  return request({
    url: '/admin/system/delete/footer/label',
   method: 'DELETE',
    params: {
      id
    }
  })
}

/**
 * 添加或者修改系统底部标签
 * @param data data
 * @returns {message} success message
 */
export function updateWebFooterLabel(data) {
  return request({
    url: '/admin/system/update/footer/label',
   method: 'PUT',
    data
  })
}

/**
 * 修改github配置
 * @param data data
 * @returns {message} success message
 */
export function updateRepoSetting(data) {
  return request({
    url: '/admin/system/update/repo/setting',
   method: 'PUT',
    data
  })
}

/**
 * 查看仓库信息
 */
export function getRepoSettingInfo(type) {
  return request({
    url: '/admin/system/find/repo/setting',
   method: 'GET',
    params: {
      type
    }
  })
}

/**
 * 查看github操作
 * @param data data
 * @returns {message} success message
 */
export function getGithubRecords(url) {
  return request({
    url: '/github/records',
   method: 'GET',
    params: {
      url
    }
  })
}
/**
 * 查看github操作
 * @param data data
 * @returns {message} success message
 */
export function deleteGithubRecords(data) {
  return request({
    url: '/github/delete/records',
    method: 'POST',
    data
  })
}

export function getUploadFileList(data) {
  return request({
    url: '/admin/upload/picture/list',
    method: 'POST',
    data
  })
}

export function delPicture(id) {
  return request({
    url: '/admin/upload/picture/del/' + id,
    method: 'get'
  })
}

export function getUploadType() {
  return request({
    url: '/admin/upload/picture/type',
    method: 'get'
  })
}

export function updateUploadType(type) {
  return request({
    url: '/admin/upload/picture/update/type',
   method: 'GET',
    params: {
      type
    }
  })
}

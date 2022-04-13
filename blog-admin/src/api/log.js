import request from '@/utils/request'

/**
 * 获取登录日志
 * @param data
 * @returns {message}
 */
export function getLoginLogList(data){
  return request({
    url: '/admin/login/log/list',
    method: 'POST',
    data
  })
}

/**
 * 删除登录日志
 * @param id
 * @returns {message}
 */
export function delLoginLog(id){
  return request({
    url:`/admin/login/log/del`,
   method: 'DELETE',
    params:{
      id
    }
  })
}


/**
 * 全部删除登录日志
 * @returns {message}
 */
export function delAllLoginLog(){
  return request({
    url: '/admin/login/log/del/all',
   method: 'DELETE',
  })
}


/**
 * 获取异常日志
 * @param data
 * @returns {message}
 */
export function getExceptionLogList(data){
  return request({
    url: '/admin/exception/log/list',
    method: 'POST',
    data
  })
}


/**
 * 删除异常日志
 * @param id
 * @returns {msg}
 */
export function delExceptionLog(id){
  return request({
    url: '/admin/exception/log/del',
   method: 'DELETE',
    params:{
      id
    }
  })
}


/**
 * 删除全部异常日志
 * @returns {msg}
 */
export function delAllExceptionLog(){
  return request({
    url: '/admin/exception/log/del/all',
   method: 'DELETE',
  })
}



/*===========================================operation===========================================*/



/**
 * 删除全部请求日志
 * @returns {message}
 */
export function delOperationLog(id){
  return request({
    url: '/admin/operation/log/del',
   method: 'DELETE',
    params:{
      id
    }
  })
}


/**
 * 删除全部请求日志
 * @returns {message}
 */
export function delAllOperationLog(){
  return request({
    url: '/admin/operation/log/del/all',
   method: 'DELETE',
  })
}


/**
 * 删除全部请求日志
 * @returns {message}
 */
export function delPartOperationLog(ids){
  return request({
    url: '/admin/operation/log/del/part',
   method: 'DELETE',
    params:{
      ids
    }
  })
}
/**
 * 删除全部请求日志
 * @returns {message}
 */
export function getOperationLogList(data){
  return request({
    url: '/admin/operation/log/list',
    method: 'POST',
    data
  })
}


/*===========================================AccessLog===========================================*/




/**
 * 删除全部请求日志
 * @returns {message}
 */
export function delAccessLog(id){
  return request({
    url: '/admin/access/log/del',
   method: 'DELETE',
    params:{
      id
    }
  })
}


/**
 * 删除全部请求日志
 * @returns {message}
 */
export function delAllAccessLog(){
  return request({
    url: '/admin/access/log/del/all',
   method: 'DELETE',
  })
}


/**
 * 删除全部请求日志
 * @returns {message}
 */
export function delPartAccessLog(ids){
  return request({
    url: '/admin/access/log/del/part',
   method: 'DELETE',
    params:{ids}
  })
}
/**
 * 删除全部请求日志
 * @returns {message}
 */
export function getAccessLogList(data){
  return request({
    url: '/admin/access/log/list',
    method: 'POST',
    data
  })
}


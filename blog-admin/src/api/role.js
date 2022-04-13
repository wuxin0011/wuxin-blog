import request from '@/utils/request'


export function getRoleList(data) {
  return request({
    url: '/admin/role/list',
    method: 'POST',
    data
  })
}


export function getUserRoleList(data) {
  return request({
    url: '/admin/user/role/list',
    method: 'POST',
    data
  })
}


export function updateUserRole(data) {
  return request({
    url: '/admin/user/update/role',
    method: 'PUT',
    data
  })
}

export function updateUserStatus(data) {
  return request({
    url: '/admin/user/update/status',
    method: 'POST',
    data
  })
}

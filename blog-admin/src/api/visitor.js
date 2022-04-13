import request from '@/utils/request'



export function getVisitorList(data) {
  return request({
    url: `/admin/visitor/list`,
    method: 'POST',
    data
  })
}



export function deleteVisitorById(id) {
  return request({
    url: `/admin/visitor/delete/${id}`,
    method: 'DELETE',
  })
}

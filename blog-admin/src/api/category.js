import request from '@/utils/request'


// categoryList()
export function getCategoryList() {
  return request({
    url: '/category/list',
   method: 'GET',
  })
}


/**
 * 分类
 * @param data
 * @returns {AxiosPromise}
 */
export function getCategoryListPage(data) {
  return request({
    url: '/admin/category/list',
    method: 'POST',
    data
  })
}


/**
 * 删除分类
 * @param cid
 * @returns {AxiosPromise}
 */
export function delCategory(cid) {
  return request({
    url: `/admin/category/del/${cid}`,
   method: 'DELETE',
  })
}


export function updateCategory(data) {
  return request({
    url: '/admin/category/update',
   method: 'PUT',
    data
  })
}


export function createCategory(data) {
  return request({
    url: '/admin/category/add',
    method: 'POST',
    data
  })
}


export function updateCategoryColor(data) {
  return request({
    url: '/admin/category/update/color',
   method: 'PUT',
    data
  })
}










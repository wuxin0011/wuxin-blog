import request from '@/utils/request'



/**
 * 获取blog
 * @param id
 * @returns {AxiosPromise}
 */
export function getBlogList(data) {
  return request({
    url: '/admin/blog/list',
    method: 'POST',
    data
  })
}

/**
 * 删除blog
 */
export function delBlog(blogId) {
  return request({
    url: '/admin/blog/del/',
   method: 'DELETE',
    params:{
      blogId
    }
  })
}


/**
 * 添加blog
 * @param data
 * @returns {AxiosPromise}
 */
export function createBlog(data) {
  
  return request({
    url: '/admin/blog/add',
    method: 'POST',
    data
  })
}

/**
 * 显示详情
 */
export function blogDetail(blogId) {
  return request({
    url: '/blog/detail',
   method: 'GET',
    params:{
      blogId
    }
  })
}

/**
 * 修改blog
 * @param data
 * @returns {AxiosPromise}
 */
export function updateBlog(data) {
  return request({
    url: '/admin/blog/update',
   method: 'PUT',
    data
  })
}




// 统计首页blog点击量
export function getAllBlogCount() {
  return request({
    url: '/admin/blog/count/blog',
   method: 'GET',
  })
}

// 图片上传
export function uploadBlogImg(data) {
  return request({
    url: '/admin/upload/blog/img',
    method: 'POST',
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    data
  })
}


export function getBlogComment(current, limit, blogId) {
  return request({
    url: `/admin/blog/comment/this/${current}/${limit}/${blogId}`,
   method: 'GET',
  })
}

export function getBlogCommentList(data) {
  return request({
    url: '/admin/blog/comment/list',
    method: 'POST',
    data
  })
}


export function delBlogComment(id) {
  return request({
    url: `/admin/blog/comment/del/${id}`,
   method: 'GET',
  })
}


export function delBlogCommentReply(id) {
  return request({
    url: `/admin/blog/comment/del/reply/${id}`,
   method: 'GET',
  })
}

import request from '@/utils/request'



export function getUpdateQuestionList(data) {
  return request({
    url: `/admin/question/list`,
    method: 'POST',
    data
  })
}

export function createQuestion(data){
  return request({
    url: `/admin/question/add`,
    method: 'POST',
    data
  })
}


export function updateQuestion(data){
  return request({
    url: `/admin/question/update`,
    method: 'POST',
    data
  })
}


export function delQuestion(id){
  return request({
    url: `/admin/question/del/${id}`,
   method: 'GET',
  })
}

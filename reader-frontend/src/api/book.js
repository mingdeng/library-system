import request from './request'

/**
 * 获取图书列表
 */
export const getBookList = (params) => {
  return request({
    url: '/reader/books',
    method: 'get',
    params
  })
}

/**
 * 获取图书详情
 */
export const getBookDetail = (id) => {
  return request({
    url: `/reader/books/${id}`,
    method: 'get'
  })
}


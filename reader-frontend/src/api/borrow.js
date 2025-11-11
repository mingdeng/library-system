import request from './request'

/**
 * 借阅图书
 */
export const borrowBook = (data) => {
  return request({
    url: '/reader/borrows',
    method: 'post',
    params: data
  })
}

/**
 * 续借图书
 */
export const renewBook = (data) => {
  return request({
    url: '/reader/borrows/renew',
    method: 'post',
    params: data
  })
}

/**
 * 获取我的借阅列表
 */
export const getBorrowList = (userId) => {
  return request({
    url: '/reader/borrows',
    method: 'get',
    params: { userId }
  })
}


import request from './request'

/**
 * 读者登录
 */
export const login = (data) => {
  return request({
    url: '/reader/login',
    method: 'post',
    data
  })
}


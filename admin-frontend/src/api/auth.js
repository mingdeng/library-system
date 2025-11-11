import request from './request'

/**
 * 管理员登录
 */
export const login = (data) => {
  return request({
    url: '/admin/login',
    method: 'post',
    data
  })
}


import request from './request'

/**
 * 获取用户列表
 */
export const getUserList = (params) => {
  return request({
    url: '/admin/users',
    method: 'get',
    params
  })
}

/**
 * 获取用户详情
 */
export const getUserDetail = (id) => {
  return request({
    url: `/admin/users/${id}`,
    method: 'get'
  })
}

/**
 * 新增用户
 */
export const addUser = (data) => {
  return request({
    url: '/admin/users',
    method: 'post',
    data
  })
}

/**
 * 更新用户
 */
export const updateUser = (id, data) => {
  return request({
    url: `/admin/users/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除用户
 */
export const deleteUser = (id) => {
  return request({
    url: `/admin/users/${id}`,
    method: 'delete'
  })
}


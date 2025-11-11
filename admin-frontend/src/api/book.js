import request from './request'

/**
 * 获取图书列表
 */
export const getBookList = (params) => {
  return request({
    url: '/admin/books',
    method: 'get',
    params
  })
}

/**
 * 获取图书详情
 */
export const getBookDetail = (id) => {
  return request({
    url: `/admin/books/${id}`,
    method: 'get'
  })
}

/**
 * 新增图书
 */
export const addBook = (data) => {
  return request({
    url: '/admin/books',
    method: 'post',
    data
  })
}

/**
 * 更新图书
 */
export const updateBook = (id, data) => {
  return request({
    url: `/admin/books/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除图书
 */
export const deleteBook = (id) => {
  return request({
    url: `/admin/books/${id}`,
    method: 'delete'
  })
}

/**
 * 上传图书封面
 * 后端接口: /api/admin/books/upload-cover
 * 接收 form-data，字段名为 file
 */
export const uploadCover = (file, bookId = '') => {
  // debugger
  const form = new FormData()
  // 后端要求的字段名为 bookId 和 cover
  form.append('bookId', bookId)
  form.append('cover', file)
  return request({
    url: '/admin/books/upload-cover',
    method: 'post',
    data: form,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}


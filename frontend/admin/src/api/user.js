import request from './request'

export function getUserList(params) {
  return request.get('/api/user/admin/list', { params })
}
export function updateUserStatus(id, status) {
  return request.put(`/api/user/admin/${id}/status`, { status })
}

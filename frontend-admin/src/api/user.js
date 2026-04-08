import request from './request'

export function getUsers(params) {
  return request.get('/users', { params })
}

export function updateUserStatus(id, status) {
  return request.put(`/users/${id}/status`, null, { params: { status } })
}

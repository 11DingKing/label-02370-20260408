import request from './request'

export function borrowBook(data) {
  return request.post('/borrows', data)
}

export function returnBook(id) {
  return request.put(`/borrows/${id}/return`)
}

export function confirmReturn(id) {
  return request.put(`/borrows/${id}/confirm`)
}

export function getCurrentBorrows(params) {
  return request.get('/borrows/current', { params })
}

export function getBorrowHistory(params) {
  return request.get('/borrows/history', { params })
}

export function getAllBorrows(params) {
  return request.get('/borrows/all', { params })
}

import request from './request'

export function getOrderList(params) {
  return request.get('/api/order/admin/list', { params })
}

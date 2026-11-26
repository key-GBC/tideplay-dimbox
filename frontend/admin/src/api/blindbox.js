import request from './request'

export function getSeriesList(params) {
  return request.get('/api/promotion/blindbox/admin/series/list', { params })
}

export function getSeriesDetail(id) {
  return request.get(`/api/promotion/blindbox/admin/series/${id}`)
}

export function createSeries(data) {
  return request.post('/api/promotion/blindbox/series', data)
}

export function updateSeries(id, data) {
  return request.put(`/api/promotion/blindbox/series/${id}`, data)
}

export function createItem(seriesId, data) {
  return request.post(`/api/promotion/blindbox/series/${seriesId}/item`, data)
}

export function updateItem(itemId, data) {
  return request.put(`/api/promotion/blindbox/item/${itemId}`, data)
}

export function deleteItem(itemId) {
  return request.delete(`/api/promotion/blindbox/item/${itemId}`)
}

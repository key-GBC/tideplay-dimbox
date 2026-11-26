import request from './request'

export function getSeriesList(params) {
  return request.get('/api/promotion/blindbox/series/list', { params })
}

export function getSeriesDetail(id) {
  return request.get(`/api/promotion/blindbox/${id}`)
}

export function drawBlindbox(seriesId) {
  return request.post(`/api/promotion/blindbox/${seriesId}/draw`)
}

export function getDrawRecord(params) {
  return request.get('/api/promotion/blindbox/record', { params })
}

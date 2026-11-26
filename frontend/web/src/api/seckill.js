import request from './request'

export function getSeckillActivityList(params) {
  return request.get('/api/promotion/seckill/activity', { params })
}

export function buySeckill(activityId) {
  return request.post(`/api/promotion/seckill/${activityId}/buy`)
}

export function getSeckillRecord(params) {
  return request.get('/api/promotion/seckill/record', { params })
}

import request from './request'

export function getSeckillActivityList(params) {
  return request.get('/api/promotion/seckill/activity', { params })
}
export function addSeckillActivity(data) {
  return request.post('/api/promotion/seckill/activity', data)
}
export function warmupSeckillActivity(id) {
  return request.post(`/api/promotion/seckill/activity/${id}/warmup`)
}

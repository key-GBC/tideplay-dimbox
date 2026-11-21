import request from './request';

export function createOrder(data) {
    return request.post('/api/order', data)
}
export function pageOrderList(params) {
    return request.get('/api/order', {params})
}
export function getOrderDetail(id) {
    return request.get(`/api/order/${id}`)
}
export function cancelOrder(id) {
    return request.post(`/api/order/${id}/cancel`)
}

import request from './request'

export function createPayment(data) {
    return request.post('/api/payment/create', data)
}
export function pay(paymentNo) {
    return request.post(`/api/payment/${paymentNo}/pay`)
}
export function callback() {
    return request.post('/api/payment/callback')
}
export function queryPayment(paymentNo) {
    return request.get(`/api/payment/${paymentNo}`)
}
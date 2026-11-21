import request from './request'

export function register(data) {
    return request.post('/api/user/register', data)
}
export function login(data) {
    return request.post('/api/user/login', data)
}
export function health() {
    return request.get('/api/user/health')
}
export function getMe() {
    return request.get('/api/user/me')
}
export function profile() {
    return request.get('/api/user/profile')
}
export function updateProfile(data) {
    return request.put('/api/user/profile', data)
}
export function changePassword(data) {
    return request.put('/api/user/password', data)
}
export function addressList() {
    return request.get('/api/user/address')
}
export function addAddress(data) {
    return request.post('/api/user/address', data)
}
export function updateAddress(data) {
    return request.put(`/api/user/address/${data.id}`, data)
}
export function deleteAddress(id) {
    return request.delete(`/api/user/address/${id}`)
}
export function setDefaultAddress(id) {
    return request.put(`/api/user/address/${id}/default`)
}

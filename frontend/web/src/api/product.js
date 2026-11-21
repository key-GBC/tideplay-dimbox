import request from './request'

export function getCategoryTree() {
    return request.get('/api/product/category/tree')
}
export function getBrandList() {
    return request.get('/api/product/brand/list')
}
export function getProductList(params) {
    return request.get('/api/product/list', { params })
}
export function getProductDetail(id) {
    return request.get(`/api/product/${id}`)
}

import request from './request'

export function getCategoryTree() {
  return request.get('/api/product/category/tree')
}
export function getAdminCategoryTree() {
  return request.get('/api/product/category/admin-tree')
}
export function addCategory(data) {
  return request.post('/api/product/category', data)
}
export function updateCategory(id, data) {
  return request.put(`/api/product/category/${id}`, data)
}
export function deleteCategory(id) {
  return request.delete(`/api/product/category/${id}`)
}

export function getBrandList() {
  return request.get('/api/product/brand/list')
}
export function addBrand(data) {
  return request.post('/api/product/brand', data)
}
export function updateBrand(id, data) {
  return request.put(`/api/product/brand/${id}`, data)
}
export function deleteBrand(id) {
  return request.delete(`/api/product/brand/${id}`)
}

export function getProductList(params) {
  return request.get('/api/product/list', { params })
}
export function getAdminProductList(params) {
  return request.get('/api/product/admin/list', { params })
}
export function getProductDetail(id) {
  return request.get(`/api/product/${id}`)
}
export function getAdminProductDetail(id) {
  return request.get(`/api/product/admin/${id}`)
}
export function addProduct(data) {
  return request.post('/api/product', data)
}
export function updateProduct(id, data) {
  return request.put(`/api/product/${id}`, data)
}
export function updateProductStatus(id, status) {
  return request.put(`/api/product/${id}/status`, { status })
}
export function deleteProduct(id) {
  return request.delete(`/api/product/${id}`)
}
export function addSku(productId, data) {
  return request.post(`/api/product/${productId}/sku`, data)
}
export function updateSku(skuId, data) {
  return request.put(`/api/product/sku/${skuId}`, data)
}
export function updateSkuStatus(skuId, status) {
  return request.put(`/api/product/sku/${skuId}/status`, { status })
}
export function deleteSku(skuId) {
  return request.delete(`/api/product/sku/${skuId}`)
}

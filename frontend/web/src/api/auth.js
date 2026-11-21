import request from './request'

export function register(data) {
  return request.post('/api/user/register', data)
}

export function login(data) {
  return request.post('/api/user/login', data)
}

export function getMe() {
  return request.get('/api/user/me')
}

import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'
import { useUserStore } from '../stores/user'

const request = axios.create({
  baseURL: '',
  timeout: 10000,
})

request.interceptors.request.use((config) => {
  const userStore = useUserStore()
  if (userStore.token) {
    config.headers.Authorization = `Bearer ${userStore.token}`
  }
  return config
})

request.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res && res.code === 200) {
      return res.data
    }
    const message = (res && res.message) || '请求失败'
    ElMessage.error(message)
    return Promise.reject(new Error(message))
  },
  (error) => {
    if (error.response) {
      const status = error.response.status
      const message = (error.response.data && error.response.data.message) || error.message || '网络异常'
      if (status === 401) {
        const userStore = useUserStore()
        userStore.logout()
        ElMessage.error(message)
        router.push({ name: 'login' })
      } else {
        ElMessage.error(message)
      }
    } else {
      ElMessage.error(error.message || '网络异常')
    }
    return Promise.reject(error)
  }
)

export default request

import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('admin_token') || '')
  const user = ref(JSON.parse(localStorage.getItem('admin_user') || 'null'))

  function setLogin(tokenVal, userVal) {
    token.value = tokenVal
    user.value = userVal
    localStorage.setItem('admin_token', tokenVal)
    localStorage.setItem('admin_user', JSON.stringify(userVal))
  }

  function logout() {
    token.value = ''
    user.value = null
    localStorage.removeItem('admin_token')
    localStorage.removeItem('admin_user')
  }

  const isAdmin = () => !!user.value && user.value.role === 1

  return { token, user, setLogin, logout, isAdmin }
})

import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../stores/user'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/login', name: 'login', component: () => import('../views/Login.vue') },
    {
      path: '/',
      component: () => import('../layout/AdminLayout.vue'),
      meta: { requiresAuth: true },
      children: [
        { path: '', redirect: '/product' },
        { path: 'product', name: 'product', component: () => import('../views/ProductManage.vue') },
        { path: 'category', name: 'category', component: () => import('../views/CategoryManage.vue') },
        { path: 'brand', name: 'brand', component: () => import('../views/BrandManage.vue') },
        { path: 'seckill', name: 'seckill', component: () => import('../views/SeckillActivityManage.vue') },
        { path: 'blindbox', name: 'blindbox', component: () => import('../views/BlindBoxManage.vue') },
        { path: 'blindbox/:id/item', name: 'blindboxItem', component: () => import('../views/BlindBoxItemManage.vue') },
        { path: 'order', name: 'order', component: () => import('../views/OrderManage.vue') },
        { path: 'user', name: 'user', component: () => import('../views/UserManage.vue') },
      ],
    },
  ],
})

router.beforeEach((to) => {
  const userStore = useUserStore()
  if (to.meta.requiresAuth && !userStore.token) {
    return { name: 'login' }
  }
  if (to.name === 'login' && userStore.token) {
    return { name: 'product' }
  }
  if (to.meta.requiresAuth && !userStore.isAdmin()) {
    userStore.logout()
    ElMessage.error('无管理员权限')
    return { name: 'login' }
  }
})

export default router

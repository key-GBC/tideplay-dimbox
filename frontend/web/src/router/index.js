import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/login', name: 'login', component: () => import('../views/Login.vue') },
    { path: '/', name: 'home', component: () => import('../views/Home.vue'), meta: { requiresAuth: true } },
    { path: '/product/:id', name: 'productDetail', component: () => import('../views/ProductDetail.vue'), meta: { requiresAuth: true } },
    { path: '/order/confirm', name: 'orderConfirm', component: () => import('../views/OrderConfirm.vue'), meta: { requiresAuth: true } },
    { path: '/order', name: 'orderList', component: () => import('../views/OrderList.vue'), meta: { requiresAuth: true } },
    { path: '/order/:id', name: 'orderDetail', component: () => import('../views/OrderDetail.vue'), meta: { requiresAuth: true } },
    { path: '/profile', name: 'profile', component: () => import('../views/Profile.vue'), meta: { requiresAuth: true } },
  ],
})

router.beforeEach((to) => {
  const userStore = useUserStore()
  if (to.meta.requiresAuth && !userStore.token) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }
  if (to.name === 'login' && userStore.token) {
    return { name: 'home' }
  }
})

export default router

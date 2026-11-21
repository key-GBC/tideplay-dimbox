<script setup>
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const menus = [
  { path: '/product', label: '商品管理' },
  { path: '/category', label: '分类管理' },
  { path: '/brand', label: '品牌管理' },
  { path: '/seckill', label: '秒杀活动' },
  { path: '/order', label: '订单管理' },
  { path: '/user', label: '用户管理' },
]

function logout() {
  userStore.logout()
  router.push({ name: 'login' })
}
</script>

<template>
  <div class="admin-layout">
    <aside class="sidebar">
      <div class="logo">次元匣社 · 后台</div>
      <el-menu :default-active="route.path" router>
        <el-menu-item v-for="m in menus" :key="m.path" :index="m.path">
          {{ m.label }}
        </el-menu-item>
      </el-menu>
    </aside>
    <div class="main">
      <header class="topbar">
        <span class="crumb">{{ menus.find((m) => route.path.startsWith(m.path))?.label || '管理后台' }}</span>
        <div class="right">
          <span v-if="userStore.user" class="name">{{ userStore.user.nickname || userStore.user.username }}</span>
          <el-button size="small" @click="logout">退出登录</el-button>
        </div>
      </header>
      <main class="content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
}
.sidebar {
  width: 200px;
  flex-shrink: 0;
  background: #1f2937;
  color: #fff;
}
.logo {
  height: 56px;
  line-height: 56px;
  padding: 0 20px;
  font-weight: 600;
  color: #fff;
}
.sidebar :deep(.el-menu) {
  background: transparent;
  border-right: none;
}
.sidebar :deep(.el-menu-item) {
  color: #d1d5db;
}
.sidebar :deep(.el-menu-item.is-active) {
  color: #fff;
  background: #374151;
}
.main {
  flex: 1;
  display: flex;
  flex-direction: column;
}
.topbar {
  height: 56px;
  background: #fff;
  border-bottom: 1px solid var(--border);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
}
.crumb {
  font-weight: 600;
}
.right {
  display: flex;
  align-items: center;
  gap: 12px;
}
.name {
  color: var(--text-muted);
  font-size: 14px;
}
.content {
  padding: 24px;
}
</style>

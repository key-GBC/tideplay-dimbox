<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '../api/auth'
import { useUserStore } from '../stores/user'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const form = ref({ username: '', password: '' })

async function submit() {
  if (!form.value.username || !form.value.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    const data = await login(form.value)
    if (data.role !== 1) {
      ElMessage.error('无管理员权限')
      return
    }
    userStore.setLogin(data.token, data)
    ElMessage.success('登录成功')
    router.push('/')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-wrap">
    <div class="login-card">
      <h1>次元匣社 · 管理后台</h1>
      <el-form label-position="top">
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入用户名" @keyup.enter="submit" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" @keyup.enter="submit" />
        </el-form-item>
        <el-button type="primary" class="submit-btn" :loading="loading" @click="submit">登录</el-button>
      </el-form>
    </div>
  </div>
</template>

<style scoped>
.login-wrap {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
}
.login-card {
  width: 360px;
  max-width: 100%;
  background: #fff;
  border: 1px solid var(--border);
  border-radius: 8px;
  padding: 32px 28px;
}
.login-card h1 {
  margin: 0 0 24px;
  font-size: 20px;
  letter-spacing: 0;
}
.submit-btn {
  width: 100%;
}
</style>

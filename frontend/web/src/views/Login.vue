<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login, register } from '../api/auth'
import { useUserStore } from '../stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const isLogin = ref(true)
const loading = ref(false)
const form = ref({ username: '', password: '', nickname: '' })

async function submit() {
  if (!form.value.username || !form.value.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    if (isLogin.value) {
      const data = await login({ username: form.value.username, password: form.value.password })
      userStore.setLogin(data.token, data)
      ElMessage.success('登录成功')
      router.push(route.query.redirect || '/')
    } else {
      await register({
        username: form.value.username,
        password: form.value.password,
        nickname: form.value.nickname || undefined,
      })
      ElMessage.success('注册成功，请登录')
      isLogin.value = true
    }
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-wrap">
    <div class="login-card">
      <div class="brand">
        <h1>次元匣社</h1>
        <p>DIMBOX · TidePlay</p>
      </div>
      <el-form label-position="top">
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item v-if="!isLogin" label="昵称">
          <el-input v-model="form.nickname" placeholder="选填" clearable />
        </el-form-item>
        <el-form-item label="密码">
          <el-input
            v-model="form.password"
            type="password"
            show-password
            placeholder="请输入密码"
            @keyup.enter="submit"
          />
        </el-form-item>
        <el-button type="primary" class="submit-btn" :loading="loading" @click="submit">
          {{ isLogin ? '登录' : '注册' }}
        </el-button>
      </el-form>
      <el-button link class="toggle-btn" @click="isLogin = !isLogin">
        {{ isLogin ? '没有账号？去注册' : '已有账号？去登录' }}
      </el-button>
    </div>
  </div>
</template>

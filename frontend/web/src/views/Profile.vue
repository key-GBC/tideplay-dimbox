<template>
  <div class="page">
    <header class="topbar">
      <span class="brand" style="cursor: pointer" @click="router.push('/')">次元匣社</span>
      <el-button size="small" @click="router.push('/')">返回商城</el-button>
    </header>

    <div class="body">
      <el-tabs v-model="activeTab">
        <!-- 资料 -->
        <el-tab-pane label="资料设置" name="profile">
          <div class="panel">
            <el-form label-width="80px" style="max-width: 480px">
              <el-form-item label="用户名">
                <el-input :model-value="profileForm.username" disabled />
              </el-form-item>
              <el-form-item label="昵称">
                <el-input :disabled="!editMode" v-model="profileForm.nickname" maxlength="32" />
              </el-form-item>
              <el-form-item label="性别">
                <el-radio-group :disabled="!editMode" v-model="profileForm.gender">
                  <el-radio :value="0">保密</el-radio>
                  <el-radio :value="1">男</el-radio>
                  <el-radio :value="2">女</el-radio>
                </el-radio-group>
              </el-form-item>
              <el-form-item label="生日">
                <el-date-picker :disabled="!editMode" v-model="profileForm.birthday" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" />
              </el-form-item>
              <el-form-item label="头像">
                <el-input :disabled="!editMode" v-model="profileForm.avatar" placeholder="头像图片 URL（选填）" />
              </el-form-item>
              <el-form-item label="简介">
                <el-input :disabled="!editMode" v-model="profileForm.bio" type="textarea" :rows="3" maxlength="255" />
              </el-form-item>
              <el-button v-if="!editMode" type="primary" @click="enterEdit">编辑</el-button>
              <template v-else>
                <el-button type="primary" @click="saveProfile">保存</el-button>
                <el-button type="primary" @click="cancelEdit">取消</el-button>
              </template>
            </el-form>
          </div>
        </el-tab-pane>

        <!-- 改密码 -->
        <el-tab-pane label="修改密码" name="password">
          <div class="panel">
            <el-form label-width="80px" style="max-width: 420px">
              <el-form-item label="旧密码">
                <el-input v-model="passwordForm.oldPassword" type="password" show-password />
              </el-form-item>
              <el-form-item label="新密码">
                <el-input v-model="passwordForm.newPassword" type="password" show-password />
              </el-form-item>
              <el-form-item label="确认密码">
                <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
              </el-form-item>
              <el-button type="primary" @click="savePassword">修改密码</el-button>
            </el-form>
          </div>
        </el-tab-pane>

        <!-- 地址 -->
        <el-tab-pane label="收货地址" name="address">
          <div class="panel">
            <el-button type="primary" style="margin-bottom: 16px" @click="openAddAddress">新增地址</el-button>
            <div v-for="addr in addresses" :key="addr.id" class="address-item">
              <div class="address-info">
                <div class="address-line1">
                  <span class="receiver">{{ addr.receiver }}</span>
                  <span class="phone">{{ addr.phone }}</span>
                  <el-tag v-if="addr.isDefault === 1" size="small" type="danger">默认</el-tag>
                </div>
                <div class="address-line2">{{ addr.province }}{{ addr.city }}{{ addr.district }} {{ addr.detail }}</div>
              </div>
              <div class="address-actions">
                <el-button v-if="addr.isDefault !== 1" link type="primary" @click="setDefault(addr)">设为默认</el-button>
                <el-button link type="primary" @click="openEditAddress(addr)">编辑</el-button>
                <el-button link type="danger" @click="removeAddress(addr)">删除</el-button>
              </div>
            </div>
            <el-empty v-if="addresses.length === 0" description="暂无地址" />
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>

    <!-- 地址编辑弹窗 -->
    <el-dialog v-model="addressDialogVisible" :title="addressForm.id ? '编辑地址' : '新增地址'" width="480px">
      <el-form label-width="80px">
        <el-form-item label="收货人">
          <el-input v-model="addressForm.receiver" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="addressForm.phone" maxlength="11" />
        </el-form-item>
        <el-form-item label="省市区">
          <div style="display: flex; gap: 8px; width: 100%">
            <el-input v-model="addressForm.province" placeholder="省" />
            <el-input v-model="addressForm.city" placeholder="市" />
            <el-input v-model="addressForm.district" placeholder="区" />
          </div>
        </el-form-item>
        <el-form-item label="详细地址">
          <el-input v-model="addressForm.detail" />
        </el-form-item>
        <el-form-item label="设为默认">
          <el-switch v-model="addressForm.isDefault" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addressDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveAddress">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  profile, updateProfile, changePassword,
  addressList, addAddress, updateAddress, deleteAddress, setDefaultAddress,
} from '../api/user'
import { useUserStore } from '../stores/user'

const router = useRouter()
const userStore = useUserStore()

const activeTab = ref('profile')

const editMode = ref(false)
const backup = ref(null)

const profileForm = ref({ username: '', nickname: '', gender: 0, avatar: '', birthday: '', bio: '' })

const passwordForm = ref({ oldPassword: '', newPassword: '', confirmPassword: '' })

const addresses = ref([])
const addressDialogVisible = ref(false)
const addressForm = ref({ id: null, receiver: '', phone: '', province: '', city: '', district: '', detail: '', isDefault: 0 })

async function load() {
  const p = await profile()
  profileForm.value = {
    username: p.username,
    nickname: p.nickname,
    gender: p.gender ?? 0,
    avatar: p.avatar || '',
    birthday: p.birthday || '',
    bio: p.bio || '',
  }
  addresses.value = await addressList()
}

function enterEdit() {
  backup.value = { ...profileForm.value }
  editMode.value = true
}

function cancelEdit() {
  profileForm.value = { ...backup.value }
  editMode.value = false
}

async function saveProfile() {
  await updateProfile({
    nickname: profileForm.value.nickname,
    gender: profileForm.value.gender,
    avatar: profileForm.value.avatar,
    birthday: profileForm.value.birthday,
    bio: profileForm.value.bio,
  })
  editMode.value = false
  ElMessage.success('资料已保存')
}

async function savePassword() {
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    ElMessage.warning('两次输入的新密码不一致')
    return
  }
  await changePassword({
    oldPassword: passwordForm.value.oldPassword,
    newPassword: passwordForm.value.newPassword,
  })
  ElMessage.success('密码已修改，请重新登录')
  userStore.logout()
  router.push({ name: 'login'})
}

async function openAddAddress() {
  addressForm.value = { id: null, receiver: '', phone: '', province: '', city: '', district: '', detail: '', isDefault: 0 }
  addressDialogVisible.value = true
}

async function openEditAddress(addr) {
  addressForm.value = { ...addr }
  addressDialogVisible.value = true
}

async function saveAddress() {
  if (addressForm.value.id) {
    await updateAddress(addressForm.value)
  } else {
    await addAddress(addressForm.value)
  }
  addressDialogVisible.value = false
  ElMessage.success('地址已保存')
  addresses.value = await addressList()
}

async function removeAddress(addr) {
  try {
    await ElMessageBox.confirm('确定要删除该地址吗？', '提示', { type: 'warning' })
  } catch (e) {
    return
  }
  await deleteAddress(addr.id)
  ElMessage.success('地址已删除')
  addresses.value = await addressList()
}

async function setDefault(addr) {
  await setDefaultAddress(addr.id)
  ElMessage.success('已设为默认')
  addresses.value = await addressList()
}

onMounted(load)
</script>

<style scoped>
.body { max-width: 900px; margin: 0 auto; padding: 20px 24px; }
.panel { background: #fff; border: 1px solid var(--border); border-radius: 8px; padding: 24px; }
.address-item { display: flex; justify-content: space-between; align-items: center; padding: 14px 0; border-bottom: 1px solid var(--border); }
.address-info { flex: 1; }
.address-line1 { display: flex; align-items: center; gap: 8px; font-size: 15px; }
.receiver { font-weight: 600; }
.phone { color: var(--text-muted); }
.address-line2 { color: var(--text-muted); font-size: 13px; margin-top: 4px; }
.address-actions { display: flex; gap: 4px; flex-shrink: 0; }
</style>
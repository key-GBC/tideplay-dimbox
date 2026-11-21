<template>
  <div class="address-picker">
    <div
        v-for="addr in addresses"
        :key="addr.id"
        class="addr-item"
        :class="{ active: modelValue && modelValue.id === addr.id }"
        @click="select(addr)"
    >
      <div class="addr-line1">
        <span class="receiver">{{ addr.receiver }}</span>
        <span class="phone">{{ addr.phone }}</span>
        <el-tag v-if="addr.isDefault === 1" size="small" type="danger">默认</el-tag>
      </div>
      <div class="addr-line2">{{ addr.province }}{{ addr.city }}{{ addr.district }} {{ addr.detail }}</div>
    </div>

    <el-empty v-if="addresses.length === 0" description="暂无地址，请先添加" />

    <el-button type="primary" plain @click="dialogVisible = true">新增地址</el-button>

    <el-dialog v-model="dialogVisible" title="新增地址" width="480px">
      <el-form label-width="80px">
        <el-form-item label="收货人"><el-input v-model="form.receiver" /></el-form-item>
        <el-form-item label="手机号"><el-input v-model="form.phone" maxlength="11" /></el-form-item>
        <el-form-item label="省市区">
          <div style="display: flex; gap: 8px; width: 100%">
            <el-input v-model="form.province" placeholder="省" />
            <el-input v-model="form.city" placeholder="市" />
            <el-input v-model="form.district" placeholder="区" />
          </div>
        </el-form-item>
        <el-form-item label="详细地址"><el-input v-model="form.detail" /></el-form-item>
        <el-form-item label="设为默认">
          <el-switch v-model="form.isDefault" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { addressList, addAddress } from '../api/user'

const props = defineProps({
  modelValue: { type: Object, default: null},
})
const emit = defineEmits(['update:modelValue'])

const addresses = ref([])
const dialogVisible = ref(false)
const form = ref({ receiver: '', phone: '', province: '', city: '', district: '', detail: '', isDefault: 0 })

async function load() {
  try {
    addresses.value = await addressList()
    if (!props.modelValue && addresses.value.length) {
      const def = addresses.value.find((a) => a.isDefault === 1)
      emit('update:modelValue', def || addresses.value[0])
    }
  } catch (e) {
    // 错误提示已由拦截器统一弹出
  }
}

function select(addr) {
  emit('update:modelValue', addr)
}

async function save() {
  try {
    await addAddress(form.value)
    dialogVisible.value = false
    ElMessage.success('地址已保存')
    form.value = { receiver: '', phone: '', province: '', city: '', district: '', detail: '', isDefault: 0 }
    await load()
  } catch (e) {
    // 错误提示已由拦截器统一弹出
  }
}

onMounted(load)
</script>

<style scoped>
.address-picker { display: flex; flex-direction: column; gap: 12px; }
.addr-item { border: 1px solid var(--border); border-radius: 8px; padding: 14px; cursor: pointer; background: #fff; }
.addr-item.active { border-color: #409eff; background: #ecf5ff; }
.addr-line1 { display: flex; align-items: center; gap: 8px; font-size: 15px; }
.receiver { font-weight: 600; }
.phone { color: var(--text-muted); }
.addr-line2 { color: var(--text-muted); font-size: 13px; margin-top: 4px; }
</style>
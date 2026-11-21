<template>
  <div class="confirm">
    <div class="left">
      <h2>确认订单</h2>
      <div class="goods">
        <img :src="image" :alt="name" />
        <div class="goods-info">
          <div class="goods-name">{{ name }}</div>
          <div class="goods-sku">{{ skuName }}</div>
          <div class="goods-price">¥{{ (price / 100).toFixed(2) }} × {{ quantity }}</div>
        </div>
      </div>
      <div class="total">合计：<span>¥{{ (totalAmount / 100).toFixed(2) }}</span></div>
    </div>

    <div class="right">
      <h3>收货地址</h3>
      <AddressPicker v-model="selectedAddress" />
      <el-form label-width="80px" style="margin-top: 20px">
        <el-form-item label="备注">
          <el-input v-model="remark" placeholder="选填" />
        </el-form-item>
        <el-button type="primary" size="large" :loading="submitting" @click="submit" >提交订单</el-button>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { createOrder } from '../api/order'
import AddressPicker from '../components/AddressPicker.vue'

const route = useRoute()
const router = useRouter()

const skuId = route.query.skuId
const quantity = Number(route.query.quantity || 1)
const name = route.query.name || ''
const skuName = route.query.skuName || ''
const price = Number(route.query.price || 0)
const image = route.query.image || ''

const submitting = ref(false)
const selectedAddress = ref(null)
const remark = ref('')

const totalAmount = computed(() => price * quantity)

const requestId = ref(crypto.randomUUID())

async function submit() {
  if (submitting.value) return
  if (!selectedAddress.value) {
      ElMessage.warning(`请选择收货地址`)
      return
  }
  submitting.value = true
  try {
    const orderNo = await createOrder({
      requestId: requestId.value,
      skuId,
      quantity,
      address: {
        receiver: selectedAddress.value.receiver,
        phone: selectedAddress.value.phone,
        province: selectedAddress.value.province,
        city: selectedAddress.value.city,
        district: selectedAddress.value.district,
        detail: selectedAddress.value.detail,
      },
      remark: remark.value
    })
    ElMessage.success('下单成功，订单号：' + orderNo)
    router.push('/order')
  } catch (e) {
  }finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.confirm { display: flex; gap: 32px; padding: 32px; max-width: 1100px; margin: 0 auto; }
.left { flex: 1; background: #fff; border: 1px solid var(--border); border-radius: 8px; padding: 24px; }
.right { width: 420px; flex-shrink: 0; background: #fff; border: 1px solid var(--border); border-radius: 8px; padding: 24px; }
h2, h3 { margin: 0 0 20px; letter-spacing: 0; }
.goods { display: flex; gap: 16px; }
.goods img { width: 100px; height: 100px; object-fit: cover; border-radius: 8px; background: #f3f4f6; }
.goods-name { font-size: 16px; font-weight: 600; }
.goods-sku { color: var(--text-muted); font-size: 13px; margin-top: 4px; }
.goods-price { color: #e5484d; font-weight: 700; margin-top: 8px; }
.total { margin-top: 24px; text-align: right; font-size: 15px; }
.total span { color: #e5484d; font-size: 24px; font-weight: 700; }
</style>
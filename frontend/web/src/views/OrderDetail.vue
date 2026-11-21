<template>
  <div class="page">
    <header class="topbar">
      <span class="brand" style="cursor: pointer" @click="router.push('/')">次元匣社</span>
      <el-button size="small" @click="router.push('/order')">返回订单</el-button>
    </header>

    <div v-if="order" class="body">
      <div class="status-bar">
        <span class="status">{{ statusText[order.status] }}</span>
        <span class="order-no">订单号：{{ order.orderNo }}</span>
      </div>

      <div class="section">
        <h3>商品明细</h3>
        <div v-for="item in order.items" :key="item.skuId" class="item">
          <img :src="item.image || ''" />
          <div class="item-info">
            <div class="item-name">{{ item.skuName }}</div>
            <div class="item-price">¥{{ (item.price / 100).toFixed(2) }} × {{ item.quantity }}</div>
          </div>
        </div>
        <div class="total">合计：<span>¥{{ (order.totalAmount / 100).toFixed(2) }}</span></div>
      </div>

      <div class="section">
        <h3>收货信息</h3>
        <p>{{ order.receiver }} {{ order.phone }}</p>
        <p>{{ order.province }}{{ order.city }}{{ order.district }} {{ order.detail }}</p>
      </div>

      <div v-if="order.status === 10" class="actions">
        <el-button type="primary" size="large" @click="goPay">去支付</el-button>
        <el-button size="large" @click="doCancel">取消订单</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrderDetail, cancelOrder } from '../api/order'
import { createPayment, pay } from '../api/payment'

const route = useRoute()
const router = useRouter()

const order = ref(null)
const statusText = {10: '待支付', 20: '已支付', 50: '已取消'}

async function load() {
  try {
    order.value = await getOrderDetail(route.params.id)
  } catch (e) {
    // 错误提示已由拦截器统一弹出
  }
}

async function goPay() {
  try {
    const payment = await createPayment({ orderNo: order.value.orderNo })
    await pay(payment.paymentNo)
    ElMessage.success('支付成功')
    load()
  } catch (e) {
    // 错误提示已由拦截器统一弹出
  }
}

async function doCancel() {
  try {
    await ElMessageBox.confirm('确定取消该订单吗？', '提示', { type: 'warning' })
  } catch (e) {
    return
  }
  try {
    await cancelOrder(order.value.id)
    ElMessage.success('订单已取消')
    load()
  } catch (e) {
    // 错误提示已由拦截器统一弹出
  }
}

onMounted(load)
</script>

<style scoped>
.body { max-width: 900px; margin: 0 auto; padding: 20px 24px; }
.status-bar { background: #fff; border: 1px solid var(--border); border-radius: 8px; padding: 16px; display: flex; justify-content: space-between; }
.status-bar .status { color: #e5484d; font-size: 20px; font-weight: 700; }
.status-bar .order-no { color: var(--text-muted); }
.section { background: #fff; border: 1px solid var(--border); border-radius: 8px; padding: 16px; margin-top: 16px; }
.section h3 { margin: 0 0 12px; letter-spacing: 0; }
.item { display: flex; gap: 12px; padding: 8px 0; }
.item img { width: 64px; height: 64px; object-fit: cover; border-radius: 6px; background: #f3f4f6; }
.item-name { font-size: 15px; font-weight: 600; }
.item-price { color: var(--text-muted); font-size: 13px; margin-top: 4px; }
.total { text-align: right; margin-top: 8px; }
.total span { color: #e5484d; font-size: 22px; font-weight: 700; }
.actions { display: flex; gap: 16px; margin-top: 20px; }
</style>
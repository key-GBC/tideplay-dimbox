<template>
  <div class="page">
    <header class="topbar">
      <span class="brand" style="cursor: pointer" @click="router.push('/')">次元匣社</span>
      <el-button size="small" @click="router.push('/')">返回商城</el-button>
    </header>

    <div class="body">
      <el-tabs v-model="activeTab" @tab-change="onTabChange">
        <el-tab-pane v-for="t in tabs" :key="t.name" :label="t.label" :name="t.name" />
      </el-tabs>

      <div v-loading="loading" class="order-cards">
        <div v-for="o in orders" :key="o.id" class="order-card" @click="router.push('/order/' + o.id)">
          <div class="order-head">
            <span>订单号：{{ o.orderNo }}</span>
            <span class="status">{{ statusText[o.status] }}</span>
          </div>
          <div class="order-main">
            <span>金额：¥{{ (o.totalAmount / 100).toFixed(2) }}</span>
            <span class="time">{{ o.createdAt }}</span>
          </div>
        </div>
        <el-empty v-if="!loading && orders.length === 0" description="暂无订单" />
      </div>

      <div v-if="total > 0" class="pagination">
        <el-pagination background layout="prev, pager, next" :total="total" :page-size="pageSize" :current-page="pageNum" @current-change="onPageChange" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { pageOrderList } from '../api/order'

const router = useRouter()

const tabs = [
  { label: '全部', name: 'all', status: null },
  { label: '待支付', name: '10', status: 10 },
  { label: '已支付', name: '20', status: 20 },
  { label: '已取消', name: '50', status: 50 },
]
const statusText = { 10: '待支付', 20: '已支付', 50: '已取消' }

const activeTab = ref('all')
const orders = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = 10
const loading = ref(false)

async function load() {
  loading.value = true
  try {
    const data = await pageOrderList({
      status: activeTab.value === 'all' ? undefined : Number(activeTab.value),
      pageNum: pageNum.value,
      pageSize: pageSize,
    })
    orders.value = data.records
    total.value = data.total
  } finally {
    loading.value = false
  }
}

function onTabChange() {
  pageNum.value = 1
  load()
}
function onPageChange(page) {
  pageNum.value = page
  load()
}
onMounted(load)
</script>

<style scoped>
.body { max-width: 900px; margin: 0 auto; padding: 20px 24px; }
.order-cards { min-height: 200px; }
.order-card { background: #fff; border: 1px solid var(--border); border-radius: 8px; padding: 16px; margin-bottom: 12px; cursor: pointer; }
.order-head { display: flex; justify-content: space-between; font-size: 14px; }
.order-head .status { color: #e5484d; font-weight: 600; }
.order-main { display: flex; justify-content: space-between; margin-top: 12px; color: var(--text-muted); font-size: 14px; }
.pagination { margin-top: 20px; display: flex; justify-content: center; }
</style>
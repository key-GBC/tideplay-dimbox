<script setup>
import { onMounted, onUnmounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getSeckillActivityList, buySeckill } from '../api/seckill'
import { formatPrice, formatTime } from '../api/format'

const router = useRouter()
const activities = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = 12
const loading = ref(false)
const now = ref(Date.now())
let timer = null

async function load() {
  loading.value = true
  try {
    const data = await getSeckillActivityList({ status: 1, pageNum: pageNum.value, pageSize })
    activities.value = data.records
    total.value = data.total
  } finally {
    loading.value = false
  }
}

function toTime(str) {
  return new Date(String(str).replace(' ', 'T')).getTime()
}

function activityState(activity) {
  const start = toTime(activity.startTime)
  const end = toTime(activity.endTime)
  if (now.value < start) return 'pending'
  if (now.value > end) return 'ended'
  return 'active'
}

const stateText = { pending: '未开始', active: '进行中', ended: '已结束' }

async function buy(activity) {
  try {
    const data = await buySeckill(activity.id)
    ElMessage.success('抢购成功，订单号：' + data.orderNo)
  } catch (e) {
    // 错误提示已由拦截器统一弹出
  }
}

function onPageChange(page) {
  pageNum.value = page
  load()
}

onMounted(() => {
  load()
  timer = setInterval(() => {
    now.value = Date.now()
  }, 1000)
})

onUnmounted(() => clearInterval(timer))
</script>

<template>
  <div class="page">
    <header class="topbar">
      <span class="brand" style="cursor: pointer" @click="router.push('/')">次元匣社</span>
      <div class="nav">
        <el-button size="small" @click="router.push('/seckill/record')">我的抢购记录</el-button>
        <el-button size="small" @click="router.push('/')">返回商城</el-button>
      </div>
    </header>

    <div class="body">
      <h2 class="title">限时秒杀</h2>
      <div v-loading="loading" class="grid">
        <div v-for="a in activities" :key="a.id" class="seckill-card">
          <div class="sku">款式 #{{ a.skuId }}</div>
          <div class="price"><span class="yen">¥</span>{{ formatPrice(a.seckillPrice) }}</div>
          <div class="meta">
            <div>秒杀库存：{{ a.stock }}</div>
            <div>开始：{{ formatTime(a.startTime) }}</div>
            <div>结束：{{ formatTime(a.endTime) }}</div>
          </div>
          <div class="foot">
            <span class="state" :class="activityState(a)">{{ stateText[activityState(a)] }}</span>
            <el-button type="danger" size="small" :disabled="activityState(a) !== 'active'" @click="buy(a)">
              立即抢购
            </el-button>
          </div>
        </div>
        <el-empty v-if="!loading && activities.length === 0" description="暂无秒杀活动" />
      </div>
      <div v-if="total > 0" class="pagination">
        <el-pagination
          background
          layout="prev, pager, next"
          :total="total"
          :page-size="pageSize"
          :current-page="pageNum"
          @current-change="onPageChange"
        />
      </div>
    </div>
  </div>
</template>

<style scoped>
.page { min-height: 100vh; background: var(--bg); }
.topbar { display: flex; align-items: center; justify-content: space-between; padding: 16px 24px; background: #fff; border-bottom: 1px solid var(--border); }
.topbar .brand { font-weight: 600; font-size: 18px; }
.nav { display: flex; gap: 8px; }
.body { max-width: 1100px; margin: 0 auto; padding: 24px; }
.title { margin: 0 0 20px; font-size: 20px; letter-spacing: 0; }
.grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(240px, 1fr)); gap: 16px; min-height: 200px; }
.seckill-card { background: #fff; border: 1px solid var(--border); border-radius: 8px; padding: 16px; }
.sku { color: var(--text-muted); font-size: 13px; }
.price { color: #e5484d; font-size: 28px; font-weight: 700; margin: 8px 0; }
.price .yen { font-size: 16px; }
.meta { color: var(--text-muted); font-size: 13px; line-height: 1.8; }
.foot { display: flex; justify-content: space-between; align-items: center; margin-top: 12px; }
.state { font-size: 13px; color: var(--text-muted); }
.state.active { color: #e5484d; font-weight: 600; }
.pagination { margin-top: 20px; display: flex; justify-content: center; }
</style>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getSeckillRecord } from '../api/seckill'
import { formatTime } from '../api/format'

const router = useRouter()
const records = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = 10
const loading = ref(false)

async function load() {
  loading.value = true
  try {
    const data = await getSeckillRecord({ pageNum: pageNum.value, pageSize })
    records.value = data.records
    total.value = data.total
  } finally {
    loading.value = false
  }
}

function onPageChange(page) {
  pageNum.value = page
  load()
}

onMounted(load)
</script>

<template>
  <div class="page">
    <header class="topbar">
      <span class="brand" style="cursor: pointer" @click="router.push('/')">次元匣社</span>
      <el-button size="small" @click="router.push('/seckill')">返回秒杀</el-button>
    </header>

    <div class="body">
      <h2 class="title">我的抢购记录</h2>
      <div v-loading="loading" class="list">
        <div v-for="r in records" :key="r.id" class="record-item">
          <div class="line1">
            <span>订单号：{{ r.orderNo }}</span>
            <span class="status">{{ r.status === 1 ? '已扣库存' : '待扣库存' }}</span>
          </div>
          <div class="line2">
            <span>款式 #{{ r.skuId }}</span>
            <span>{{ formatTime(r.createAt) }}</span>
          </div>
        </div>
        <el-empty v-if="!loading && records.length === 0" description="暂无记录" />
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
.body { max-width: 900px; margin: 0 auto; padding: 24px; }
.title { margin: 0 0 20px; font-size: 20px; letter-spacing: 0; }
.list { min-height: 200px; }
.record-item { background: #fff; border: 1px solid var(--border); border-radius: 8px; padding: 14px 16px; margin-bottom: 12px; }
.line1 { display: flex; justify-content: space-between; font-size: 14px; }
.line1 .status { color: #e5484d; font-weight: 600; }
.line2 { display: flex; justify-content: space-between; margin-top: 8px; color: var(--text-muted); font-size: 13px; }
.pagination { margin-top: 20px; display: flex; justify-content: center; }
</style>

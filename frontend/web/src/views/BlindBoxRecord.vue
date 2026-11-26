<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getDrawRecord } from '../api/blindbox'

const router = useRouter()
const records = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = 10
const loading = ref(false)

async function load() {
  loading.value = true
  try {
    const data = await getDrawRecord({ pageNum: pageNum.value, pageSize })
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
      <el-button size="small" @click="router.push('/blindbox')">返回盲盒</el-button>
    </header>

    <div class="body">
      <h2 class="title">我的抽盒记录</h2>
      <div v-loading="loading" class="record-list">
        <div v-for="r in records" :key="r.orderNo" class="record-card">
          <div class="left">
            <div class="order-no">单号：{{ r.orderNo }}</div>
            <div class="meta">款式 ID：{{ r.itemId }} · SKU：{{ r.skuId }}</div>
          </div>
          <div class="right">
            <el-tag v-if="r.isHidden === 1" type="warning">隐藏款</el-tag>
            <span class="price">¥{{ (r.price / 100).toFixed(2) }}</span>
            <span class="time">{{ r.createdAt }}</span>
          </div>
        </div>
        <el-empty v-if="!loading && records.length === 0" description="暂无抽盒记录" />
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
.page {
  min-height: 100vh;
  background: var(--bg);
}
.topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 24px;
  background: #fff;
  border-bottom: 1px solid var(--border);
}
.topbar .brand {
  font-weight: 600;
  font-size: 18px;
}
.body {
  max-width: 900px;
  margin: 0 auto;
  padding: 24px;
}
.title {
  margin: 0 0 20px;
  font-size: 20px;
}
.record-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  border: 1px solid var(--border);
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 12px;
}
.order-no {
  font-weight: 600;
}
.meta {
  color: var(--text-muted);
  font-size: 13px;
  margin-top: 6px;
}
.right {
  display: flex;
  align-items: center;
  gap: 12px;
}
.price {
  color: #e5484d;
  font-weight: 700;
}
.time {
  color: var(--text-muted);
  font-size: 13px;
}
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>

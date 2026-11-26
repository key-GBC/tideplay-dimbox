<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getSeriesList } from '../api/blindbox'

const router = useRouter()
const series = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = 12
const loading = ref(false)

async function load() {
  loading.value = true
  try {
    const data = await getSeriesList({ pageNum: pageNum.value, pageSize })
    series.value = data.records
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
      <el-button size="small" @click="router.push('/')">返回商城</el-button>
    </header>

    <div class="body">
      <h2 class="title">盲盒系列</h2>
      <div v-loading="loading" class="grid">
        <div v-for="s in series" :key="s.id" class="series-card" @click="router.push('/blindbox/' + s.id)">
          <img :src="s.coverImage || ''" :alt="s.name" />
          <div class="info">
            <div class="name">{{ s.name }}</div>
            <div class="desc">{{ s.description }}</div>
            <div class="price">¥{{ (s.price / 100).toFixed(2) }} / 抽</div>
          </div>
        </div>
        <el-empty v-if="!loading && series.length === 0" description="暂无盲盒" />
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
  max-width: 1100px;
  margin: 0 auto;
  padding: 24px;
}
.title {
  margin: 0 0 20px;
  font-size: 20px;
}
.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 16px;
  min-height: 200px;
}
.series-card {
  background: #fff;
  border: 1px solid var(--border);
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
}
.series-card img {
  width: 100%;
  aspect-ratio: 1;
  object-fit: cover;
  display: block;
  background: #f3f4f6;
}
.series-card .info {
  padding: 12px;
}
.series-card .name {
  font-size: 15px;
  font-weight: 600;
}
.series-card .desc {
  color: var(--text-muted);
  font-size: 13px;
  margin-top: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.series-card .price {
  color: #e5484d;
  font-weight: 700;
  margin-top: 8px;
}
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>

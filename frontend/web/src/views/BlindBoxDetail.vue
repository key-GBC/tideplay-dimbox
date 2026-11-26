<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getSeriesDetail, drawBlindbox } from '../api/blindbox'

const route = useRoute()
const router = useRouter()
const series = ref(null)
const drawing = ref(false)
const result = ref(null)
const dialogVisible = ref(false)

onMounted(async () => {
  series.value = await getSeriesDetail(route.params.id)
})

async function draw() {
  drawing.value = true
  try {
    result.value = await drawBlindbox(route.params.id)
    dialogVisible.value = true
  } catch (e) {
    // 错误提示已由拦截器统一弹出
  } finally {
    drawing.value = false
  }
}
</script>

<template>
  <div class="page">
    <header class="topbar">
      <span class="brand" style="cursor: pointer" @click="router.push('/')">次元匣社</span>
      <div>
        <el-button size="small" @click="router.push('/blindbox')">返回列表</el-button>
        <el-button size="small" @click="router.push('/blindbox/record')">我的记录</el-button>
      </div>
    </header>

    <div v-if="series" class="body">
      <div class="head">
        <img :src="series.coverImage || ''" :alt="series.name" />
        <div class="head-info">
          <h1>{{ series.name }}</h1>
          <p class="desc">{{ series.description }}</p>
          <div class="price">¥{{ (series.price / 100).toFixed(2) }} / 抽</div>
          <el-button type="primary" size="large" :loading="drawing" @click="draw">抽一次</el-button>
        </div>
      </div>

      <div class="styles">
        <h3>款式一览</h3>
        <div class="style-grid">
          <div v-for="item in series.items" :key="item.id" class="style-card">
            <img :src="item.image || ''" :alt="item.styleName" />
            <div class="style-name">{{ item.styleName }}</div>
          </div>
        </div>
      </div>
    </div>

    <el-dialog v-model="dialogVisible" title="抽盒结果" width="360px" align-center>
      <div v-if="result" class="draw-result">
        <img :src="result.image || ''" :alt="result.styleName" />
        <div class="style-name">{{ result.styleName }}</div>
        <el-tag v-if="result.isHidden === 1" type="warning" size="large">隐藏款</el-tag>
        <div class="order-no">单号：{{ result.orderNo }}</div>
      </div>
      <template #footer>
        <el-button @click="router.push('/blindbox/record')">查看记录</el-button>
        <el-button type="primary" @click="dialogVisible = false">再来一次</el-button>
      </template>
    </el-dialog>
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
  max-width: 1000px;
  margin: 0 auto;
  padding: 24px;
}
.head {
  display: flex;
  gap: 32px;
  background: #fff;
  border: 1px solid var(--border);
  border-radius: 8px;
  padding: 24px;
}
.head img {
  width: 280px;
  height: 280px;
  object-fit: cover;
  border-radius: 8px;
  background: #f3f4f6;
}
.head-info h1 {
  margin: 0;
  font-size: 24px;
  letter-spacing: 0;
}
.head-info .desc {
  color: var(--text-muted);
  margin: 12px 0;
}
.head-info .price {
  color: #e5484d;
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 20px;
}
.styles {
  margin-top: 24px;
}
.styles h3 {
  margin: 0 0 16px;
  font-size: 18px;
}
.style-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  gap: 12px;
}
.style-card {
  background: #fff;
  border: 1px solid var(--border);
  border-radius: 8px;
  overflow: hidden;
}
.style-card img {
  width: 100%;
  aspect-ratio: 1;
  object-fit: cover;
  display: block;
  background: #f3f4f6;
}
.style-card .style-name {
  padding: 8px;
  text-align: center;
  font-size: 13px;
}
.draw-result {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}
.draw-result img {
  width: 160px;
  height: 160px;
  object-fit: cover;
  border-radius: 8px;
  background: #f3f4f6;
}
.draw-result .style-name {
  font-size: 18px;
  font-weight: 600;
}
.draw-result .order-no {
  color: var(--text-muted);
  font-size: 13px;
}
</style>

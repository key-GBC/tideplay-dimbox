<template>
  <div class="home">
    <header class="topbar">
      <span class="brand">次元匣社</span>
      <div class="user">
        <span v-if="userStore.user" class="nickname">{{ userStore.user.nickname || userStore.user.username }}</span>
        <el-button size="small" @click="router.push('/profile')">个人中心</el-button>
        <el-button size="small" @click="logout">退出登录</el-button>
      </div>
    </header>

    <div class="layout">
      <aside class="sidebar">
        <el-menu @select="onMenuSelect">
          <template v-for="cat in categories" :key="cat.id">
            <el-sub-menu v-if="cat.children && cat.children.length" :index="String(cat.id)">
              <template #title>{{ cat.name }}</template>
              <el-menu-item v-for="child in cat.children" :key="child.id" :index="String(child.id)">
                {{ child.name }}
              </el-menu-item>
            </el-sub-menu>
            <el-menu-item v-else :index="String(cat.id)">{{ cat.name }}</el-menu-item>
          </template>
        </el-menu>
      </aside>

      <main class="main">
        <div class="toolbar">
          <el-input v-model="query.keyword" placeholder="搜索商品" clearable style="width: 240px" @keyup.enter="search" />
          <el-button type="primary" @click="search">搜索</el-button>
          <el-select v-model="query.brandId" placeholder="全部品牌" clearable style="width: 160px" @change="onBrandChange">
            <el-option v-for="b in brands" :key="b.id" :label="b.name" :value="b.id" />
          </el-select>
        </div>

        <div v-loading="loading" class="product-grid">
          <ProductCard v-for="p in products" :key="p.id" :product="p" @click="router.push('/product/' + p.id)" />
        </div>

        <div v-if="total > 0" class="pagination">
          <el-pagination background layout="prev, pager, next" :total="total" :page-size="query.pageSize" :current-page="query.pageNum" @current-change="onPageChange" />
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getCategoryTree, getBrandList, getProductList } from '../api/product'
import { useUserStore } from '../stores/user'
import ProductCard from '../components/ProductCard.vue'

const router = useRouter()
const userStore = useUserStore()

const categories = ref([])
const brands = ref([])
const products = ref([])
const total = ref(0)
const loading = ref(false)
const query = ref({ categoryId: null, brandId: null, keyword: '', pageNum: 1, pageSize: 12 })

async function loadProducts() {
  loading.value = true
  try {
    const data = await getProductList(query.value)
    products.value = data.records
    total.value = data.total
  } finally {
    loading.value = false
  }
}

function onMenuSelect(index) {
  query.value.categoryId = index
  query.value.pageNum = 1
  loadProducts()
}
function search() {
  query.value.pageNum = 1
  loadProducts()
}
function onBrandChange() {
  query.value.pageNum = 1
  loadProducts()
}
function onPageChange(page) {
  query.value.pageNum = page
  loadProducts()
}
function logout() {
  userStore.logout()
  router.push({ name: 'login' })
}

onMounted(async () => {
  categories.value = await getCategoryTree()
  brands.value = await getBrandList()
  loadProducts()
})
</script>

<style scoped>
.layout { display: flex; }
.sidebar { width: 200px; flex-shrink: 0; background: #fff; border-right: 1px solid var(--border); min-height: calc(100vh - 57px); }
.main { flex: 1; padding: 20px 24px; }
.toolbar { display: flex; gap: 12px; margin-bottom: 16px; }
.product-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); gap: 16px; min-height: 200px; }
.pagination { margin-top: 20px; display: flex; justify-content: center; }
</style>
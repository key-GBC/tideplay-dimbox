<template>
  <div v-if="product" class="detail">
    <div class="left">
      <img :src="product.mainImage || ''" :alt="product.name" />
    </div>
    <div class="right">
      <h1>{{ product.name }}</h1>
      <p class="sub-title">{{ product.subTitle }}</p>
      <div class="price">
        <span class="yen">¥</span>{{ selectedSku ? (selectedSku.price / 100).toFixed(2) : '--' }}
      </div>

      <div class="sku-list">
        <div
            v-for="sku in product.skus"
            :key="sku.id"
            class="sku-item"
            :class="{ active: sku.id === selectedSkuId }"
            @click="selectedSkuId = sku.id"
        >
          {{ sku.skuName }}
        </div>
      </div>

      <div class="stock">库存：{{ selectedSku ? selectedSku.stock : '--' }}</div>

      <div class="actions">
        <el-input-number v-model="quantity" :min="1" :max="99" />
        <el-button type="primary" size="large" :disabled="!selectedSku" @click="buy">立即购买</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getProductDetail } from '../api/product.js'

const route = useRoute()
const router = useRouter()

const product = ref(null)
const selectedSkuId = ref(null)
const quantity = ref(1)

const selectedSku = computed(() => {
  if (!product.value || !selectedSkuId.value) return null
  return product.value.skus.find((s) => s.id === selectedSkuId.value)
})

onMounted(async () => {
  product.value = await getProductDetail(route.params.id)
  if (product.value.skus && product.value.skus.length) {
    selectedSkuId.value = product.value.skus[0].id
  }
})

function buy() {
  router.push({
    name: 'orderConfirm',
    query: {
      skuId: selectedSkuId.value,
      quantity: quantity.value,
      name: product.value.name,
      skuName: selectedSku.value.skuName,
      price: selectedSku.value.price,
      image: selectedSku.value.image || product.value.mainImage,
      },
  })
}
</script>


<style scoped>
.detail { display: flex; gap: 32px; padding: 32px; max-width: 1100px; margin: 0 auto; }
.left { width: 420px; flex-shrink: 0; }
.left img { width: 100%; aspect-ratio: 1; object-fit: cover; border-radius: 8px; background: #f3f4f6; }
.right { flex: 1; }
h1 { margin: 0; font-size: 24px; letter-spacing: 0; }
.sub-title { color: var(--text-muted); margin: 8px 0 16px; }
.price { color: #e5484d; font-size: 28px; font-weight: 700; }
.price .yen { font-size: 16px; }
.sku-list { display: flex; flex-wrap: wrap; gap: 10px; margin: 20px 0; }
.sku-item { padding: 8px 16px; border: 1px solid var(--border); border-radius: 6px; cursor: pointer; }
.sku-item.active { border-color: #409eff; color: #409eff; background: #ecf5ff; }
.stock { color: var(--text-muted); margin-bottom: 20px; }
.actions { display: flex; gap: 16px; align-items: center; }
</style>
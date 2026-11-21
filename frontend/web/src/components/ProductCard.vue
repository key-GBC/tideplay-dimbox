<script setup>
defineProps({ product: { type: Object, required: true } })
defineEmits(['click'])
</script>

<template>
  <div class="product-card" @click="$emit('click')">
    <div class="img-wrap">
      <img :src="product.mainImage || ''" :alt="product.name" />
    </div>
    <div class="info">
      <div class="name">{{ product.name }}</div>
      <div class="sub-title">{{ product.subTitle }}</div>
      <div class="price"><span class="yen">¥</span>{{ (product.price / 100).toFixed(2) }}</div>
    </div>
  </div>
</template>

<style scoped>
.product-card { background: #fff; border: 1px solid var(--border); border-radius: 8px; overflow: hidden; cursor: pointer; }
.product-card:hover { box-shadow: 0 8px 24px rgba(15, 23, 42, 0.08); }
.img-wrap { aspect-ratio: 1; background: #f3f4f6; }
.img-wrap img { width: 100%; height: 100%; object-fit: cover; display: block; }
.info { padding: 12px; }
.name { font-size: 15px; font-weight: 600; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.sub-title { color: var(--text-muted); font-size: 12px; margin-top: 4px; }
.price { margin-top: 8px; color: #e5484d; font-weight: 700; font-size: 16px; }
.price .yen { font-size: 12px; margin-right: 1px; }
</style>
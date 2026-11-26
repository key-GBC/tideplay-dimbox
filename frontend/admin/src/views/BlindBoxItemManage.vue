<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getSeriesDetail, createItem, updateItem, deleteItem } from '../api/blindbox'

const route = useRoute()
const router = useRouter()
const seriesId = route.params.id

const series = ref(null)
const loading = ref(false)
const dialogVisible = ref(false)
const form = ref({ id: null, skuId: null, styleName: '', image: '', probability: 0, isHidden: 0 })

const probabilitySum = computed(() => {
  if (!series.value || !series.value.items) return 0
  return series.value.items.reduce((sum, item) => sum + item.probability, 0)
})

async function load() {
  loading.value = true
  try {
    series.value = await getSeriesDetail(seriesId)
  } finally {
    loading.value = false
  }
}

function openCreate() {
  form.value = { id: null, skuId: '', styleName: '', image: '', probability: 0, isHidden: 0 }
  dialogVisible.value = true
}

function openEdit(row) {
  form.value = {
    id: row.id,
    skuId: row.skuId,
    styleName: row.styleName,
    image: row.image,
    probability: row.probability,
    isHidden: row.isHidden,
  }
  dialogVisible.value = true
}

async function save() {
  const payload = {
    skuId: form.value.skuId,
    styleName: form.value.styleName,
    image: form.value.image,
    probability: form.value.probability,
    isHidden: form.value.isHidden,
  }
  if (form.value.id) {
    await updateItem(form.value.id, payload)
  } else {
    await createItem(seriesId, payload)
  }
  ElMessage.success('保存成功')
  dialogVisible.value = false
  load()
}

async function remove(row) {
  try {
    await ElMessageBox.confirm('确定删除该款式吗？', '提示', { type: 'warning' })
  } catch (e) {
    return
  }
  await deleteItem(row.id)
  ElMessage.success('删除成功')
  load()
}

onMounted(load)
</script>

<template>
  <div>
    <div class="head">
      <div>
        <h2>{{ series ? series.name : '款式管理' }}</h2>
        <span class="sum" :class="{ ok: probabilitySum === 10000 }">
          概率总和：{{ probabilitySum }} / 10000
        </span>
      </div>
      <el-button type="primary" @click="openCreate">新增款式</el-button>
    </div>

    <el-table v-loading="loading" :data="series ? series.items : []" border>
      <el-table-column prop="id" label="ID" width="180" />
      <el-table-column prop="styleName" label="款式名" min-width="140" />
      <el-table-column prop="skuId" label="SKU ID" width="180" />
      <el-table-column prop="probability" label="概率(万分比)" width="120" />
      <el-table-column label="隐藏款" width="90">
        <template #default="{ row }">
          <el-tag v-if="row.isHidden === 1" type="warning">是</el-tag>
          <span v-else>否</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="140" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
          <el-button link type="danger" @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="back">
      <el-button @click="router.push('/blindbox')">返回系列列表</el-button>
    </div>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑款式' : '新增款式'" width="480px">
      <el-form label-width="90px">
        <el-form-item label="SKU ID">
          <el-input v-model="form.skuId" placeholder="请输入 SKU ID" />
        </el-form-item>
        <el-form-item label="款式名">
          <el-input v-model="form.styleName" maxlength="64" />
        </el-form-item>
        <el-form-item label="图片">
          <el-input v-model="form.image" placeholder="款式图片 URL" />
        </el-form-item>
        <el-form-item label="概率(万分比)">
          <el-input-number v-model="form.probability" :min="0" :max="10000" style="width: 100%" />
        </el-form-item>
        <el-form-item label="隐藏款">
          <el-switch v-model="form.isHidden" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}
.head h2 {
  margin: 0;
  font-size: 18px;
}
.sum {
  color: #e5484d;
  font-size: 13px;
  margin-left: 12px;
}
.sum.ok {
  color: #67c23a;
}
.back {
  margin-top: 16px;
}
</style>

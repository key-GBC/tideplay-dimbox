<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getSeriesList, createSeries, updateSeries } from '../api/blindbox'

const router = useRouter()
const list = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = 10
const loading = ref(false)
const dialogVisible = ref(false)
const form = ref({ id: null, name: '', description: '', coverImage: '', priceYuan: 0 })

async function load() {
  loading.value = true
  try {
    const data = await getSeriesList({ pageNum: pageNum.value, pageSize })
    list.value = data.records
    total.value = data.total
  } finally {
    loading.value = false
  }
}

function openCreate() {
  form.value = { id: null, name: '', description: '', coverImage: '', priceYuan: 0 }
  dialogVisible.value = true
}

function openEdit(row) {
  form.value = {
    id: row.id,
    name: row.name,
    description: row.description,
    coverImage: row.coverImage,
    priceYuan: row.price / 100,
  }
  dialogVisible.value = true
}

async function save() {
  const payload = {
    name: form.value.name,
    description: form.value.description,
    coverImage: form.value.coverImage,
    price: Math.round(form.value.priceYuan * 100),
  }
  if (form.value.id) {
    await updateSeries(form.value.id, payload)
  } else {
    await createSeries(payload)
  }
  ElMessage.success('保存成功')
  dialogVisible.value = false
  load()
}

function onPageChange(page) {
  pageNum.value = page
  load()
}

onMounted(load)
</script>

<template>
  <div>
    <div class="toolbar">
      <el-button type="primary" @click="openCreate">新增系列</el-button>
    </div>

    <el-table v-loading="loading" :data="list" border>
      <el-table-column prop="id" label="ID" width="180" />
      <el-table-column prop="name" label="系列名" min-width="160" />
      <el-table-column prop="description" label="描述" min-width="180" show-overflow-tooltip />
      <el-table-column label="单价" width="120">
        <template #default="{ row }">¥{{ (row.price / 100).toFixed(2) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
          <el-button link type="primary" @click="router.push('/blindbox/' + row.id + '/item')">款式管理</el-button>
        </template>
      </el-table-column>
    </el-table>

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

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑系列' : '新增系列'" width="480px">
      <el-form label-width="80px">
        <el-form-item label="系列名">
          <el-input v-model="form.name" maxlength="64" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="2" maxlength="500" />
        </el-form-item>
        <el-form-item label="封面">
          <el-input v-model="form.coverImage" placeholder="封面图片 URL" />
        </el-form-item>
        <el-form-item label="单价">
          <el-input-number v-model="form.priceYuan" :min="0" :precision="2" :step="1" style="width: 100%" />
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
.toolbar {
  margin-bottom: 16px;
}
.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: center;
}
</style>

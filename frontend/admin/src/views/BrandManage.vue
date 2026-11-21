<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getBrandList, addBrand, updateBrand, deleteBrand } from '../api/product'

const list = ref([])
const dialogVisible = ref(false)
const form = ref({ id: null, name: '', logo: '', description: '', status: 1 })

async function load() {
  list.value = await getBrandList()
}

function openAdd() {
  form.value = { id: null, name: '', logo: '', description: '', status: 1 }
  dialogVisible.value = true
}

function openEdit(row) {
  form.value = { ...row }
  dialogVisible.value = true
}

async function save() {
  if (!form.value.name) {
    ElMessage.warning('请输入品牌名')
    return
  }
  if (form.value.id) {
    await updateBrand(form.value.id, form.value)
  } else {
    await addBrand(form.value)
  }
  ElMessage.success('已保存')
  dialogVisible.value = false
  load()
}

async function remove(row) {
  try {
    await ElMessageBox.confirm(`确定删除品牌「${row.name}」吗？`, '提示', { type: 'warning' })
  } catch (e) {
    return
  }
  await deleteBrand(row.id)
  ElMessage.success('已删除')
  load()
}

onMounted(load)
</script>

<template>
  <div class="panel">
    <div class="toolbar">
      <el-button type="primary" @click="openAdd">新增品牌</el-button>
    </div>
    <el-table :data="list">
      <el-table-column prop="name" label="品牌名" />
      <el-table-column prop="logo" label="Logo" />
      <el-table-column prop="description" label="描述" show-overflow-tooltip />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
          <el-button link type="danger" @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑品牌' : '新增品牌'" width="460px">
      <el-form label-width="80px">
        <el-form-item label="品牌名">
          <el-input v-model="form.name" maxlength="64" />
        </el-form-item>
        <el-form-item label="Logo">
          <el-input v-model="form.logo" placeholder="选填" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="2" maxlength="500" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
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
.panel {
  background: #fff;
  border: 1px solid var(--border);
  border-radius: 8px;
  padding: 16px;
}
.toolbar {
  margin-bottom: 16px;
}
</style>

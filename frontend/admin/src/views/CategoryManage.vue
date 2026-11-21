<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAdminCategoryTree, addCategory, updateCategory, deleteCategory } from '../api/product'

const tree = ref([])
const dialogVisible = ref(false)
const form = ref({ id: null, parentId: 0, name: '', icon: '', sort: 0, status: 1 })

async function load() {
  tree.value = await getAdminCategoryTree()
}

function openAdd() {
  form.value = { id: null, parentId: 0, name: '', icon: '', sort: 0, status: 1 }
  dialogVisible.value = true
}

function openEdit(row) {
  form.value = { id: row.id, parentId: row.parentId, name: row.name, icon: row.icon, sort: row.sort, status: row.status }
  dialogVisible.value = true
}

async function save() {
  if (!form.value.name) {
    ElMessage.warning('请输入分类名')
    return
  }
  if (form.value.id) {
    await updateCategory(form.value.id, form.value)
  } else {
    await addCategory(form.value)
  }
  ElMessage.success('已保存')
  dialogVisible.value = false
  load()
}

async function remove(row) {
  try {
    await ElMessageBox.confirm(`确定删除分类「${row.name}」吗？`, '提示', { type: 'warning' })
  } catch (e) {
    return
  }
  await deleteCategory(row.id)
  ElMessage.success('已删除')
  load()
}

onMounted(load)
</script>

<template>
  <div class="panel">
    <div class="toolbar">
      <el-button type="primary" @click="openAdd">新增分类</el-button>
    </div>
    <el-table :data="tree" row-key="id" default-expand-all :tree-props="{ children: 'children' }">
      <el-table-column prop="name" label="分类名" />
      <el-table-column prop="sort" label="排序" width="100" />
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

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑分类' : '新增分类'" width="460px">
      <el-form label-width="80px">
        <el-form-item label="父分类">
          <el-tree-select
            v-model="form.parentId"
            :data="tree"
            :props="{ label: 'name', value: 'id', children: 'children' }"
            check-strictly
            clearable
            placeholder="不选则为顶级分类"
          />
        </el-form-item>
        <el-form-item label="分类名">
          <el-input v-model="form.name" maxlength="64" />
        </el-form-item>
        <el-form-item label="图标">
          <el-input v-model="form.icon" placeholder="选填" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" />
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

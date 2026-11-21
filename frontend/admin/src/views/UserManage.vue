<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getUserList, updateUserStatus } from '../api/user'

const list = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = 10
const username = ref('')
const status = ref(null)

async function load() {
  const data = await getUserList({
    pageNum: pageNum.value,
    pageSize,
    username: username.value || undefined,
    status: status.value || undefined,
  })
  list.value = data.records
  total.value = data.total
}

async function toggleStatus(row) {
  const next = row.status === 1 ? 0 : 1
  await updateUserStatus(row.id, next)
  ElMessage.success(next === 1 ? '已启用' : '已禁用')
  load()
}

onMounted(load)
</script>

<template>
  <div class="panel">
    <div class="toolbar">
      <el-input v-model="username" placeholder="用户名" clearable style="width: 200px" @keyup.enter="load" />
      <el-select v-model="status" placeholder="状态" clearable style="width: 140px">
        <el-option label="正常" :value="1" />
        <el-option label="禁用" :value="0" />
      </el-select>
      <el-button type="primary" @click="load">查询</el-button>
    </div>
    <el-table :data="list">
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="nickname" label="昵称" />
      <el-table-column prop="phone" label="手机号" />
      <el-table-column label="角色" width="100">
        <template #default="{ row }">
          <el-tag :type="row.role === 1 ? 'danger' : 'info'">{{ row.role === 1 ? '管理员' : '用户' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '正常' : '禁用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="注册时间" />
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button link :type="row.status === 1 ? 'danger' : 'success'" @click="toggleStatus(row)">
            {{ row.status === 1 ? '禁用' : '启用' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="pagination">
      <el-pagination background layout="prev, pager, next" :total="total" :page-size="pageSize" :current-page="pageNum" @current-change="(p) => { pageNum = p; load() }" />
    </div>
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
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}
.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: center;
}
</style>

<script setup>
import { onMounted, ref } from 'vue'
import { getOrderList } from '../api/order'

const list = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = 10
const status = ref(null)
const orderNo = ref('')

async function load() {
  const data = await getOrderList({
    pageNum: pageNum.value,
    pageSize,
    status: status.value || undefined,
    orderNo: orderNo.value || undefined,
  })
  list.value = data.records
  total.value = data.total
}

const statusText = { 10: '待支付', 20: '已支付', 50: '已取消' }

onMounted(load)
</script>

<template>
  <div class="panel">
    <div class="toolbar">
      <el-input v-model="orderNo" placeholder="订单号" clearable style="width: 220px" @keyup.enter="load" />
      <el-select v-model="status" placeholder="状态" clearable style="width: 140px">
        <el-option label="待支付" :value="10" />
        <el-option label="已支付" :value="20" />
        <el-option label="已取消" :value="50" />
      </el-select>
      <el-button type="primary" @click="load">查询</el-button>
    </div>
    <el-table :data="list">
      <el-table-column prop="orderNo" label="订单号" />
      <el-table-column prop="userId" label="用户ID" width="180" />
      <el-table-column label="金额(元)" width="120">
        <template #default="{ row }">¥{{ (row.totalAmount / 100).toFixed(2) }}</template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 20 ? 'success' : row.status === 50 ? 'info' : 'warning'">{{ statusText[row.status] }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" />
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

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getSeckillActivityList, addSeckillActivity, warmupSeckillActivity } from '../api/promotion'

const list = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = 10
const dialogVisible = ref(false)
const form = ref({ skuId: null, seckillPrice: 0, stock: 0, startTime: '', endTime: '' })

async function load() {
  const data = await getSeckillActivityList({ pageNum: pageNum.value, pageSize })
  list.value = data.records
  total.value = data.total
}

async function save() {
  if (!form.value.skuId || !form.value.startTime || !form.value.endTime) {
    ElMessage.warning('请填写完整活动信息')
    return
  }
  await addSeckillActivity(form.value)
  ElMessage.success('活动已创建，请点击「预热」写入库存')
  dialogVisible.value = false
  load()
}

async function warmup(row) {
  await warmupSeckillActivity(row.id)
  ElMessage.success('库存已预热')
}

function openAdd() {
  form.value = { skuId: null, seckillPrice: 0, stock: 0, startTime: '', endTime: '' }
  dialogVisible.value = true
}

onMounted(load)
</script>

<template>
  <div class="panel">
    <div class="toolbar">
      <el-button type="primary" @click="openAdd">新增秒杀活动</el-button>
    </div>
    <el-table :data="list">
      <el-table-column prop="id" label="活动ID" />
      <el-table-column prop="skuId" label="SKU" width="120" />
      <el-table-column label="秒杀价(元)" width="120">
        <template #default="{ row }">¥{{ (row.seckillPrice / 100).toFixed(2) }}</template>
      </el-table-column>
      <el-table-column prop="stock" label="库存" width="100" />
      <el-table-column prop="startTime" label="开始时间" />
      <el-table-column prop="endTime" label="结束时间" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button link type="primary" @click="warmup(row)">预热库存</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="pagination">
      <el-pagination background layout="prev, pager, next" :total="total" :page-size="pageSize" :current-page="pageNum" @current-change="(p) => { pageNum = p; load() }" />
    </div>

    <el-dialog v-model="dialogVisible" title="新增秒杀活动" width="460px">
      <el-form label-width="90px">
        <el-form-item label="SKU ID">
          <el-input-number v-model="form.skuId" :min="1" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item label="秒杀价(分)">
          <el-input-number v-model="form.seckillPrice" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="库存">
          <el-input-number v-model="form.stock" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker v-model="form.startTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker v-model="form.endTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save">创建</el-button>
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
.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: center;
}
</style>

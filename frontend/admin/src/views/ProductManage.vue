<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getAdminProductList, getAdminProductDetail, addProduct, updateProduct, updateProductStatus, deleteProduct,
  addSku, updateSku, updateSkuStatus, deleteSku,
  getAdminCategoryTree, getBrandList,
} from '../api/product'

const list = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = 10
const keyword = ref('')
const categories = ref([])
const brands = ref([])

const dialogVisible = ref(false)
const form = ref(null)

const skuDialogVisible = ref(false)
const skuForm = ref(null)
const tableRef = ref(null)

async function load() {
  const data = await getAdminProductList({ pageNum: pageNum.value, pageSize, keyword: keyword.value || undefined })
  list.value = data.records
  total.value = data.total
}

async function loadMeta() {
  categories.value = await getAdminCategoryTree()
  brands.value = await getBrandList()
}

function blankForm() {
  return { id: null, categoryId: null, brandId: null, name: '', subTitle: '', mainImage: '', detail: '', skus: [{ skuName: '', image: '', price: 0, stock: 0 }] }
}

function openAdd() {
  form.value = blankForm()
  dialogVisible.value = true
}

async function openEdit(row) {
  const detail = await getAdminProductDetail(row.id)
  form.value = {
    id: detail.id,
    categoryId: detail.categoryId,
    brandId: detail.brandId,
    name: detail.name,
    subTitle: detail.subTitle,
    mainImage: detail.mainImage,
    detail: detail.detail,
    skus: detail.skus.map((s) => ({ skuName: s.skuName, image: s.image, price: s.price, stock: s.stock })),
  }
  dialogVisible.value = true
}

function addSkuRow() {
  form.value.skus.push({ skuName: '', image: '', price: 0, stock: 0 })
}

function removeSkuRow(index) {
  form.value.skus.splice(index, 1)
}

async function save() {
  if (!form.value.name || !form.value.categoryId) {
    ElMessage.warning('请填写商品名并选择分类')
    return
  }
  if (!form.value.skus.length) {
    ElMessage.warning('至少需要一个款式')
    return
  }
  if (form.value.id) {
    await updateProduct(form.value.id, form.value)
  } else {
    await addProduct(form.value)
  }
  ElMessage.success('已保存')
  dialogVisible.value = false
  load()
}

async function toggleStatus(row) {
  const next = row.status === 1 ? 0 : 1
  await updateProductStatus(row.id, next)
  ElMessage.success(next === 1 ? '已上架' : '已下架')
  load()
}

async function remove(row) {
  try {
    await ElMessageBox.confirm(`确定删除商品「${row.name}」吗？`, '提示', { type: 'warning' })
  } catch (e) {
    return
  }
  await deleteProduct(row.id)
  ElMessage.success('已删除')
  load()
}

async function handleExpand(row, expandedRows) {
  if (expandedRows.includes(row) && !row.skus) {
    row.skus = (await getAdminProductDetail(row.id)).skus
  }
}

function handleRowClick(row, column) {
  if (!column || column.type === 'expand' || column.label === '操作') return
  tableRef.value.toggleRowExpansion(row)
}

function openAddSku(row) {
  skuForm.value = { productId: row.id, id: null, skuName: '', image: '', price: 0, stock: 0 }
  skuDialogVisible.value = true
}

function openEditSku(row, sku) {
  skuForm.value = { productId: row.id, id: sku.id, skuName: sku.skuName, image: sku.image, price: sku.price, stock: sku.stock }
  skuDialogVisible.value = true
}

async function saveSku() {
  if (!skuForm.value.skuName) {
    ElMessage.warning('请输入款式名')
    return
  }
  if (skuForm.value.id) {
    await updateSku(skuForm.value.id, skuForm.value)
  } else {
    await addSku(skuForm.value.productId, skuForm.value)
  }
  ElMessage.success('已保存')
  skuDialogVisible.value = false
  list.value.forEach((p) => { delete p.skus })
  load()
}

async function toggleSkuStatus(row, sku) {
  const next = sku.status === 1 ? 0 : 1
  await updateSkuStatus(sku.id, next)
  ElMessage.success(next === 1 ? '已上架' : '已下架')
  delete row.skus
  load()
}

async function removeSku(row, sku) {
  try {
    await ElMessageBox.confirm(`确定删除款式「${sku.skuName}」吗？`, '提示', { type: 'warning' })
  } catch (e) {
    return
  }
  await deleteSku(sku.id)
  ElMessage.success('已删除')
  delete row.skus
  load()
}

function search() {
  pageNum.value = 1
  load()
}

onMounted(() => {
  loadMeta()
  load()
})
</script>

<template>
  <div class="panel">
    <div class="toolbar">
      <el-input v-model="keyword" placeholder="搜索商品" clearable style="width: 220px" @keyup.enter="search" />
      <el-button type="primary" @click="search">搜索</el-button>
      <el-button type="primary" plain @click="openAdd">新增商品</el-button>
    </div>
    <el-table ref="tableRef" :data="list" row-key="id" @expand-change="handleExpand" @row-click="handleRowClick">
      <el-table-column type="expand">
        <template #default="{ row }">
          <div class="sku-panel">
            <div class="sku-head">
              <span class="sku-title">款式列表</span>
              <el-button link type="primary" @click="openAddSku(row)">新增款式</el-button>
            </div>
            <el-table :data="row.skus || []" size="small">
              <el-table-column prop="skuName" label="款式名" />
              <el-table-column label="价格(元)" width="120">
                <template #default="{ row: sku }">¥{{ (sku.price / 100).toFixed(2) }}</template>
              </el-table-column>
              <el-table-column prop="stock" label="库存" width="100" />
              <el-table-column label="状态" width="100">
                <template #default="{ row: sku }">
                  <el-tag :type="sku.status === 1 ? 'success' : 'info'">{{ sku.status === 1 ? '上架' : '下架' }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="220">
                <template #default="{ row: sku }">
                  <el-button link type="primary" @click="openEditSku(row, sku)">编辑</el-button>
                  <el-button link :type="sku.status === 1 ? 'warning' : 'success'" @click="toggleSkuStatus(row, sku)">
                    {{ sku.status === 1 ? '下架' : '上架' }}
                  </el-button>
                  <el-button link type="danger" @click="removeSku(row, sku)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="name" label="商品名" />
      <el-table-column prop="subTitle" label="副标题" show-overflow-tooltip />
      <el-table-column label="最低价(元)" width="120">
        <template #default="{ row }">¥{{ (row.price / 100).toFixed(2) }}</template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '上架' : '下架' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="240">
        <template #default="{ row }">
          <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
          <el-button link :type="row.status === 1 ? 'warning' : 'success'" @click="toggleStatus(row)">
            {{ row.status === 1 ? '下架' : '上架' }}
          </el-button>
          <el-button link type="danger" @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="pagination">
      <el-pagination background layout="prev, pager, next" :total="total" :page-size="pageSize" :current-page="pageNum" @current-change="(p) => { pageNum = p; load() }" />
    </div>

    <el-dialog v-model="dialogVisible" :title="form?.id ? '编辑商品' : '新增商品'" width="720px">
      <el-form v-if="form" label-width="80px">
        <el-form-item label="商品名">
          <el-input v-model="form.name" maxlength="128" />
        </el-form-item>
        <el-form-item label="副标题">
          <el-input v-model="form.subTitle" maxlength="255" />
        </el-form-item>
        <el-form-item label="分类">
          <el-tree-select v-model="form.categoryId" :data="categories" :props="{ label: 'name', value: 'id', children: 'children' }" check-strictly />
        </el-form-item>
        <el-form-item label="品牌">
          <el-select v-model="form.brandId" clearable placeholder="选填">
            <el-option v-for="b in brands" :key="b.id" :label="b.name" :value="b.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="主图">
          <el-input v-model="form.mainImage" placeholder="图片 URL" />
        </el-form-item>
        <el-form-item label="详情">
          <el-input v-model="form.detail" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="款式">
          <div class="sku-list">
            <div v-for="(sku, i) in form.skus" :key="i" class="sku-row">
              <el-input v-model="sku.skuName" placeholder="款式名" style="width: 140px" />
              <el-input v-model="sku.image" placeholder="图片 URL" style="width: 180px" />
              <el-input-number v-model="sku.price" :min="0" :step="100" placeholder="价格(分)" style="width: 150px" />
              <el-input-number v-model="sku.stock" :min="0" placeholder="库存" style="width: 130px" />
              <el-button link type="danger" :disabled="form.skus.length <= 1" @click="removeSkuRow(i)">删除</el-button>
            </div>
            <el-button link type="primary" @click="addSkuRow">添加款式</el-button>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="skuDialogVisible" :title="skuForm?.id ? '编辑款式' : '新增款式'" width="460px">
      <el-form v-if="skuForm" label-width="80px">
        <el-form-item label="款式名">
          <el-input v-model="skuForm.skuName" maxlength="128" />
        </el-form-item>
        <el-form-item label="图片">
          <el-input v-model="skuForm.image" placeholder="图片 URL" />
        </el-form-item>
        <el-form-item label="价格(分)">
          <el-input-number v-model="skuForm.price" :min="0" :step="100" style="width: 100%" />
        </el-form-item>
        <el-form-item label="库存">
          <el-input-number v-model="skuForm.stock" :min="0" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="skuDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveSku">保存</el-button>
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
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}
.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: center;
}
.sku-panel {
  padding: 12px 20px;
  background: #fafafa;
}
.sku-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}
.sku-title {
  font-weight: 600;
}
.sku-list {
  width: 100%;
}
.sku-row {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
  align-items: center;
}
</style>

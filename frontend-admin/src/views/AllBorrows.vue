<template>
  <div class="all-borrows-page">
    <div class="card">
      <div class="search-bar">
        <el-select v-model="status" placeholder="借阅状态" clearable style="width: 140px" @change="loadRecords">
          <el-option label="借阅中" :value="0" />
          <el-option label="已归还" :value="1" />
          <el-option label="已逾期" :value="2" />
        </el-select>
      </div>
      
      <el-table :data="records" v-loading="loading" stripe>
        <el-table-column prop="username" label="用户" width="120" />
        <el-table-column prop="bookTitle" label="书名" min-width="180" />
        <el-table-column prop="bookIsbn" label="ISBN" width="160" />
        <el-table-column label="借阅时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.borrowTime) }}
          </template>
        </el-table-column>
        <el-table-column label="应还时间" width="180">
          <template #default="{ row }">
            <span :class="{ 'text-danger': row.status === 0 && isOverdue(row.dueTime) }">
              {{ formatDate(row.dueTime) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="归还时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.returnTime) }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row)">
              {{ getStatusText(row) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button 
              v-if="row.status === 0" 
              type="success" 
              size="small" 
              @click="handleConfirmReturn(row)"
            >
              确认归还
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @change="loadRecords"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAllBorrows, confirmReturn } from '@/api/borrow'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const records = ref([])
const total = ref(0)
const status = ref(null)
const pageNum = ref(1)
const pageSize = ref(10)

onMounted(() => {
  loadRecords()
})

async function loadRecords() {
  loading.value = true
  try {
    const params = { pageNum: pageNum.value, pageSize: pageSize.value }
    if (status.value !== null) {
      params.status = status.value
    }
    const res = await getAllBorrows(params)
    records.value = res.data.list
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function formatDate(dateStr) {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN')
}

function isOverdue(dueTime) {
  return new Date(dueTime) < new Date()
}

function getStatusType(row) {
  if (row.status === 1) return 'success'
  if (row.status === 0 && isOverdue(row.dueTime)) return 'danger'
  return 'primary'
}

function getStatusText(row) {
  if (row.status === 1) return '已归还'
  if (row.status === 0 && isOverdue(row.dueTime)) return '已逾期'
  return '借阅中'
}

async function handleConfirmReturn(record) {
  await ElMessageBox.confirm(`确定确认归还《${record.bookTitle}》吗？`, '提示', { type: 'info' })
  await confirmReturn(record.id)
  ElMessage.success('确认成功')
  loadRecords()
}
</script>

<style lang="scss" scoped>
.text-danger {
  color: #F56C6C;
}
</style>

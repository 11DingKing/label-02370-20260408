<template>
  <div class="my-borrows-page">
    <div class="card">
      <el-table :data="records" v-loading="loading" stripe>
        <el-table-column prop="bookTitle" label="书名" min-width="180" />
        <el-table-column prop="bookAuthor" label="作者" width="120" />
        <el-table-column prop="bookIsbn" label="ISBN" width="160" />
        <el-table-column label="借阅时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.borrowTime) }}
          </template>
        </el-table-column>
        <el-table-column label="应还时间" width="180">
          <template #default="{ row }">
            <span :class="{ 'text-danger': isOverdue(row.dueTime) }">
              {{ formatDate(row.dueTime) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="isOverdue(row.dueTime) ? 'danger' : 'primary'">
              {{ isOverdue(row.dueTime) ? '已逾期' : '借阅中' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button type="success" size="small" @click="handleReturn(row)">归还</el-button>
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
import { getCurrentBorrows, returnBook } from '@/api/borrow'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const records = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

onMounted(() => {
  loadRecords()
})

async function loadRecords() {
  loading.value = true
  try {
    const res = await getCurrentBorrows({ pageNum: pageNum.value, pageSize: pageSize.value })
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

async function handleReturn(record) {
  await ElMessageBox.confirm(`确定归还《${record.bookTitle}》吗？`, '提示', { type: 'info' })
  await returnBook(record.id)
  ElMessage.success('归还成功')
  loadRecords()
}
</script>

<style lang="scss" scoped>
.text-danger {
  color: #F56C6C;
}
</style>

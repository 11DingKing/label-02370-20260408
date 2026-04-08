<template>
  <div class="borrow-history-page">
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
            {{ formatDate(row.dueTime) }}
          </template>
        </el-table-column>
        <el-table-column label="归还时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.returnTime) }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ row.statusText }}
            </el-tag>
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
import { getBorrowHistory } from '@/api/borrow'

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
    const res = await getBorrowHistory({ pageNum: pageNum.value, pageSize: pageSize.value })
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

function getStatusType(status) {
  const types = { 0: 'primary', 1: 'success', 2: 'danger' }
  return types[status] || 'info'
}
</script>

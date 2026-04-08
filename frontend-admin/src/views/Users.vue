<template>
  <div class="users-page">
    <div class="card">
      <div class="search-bar">
        <el-input v-model="keyword" placeholder="搜索用户名/邮箱" clearable style="width: 240px" @keyup.enter="loadUsers" />
        <el-button type="primary" @click="loadUsers">搜索</el-button>
      </div>
      
      <el-table :data="users" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="140" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column prop="phone" label="手机号" width="140" />
        <el-table-column label="角色" width="100">
          <template #default="{ row }">
            <el-tag :type="row.role === 1 ? 'danger' : 'info'">
              {{ row.role === 1 ? '管理员' : '普通用户' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="注册时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button 
              v-if="row.status === 1" 
              type="danger" 
              size="small" 
              @click="handleToggleStatus(row, 0)"
            >
              禁用
            </el-button>
            <el-button 
              v-else 
              type="success" 
              size="small" 
              @click="handleToggleStatus(row, 1)"
            >
              启用
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
          @change="loadUsers"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getUsers, updateUserStatus } from '@/api/user'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const users = ref([])
const total = ref(0)
const keyword = ref('')
const pageNum = ref(1)
const pageSize = ref(10)

onMounted(() => {
  loadUsers()
})

async function loadUsers() {
  loading.value = true
  try {
    const res = await getUsers({ keyword: keyword.value, pageNum: pageNum.value, pageSize: pageSize.value })
    users.value = res.data.list
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function formatDate(dateStr) {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN')
}

async function handleToggleStatus(user, status) {
  const action = status === 1 ? '启用' : '禁用'
  await ElMessageBox.confirm(`确定${action}用户 ${user.username} 吗？`, '提示', { type: 'warning' })
  await updateUserStatus(user.id, status)
  ElMessage.success(`${action}成功`)
  loadUsers()
}
</script>

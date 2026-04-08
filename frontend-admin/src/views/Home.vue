<template>
  <div class="home-page">
    <div class="welcome-card">
      <h1>{{ getGreeting() }}，{{ userStore.user?.username }}！</h1>
      <p>{{ userStore.isAdmin ? '您是管理员，拥有系统全部管理权限' : '欢迎使用图书管理系统' }}</p>
    </div>
    
    <div class="stats-row">
      <div class="stat-card">
        <div class="stat-icon blue"><el-icon :size="22"><Document /></el-icon></div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.totalBooks }}</div>
          <div class="stat-label">图书总数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon purple"><el-icon :size="22"><Star /></el-icon></div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.newBooks }}</div>
          <div class="stat-label">新书数量</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon orange"><el-icon :size="22"><Collection /></el-icon></div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.currentBorrows }}</div>
          <div class="stat-label">当前借阅</div>
        </div>
      </div>
    </div>
    
    <div class="content-row">
      <div class="section-card">
        <div class="section-header">
          <h3>新书推荐</h3>
          <el-button type="primary" link size="small" @click="router.push('/new-books')">查看更多 →</el-button>
        </div>
        <div class="book-list" v-if="newBooks.length > 0">
          <div class="book-item" v-for="book in newBooks" :key="book.id">
            <div class="book-main">
              <span class="book-title">{{ book.title }}</span>
              <span class="book-author">{{ book.author }}</span>
            </div>
            <el-tag size="small" :type="book.availableCount > 0 ? 'success' : 'danger'">
              {{ book.availableCount > 0 ? '可借' : '已借完' }}
            </el-tag>
          </div>
        </div>
        <el-empty v-else description="暂无新书" :image-size="50" />
      </div>
      
      <div class="section-card">
        <div class="section-header">
          <h3>快捷操作</h3>
        </div>
        <div class="quick-grid">
          <div class="quick-item" @click="router.push('/books')">
            <el-icon :size="24" color="#1890ff"><Search /></el-icon>
            <span>搜索图书</span>
          </div>
          <div class="quick-item" @click="router.push('/my-borrows')">
            <el-icon :size="24" color="#722ed1"><Collection /></el-icon>
            <span>我的借阅</span>
          </div>
          <div class="quick-item" @click="router.push('/borrow-history')">
            <el-icon :size="24" color="#fa8c16"><Clock /></el-icon>
            <span>借阅记录</span>
          </div>
          <div class="quick-item" @click="router.push('/new-books')">
            <el-icon :size="24" color="#52c41a"><Star /></el-icon>
            <span>新书推荐</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getBooks, getNewBooks } from '@/api/book'
import { getCurrentBorrows } from '@/api/borrow'
import { Document, Star, Collection, Search, Clock } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const stats = ref({ totalBooks: 0, newBooks: 0, currentBorrows: 0 })
const newBooks = ref([])

function getGreeting() {
  const hour = new Date().getHours()
  if (hour < 12) return '早上好'
  if (hour < 18) return '下午好'
  return '晚上好'
}

onMounted(async () => {
  try {
    const [booksRes, newBooksRes, borrowsRes] = await Promise.all([
      getBooks({ pageNum: 1, pageSize: 1 }),
      getNewBooks(),
      getCurrentBorrows({ pageNum: 1, pageSize: 1 })
    ])
    stats.value.totalBooks = booksRes.data.total
    stats.value.newBooks = newBooksRes.data.length
    stats.value.currentBorrows = borrowsRes.data.total
    newBooks.value = newBooksRes.data.slice(0, 5)
  } catch (e) {
    console.error(e)
  }
})
</script>

<style lang="scss" scoped>
.home-page {
  height: 100%;
}

.welcome-card {
  background: linear-gradient(135deg, #1890ff 0%, #722ed1 100%);
  border-radius: 8px;
  padding: 24px;
  margin-bottom: 16px;
  color: #fff;
  
  h1 {
    font-size: 20px;
    font-weight: 600;
    margin: 0 0 6px;
  }
  
  p {
    font-size: 13px;
    opacity: 0.9;
    margin: 0;
  }
}

.stats-row {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-bottom: 16px;
}

.stat-card {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  display: flex;
  align-items: center;
  gap: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
  
  .stat-icon {
    width: 44px;
    height: 44px;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
    
    &.blue { background: #1890ff; }
    &.purple { background: #722ed1; }
    &.orange { background: #fa8c16; }
  }
  
  .stat-value {
    font-size: 24px;
    font-weight: 700;
    color: #1f2937;
    line-height: 1;
  }
  
  .stat-label {
    font-size: 13px;
    color: #6b7280;
    margin-top: 4px;
  }
}

.content-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.section-card {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  
  h3 {
    font-size: 15px;
    font-weight: 600;
    color: #1f2937;
    margin: 0;
  }
}

.book-list {
  .book-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 10px 0;
    border-bottom: 1px solid #f3f4f6;
    
    &:last-child { border-bottom: none; }
  }
  
  .book-main {
    display: flex;
    flex-direction: column;
    gap: 2px;
    min-width: 0;
    flex: 1;
    margin-right: 12px;
  }
  
  .book-title {
    font-size: 13px;
    font-weight: 500;
    color: #1f2937;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
  
  .book-author {
    font-size: 12px;
    color: #9ca3af;
  }
}

.quick-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
}

.quick-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 16px 12px;
  background: #f9fafb;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
  
  &:hover {
    background: #f3f4f6;
  }
  
  span {
    font-size: 12px;
    color: #4b5563;
  }
}

@media (max-width: 768px) {
  .stats-row { grid-template-columns: 1fr; }
  .content-row { grid-template-columns: 1fr; }
}
</style>

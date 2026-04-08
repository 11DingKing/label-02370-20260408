<template>
  <div class="new-books-page">
    <div class="page-header">
      <h2>新书推荐</h2>
      <span class="page-desc">为您精选最新上架的优质图书</span>
    </div>
    
    <div class="books-grid" v-loading="loading">
      <div class="book-card" v-for="book in books" :key="book.id">
        <div class="book-tags">
          <span class="book-badge">NEW</span>
          <span class="book-category">{{ book.category || '未分类' }}</span>
        </div>
        <h3 class="book-title" :title="book.title">{{ book.title }}</h3>
        <div class="book-meta">
          <span>{{ book.author }}</span>
          <span class="dot">·</span>
          <span>{{ book.publisher }}</span>
        </div>
        <p class="book-desc" v-if="book.description">{{ book.description }}</p>
        <div class="book-footer">
          <div class="stock">
            <span class="label">库存</span>
            <span class="value" :class="{ low: book.availableCount < 3 }">
              {{ book.availableCount }}/{{ book.totalCount }}
            </span>
          </div>
          <el-button 
            type="primary" 
            size="small"
            :disabled="book.availableCount === 0" 
            @click="handleBorrow(book)"
          >
            {{ book.availableCount === 0 ? '暂无库存' : '立即借阅' }}
          </el-button>
        </div>
      </div>
    </div>
    
    <el-empty v-if="!loading && books.length === 0" description="暂无新书推荐" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getNewBooks } from '@/api/book'
import { borrowBook } from '@/api/borrow'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const books = ref([])

onMounted(() => {
  loadBooks()
})

async function loadBooks() {
  loading.value = true
  try {
    const res = await getNewBooks()
    books.value = res.data
  } finally {
    loading.value = false
  }
}

async function handleBorrow(book) {
  await ElMessageBox.confirm(`确定借阅《${book.title}》吗？`, '确认借阅', { type: 'info' })
  await borrowBook({ bookId: book.id })
  ElMessage.success('借阅成功')
  loadBooks()
}
</script>

<style lang="scss" scoped>
.new-books-page {
  height: 100%;
}

.page-header {
  margin-bottom: 16px;
  display: flex;
  align-items: baseline;
  gap: 12px;
  
  h2 {
    font-size: 18px;
    font-weight: 600;
    color: #1f2937;
    margin: 0;
  }
  
  .page-desc {
    font-size: 13px;
    color: #9ca3af;
  }
}

.books-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 16px;
}

.book-card {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
  transition: box-shadow 0.2s;
  
  &:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
  }
}

.book-tags {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
}

.book-badge {
  background: #ff4d4f;
  color: #fff;
  font-size: 10px;
  font-weight: 600;
  padding: 3px 8px;
  border-radius: 3px;
  line-height: 1;
}

.book-category {
  font-size: 11px;
  color: #1890ff;
  background: rgba(24, 144, 255, 0.1);
  padding: 3px 8px;
  border-radius: 3px;
  line-height: 1;
}

.book-title {
  font-size: 15px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 8px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.book-meta {
  font-size: 13px;
  color: #6b7280;
  margin-bottom: 8px;
  
  .dot {
    margin: 0 6px;
    color: #d1d5db;
  }
}

.book-desc {
  font-size: 12px;
  color: #9ca3af;
  line-height: 1.5;
  margin: 0 0 12px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.book-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #f3f4f6;
}

.stock {
  .label {
    font-size: 12px;
    color: #9ca3af;
    margin-right: 4px;
  }
  
  .value {
    font-size: 14px;
    font-weight: 600;
    color: #10b981;
    
    &.low {
      color: #ef4444;
    }
  }
}
</style>

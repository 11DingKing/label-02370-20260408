<template>
  <div class="books-page">
    <div class="card">
      <div class="search-bar">
        <el-input v-model="query.keyword" placeholder="搜索书名/作者/ISBN" clearable style="width: 240px" @keyup.enter="loadBooks" />
        <el-select v-model="query.category" placeholder="分类" clearable style="width: 140px">
          <el-option label="计算机" value="计算机" />
          <el-option label="文学" value="文学" />
          <el-option label="历史" value="历史" />
          <el-option label="科学" value="科学" />
        </el-select>
        <el-button type="primary" @click="loadBooks">搜索</el-button>
        <el-button v-if="userStore.isAdmin" type="success" @click="openDialog()">新增图书</el-button>
      </div>
      
      <el-table :data="books" v-loading="loading" stripe>
        <el-table-column prop="isbn" label="ISBN" width="160" />
        <el-table-column prop="title" label="书名" min-width="180" />
        <el-table-column prop="author" label="作者" width="120" />
        <el-table-column prop="category" label="分类" width="100" />
        <el-table-column prop="publisher" label="出版社" width="140" />
        <el-table-column label="库存" width="100">
          <template #default="{ row }">
            <span :class="{ 'text-danger': row.availableCount === 0 }">
              {{ row.availableCount }} / {{ row.totalCount }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" :disabled="row.availableCount === 0" @click="handleBorrow(row)">
              借阅
            </el-button>
            <template v-if="userStore.isAdmin">
              <el-button type="warning" size="small" @click="openDialog(row)">编辑</el-button>
              <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
            </template>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="query.pageNum"
          v-model:page-size="query.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @change="loadBooks"
        />
      </div>
    </div>
    
    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="editingBook ? '编辑图书' : '新增图书'" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="ISBN" prop="isbn">
          <el-input v-model="form.isbn" placeholder="请输入ISBN" />
        </el-form-item>
        <el-form-item label="书名" prop="title">
          <el-input v-model="form.title" placeholder="请输入书名" />
        </el-form-item>
        <el-form-item label="作者" prop="author">
          <el-input v-model="form.author" placeholder="请输入作者" />
        </el-form-item>
        <el-form-item label="出版社" prop="publisher">
          <el-input v-model="form.publisher" placeholder="请输入出版社" />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select v-model="form.category" placeholder="请选择分类" style="width: 100%">
            <el-option label="计算机" value="计算机" />
            <el-option label="文学" value="文学" />
            <el-option label="历史" value="历史" />
            <el-option label="科学" value="科学" />
          </el-select>
        </el-form-item>
        <el-form-item label="总数量" prop="totalCount">
          <el-input-number v-model="form.totalCount" :min="1" />
        </el-form-item>
        <el-form-item label="是否新书" prop="isNew">
          <el-switch v-model="form.isNew" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="简介" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入简介" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { getBooks, addBook, updateBook, deleteBook } from '@/api/book'
import { borrowBook } from '@/api/borrow'
import { ElMessage, ElMessageBox } from 'element-plus'

const userStore = useUserStore()

const loading = ref(false)
const books = ref([])
const total = ref(0)

const query = reactive({
  keyword: '',
  category: '',
  pageNum: 1,
  pageSize: 10
})

const dialogVisible = ref(false)
const editingBook = ref(null)
const formRef = ref()
const submitting = ref(false)

const form = reactive({
  isbn: '',
  title: '',
  author: '',
  publisher: '',
  category: '',
  totalCount: 1,
  isNew: 0,
  description: ''
})

const rules = {
  isbn: [{ required: true, message: '请输入ISBN', trigger: 'blur' }],
  title: [{ required: true, message: '请输入书名', trigger: 'blur' }],
  author: [{ required: true, message: '请输入作者', trigger: 'blur' }],
  totalCount: [{ required: true, message: '请输入总数量', trigger: 'blur' }]
}

onMounted(() => {
  loadBooks()
})

async function loadBooks() {
  loading.value = true
  try {
    const res = await getBooks(query)
    books.value = res.data.list
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function openDialog(book = null) {
  editingBook.value = book
  if (book) {
    Object.assign(form, book)
  } else {
    Object.assign(form, {
      isbn: '',
      title: '',
      author: '',
      publisher: '',
      category: '',
      totalCount: 1,
      isNew: 0,
      description: ''
    })
  }
  dialogVisible.value = true
}

async function handleSubmit() {
  await formRef.value.validate()
  submitting.value = true
  try {
    if (editingBook.value) {
      await updateBook(editingBook.value.id, form)
      ElMessage.success('编辑成功')
    } else {
      await addBook(form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadBooks()
  } finally {
    submitting.value = false
  }
}

async function handleDelete(book) {
  await ElMessageBox.confirm(`确定删除《${book.title}》吗？`, '提示', { type: 'warning' })
  await deleteBook(book.id)
  ElMessage.success('删除成功')
  loadBooks()
}

async function handleBorrow(book) {
  await ElMessageBox.confirm(`确定借阅《${book.title}》吗？`, '提示', { type: 'info' })
  await borrowBook({ bookId: book.id })
  ElMessage.success('借阅成功')
  loadBooks()
}
</script>

<style lang="scss" scoped>
.text-danger {
  color: #F56C6C;
}
</style>

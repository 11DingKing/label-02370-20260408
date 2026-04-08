<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-card">
        <div class="login-header">
          <h1>📚 图书管理系统</h1>
          <p>请登录您的账号</p>
        </div>
        
        <el-form 
          ref="formRef" 
          :model="form" 
          :rules="rules" 
          class="login-form"
          @submit.prevent="handleLogin"
        >
          <el-form-item prop="username">
            <el-input 
              v-model="form.username" 
              placeholder="用户名"
              size="large"
              :prefix-icon="User"
            />
          </el-form-item>
          
          <el-form-item prop="password">
            <el-input 
              v-model="form.password" 
              type="password" 
              placeholder="密码"
              size="large"
              :prefix-icon="Lock"
              show-password
              @keyup.enter="handleLogin"
            />
          </el-form-item>
          
          <el-form-item>
            <el-button 
              type="primary" 
              size="large" 
              :loading="loading" 
              class="login-btn"
              @click="handleLogin"
            >
              登 录
            </el-button>
          </el-form-item>
        </el-form>
        
        <div class="login-footer">
          <span>还没有账号？</span>
          <router-link to="/register">立即注册</router-link>
        </div>
        
        <div class="demo-accounts">
          <div class="demo-title">演示账号</div>
          <div class="demo-list">
            <div class="demo-item" @click="fillDemo('admin', 'admin123')">
              <span class="demo-role">管理员</span>
              <span class="demo-info">admin / admin123</span>
            </div>
            <div class="demo-item" @click="fillDemo('user', 'user123')">
              <span class="demo-role">普通用户</span>
              <span class="demo-info">user / user123</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref()
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

function fillDemo(username, password) {
  form.username = username
  form.password = password
}

async function handleLogin() {
  await formRef.value.validate()
  loading.value = true
  try {
    await userStore.login(form.username, form.password)
    ElMessage.success('登录成功')
    router.push('/home')
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-container {
  width: 100%;
  max-width: 400px;
}

.login-card {
  background: #fff;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
  
  h1 {
    font-size: 24px;
    font-weight: 600;
    color: #1f2937;
    margin: 0 0 8px;
  }
  
  p {
    font-size: 14px;
    color: #6b7280;
    margin: 0;
  }
}

.login-form {
  .el-form-item {
    margin-bottom: 20px;
  }
}

.login-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
}

.login-footer {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: #6b7280;
  
  a {
    color: #1890ff;
    margin-left: 4px;
    
    &:hover {
      text-decoration: underline;
    }
  }
}

.demo-accounts {
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #e5e7eb;
  
  .demo-title {
    font-size: 12px;
    color: #9ca3af;
    text-align: center;
    margin-bottom: 12px;
  }
  
  .demo-list {
    display: flex;
    gap: 12px;
  }
  
  .demo-item {
    flex: 1;
    padding: 12px;
    background: #f9fafb;
    border-radius: 8px;
    cursor: pointer;
    text-align: center;
    transition: all 0.2s;
    
    &:hover {
      background: #f3f4f6;
    }
    
    .demo-role {
      display: block;
      font-size: 12px;
      color: #1890ff;
      margin-bottom: 4px;
    }
    
    .demo-info {
      display: block;
      font-size: 11px;
      color: #9ca3af;
    }
  }
}
</style>

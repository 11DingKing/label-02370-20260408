<template>
  <el-container class="layout-container">
    <el-aside :width="isCollapse ? '64px' : '200px'" class="sidebar">
      <div class="logo">
        <span class="logo-icon">📚</span>
        <span v-if="!isCollapse" class="logo-text">图书管理</span>
      </div>
      
      <el-menu
        :default-active="route.path"
        :collapse="isCollapse"
        :collapse-transition="false"
        router
        class="sidebar-menu"
      >
        <el-menu-item index="/home">
          <el-icon><HomeFilled /></el-icon>
          <template #title>首页</template>
        </el-menu-item>
        <el-menu-item index="/new-books">
          <el-icon><Star /></el-icon>
          <template #title>新书推荐</template>
        </el-menu-item>
        <el-menu-item index="/books">
          <el-icon><Document /></el-icon>
          <template #title>图书借阅</template>
        </el-menu-item>
        <el-menu-item index="/my-borrows">
          <el-icon><Collection /></el-icon>
          <template #title>当前借阅</template>
        </el-menu-item>
        <el-menu-item index="/borrow-history">
          <el-icon><Clock /></el-icon>
          <template #title>借阅记录</template>
        </el-menu-item>
        
        <template v-if="userStore.isAdmin">
          <div class="menu-divider" v-if="!isCollapse"></div>
          <el-menu-item index="/users">
            <el-icon><User /></el-icon>
            <template #title>用户管理</template>
          </el-menu-item>
          <el-menu-item index="/all-borrows">
            <el-icon><List /></el-icon>
            <template #title>借阅管理</template>
          </el-menu-item>
        </template>
      </el-menu>
      
      <div class="sidebar-bottom">
        <div class="collapse-btn" @click="isCollapse = !isCollapse">
          <el-icon v-if="isCollapse"><Expand /></el-icon>
          <el-icon v-else><Fold /></el-icon>
        </div>
      </div>
    </el-aside>
    
    <el-container class="main-wrapper">
      <el-header class="header">
        <div class="header-left">
          <span class="current-page">{{ route.meta.title || '首页' }}</span>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand" trigger="click">
            <div class="user-info">
              <el-avatar :size="32" class="user-avatar">
                {{ userStore.user?.username?.charAt(0).toUpperCase() }}
              </el-avatar>
              <span class="user-name">{{ userStore.user?.username }}</span>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item disabled>
                  {{ userStore.isAdmin ? '管理员' : '普通用户' }}
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { logout } from '@/api/auth'
import { ElMessage } from 'element-plus'
import { 
  HomeFilled, Star, Document, Collection, Clock, 
  User, List, Expand, Fold, ArrowDown 
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const isCollapse = ref(false)

async function handleCommand(command) {
  if (command === 'logout') {
    try {
      await logout()
    } catch (e) {}
    userStore.logout()
    ElMessage.success('已退出登录')
    router.push('/login')
  }
}
</script>

<style lang="scss" scoped>
.layout-container {
  height: 100vh;
  background: #f5f7fa;
}

.sidebar {
  background: #001529;
  display: flex;
  flex-direction: column;
  transition: width 0.2s;
  overflow: hidden;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  flex-shrink: 0;
  
  .logo-icon {
    font-size: 24px;
  }
  
  .logo-text {
    white-space: nowrap;
  }
}

.sidebar-menu {
  flex: 1;
  border-right: none !important;
  background: transparent !important;
  overflow-y: auto;
  overflow-x: hidden;
  
  &:not(.el-menu--collapse) {
    width: 200px;
  }
  
  // 展开状态
  :deep(.el-menu-item) {
    height: 50px;
    line-height: 50px;
    color: rgba(255, 255, 255, 0.65);
    
    .el-icon {
      color: rgba(255, 255, 255, 0.65);
    }
    
    &:hover {
      color: #fff;
      background: rgba(255, 255, 255, 0.08);
      
      .el-icon {
        color: #fff;
      }
    }
    
    &.is-active {
      color: #fff;
      background: #1890ff;
      
      .el-icon {
        color: #fff;
      }
    }
  }
  
  // 折叠状态
  &.el-menu--collapse {
    width: 64px;
    
    :deep(.el-menu-item) {
      padding: 0 !important;
      display: flex;
      justify-content: center;
      
      .el-icon {
        margin: 0;
      }
    }
    
    :deep(.el-sub-menu__icon-arrow) {
      display: none;
    }
  }
}

.menu-divider {
  height: 1px;
  background: rgba(255, 255, 255, 0.1);
  margin: 12px 16px;
}

.sidebar-bottom {
  padding: 12px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  flex-shrink: 0;
}

.collapse-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 36px;
  color: rgba(255, 255, 255, 0.65);
  cursor: pointer;
  border-radius: 4px;
  
  &:hover {
    color: #fff;
    background: rgba(255, 255, 255, 0.08);
  }
}

.main-wrapper {
  flex-direction: column;
  overflow: hidden;
}

.header {
  height: 60px;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  flex-shrink: 0;
}

.header-left {
  .current-page {
    font-size: 16px;
    font-weight: 500;
    color: #1f2937;
  }
}

.header-right {
  .user-info {
    display: flex;
    align-items: center;
    gap: 8px;
    cursor: pointer;
    padding: 4px 8px;
    border-radius: 4px;
    
    &:hover {
      background: #f5f7fa;
    }
  }
  
  .user-avatar {
    background: #1890ff;
    color: #fff;
  }
  
  .user-name {
    font-size: 14px;
    color: #1f2937;
  }
}

.main-content {
  background: #f5f7fa;
  padding: 16px;
  overflow-y: auto;
}
</style>

import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('@/views/Layout.vue'),
    redirect: '/home',
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('@/views/Home.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'books',
        name: 'Books',
        component: () => import('@/views/Books.vue'),
        meta: { title: '图书管理' }
      },
      {
        path: 'new-books',
        name: 'NewBooks',
        component: () => import('@/views/NewBooks.vue'),
        meta: { title: '新书推荐' }
      },
      {
        path: 'my-borrows',
        name: 'MyBorrows',
        component: () => import('@/views/MyBorrows.vue'),
        meta: { title: '当前借阅' }
      },
      {
        path: 'borrow-history',
        name: 'BorrowHistory',
        component: () => import('@/views/BorrowHistory.vue'),
        meta: { title: '借阅记录' }
      },
      {
        path: 'users',
        name: 'Users',
        component: () => import('@/views/Users.vue'),
        meta: { title: '用户管理', requiresAdmin: true }
      },
      {
        path: 'all-borrows',
        name: 'AllBorrows',
        component: () => import('@/views/AllBorrows.vue'),
        meta: { title: '借阅管理', requiresAdmin: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  if (to.meta.requiresAuth === false) {
    next()
    return
  }
  
  if (!userStore.token) {
    next('/login')
    return
  }
  
  if (to.meta.requiresAdmin && userStore.user?.role !== 1) {
    next('/home')
    return
  }
  
  next()
})

export default router

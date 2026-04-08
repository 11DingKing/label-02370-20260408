import { describe, it, expect, beforeEach, vi } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useUserStore } from '@/stores/user'

// Mock localStorage
const localStorageMock = {
  store: {},
  getItem: vi.fn((key) => localStorageMock.store[key] || null),
  setItem: vi.fn((key, value) => { localStorageMock.store[key] = value }),
  removeItem: vi.fn((key) => { delete localStorageMock.store[key] }),
  clear: vi.fn(() => { localStorageMock.store = {} })
}
Object.defineProperty(window, 'localStorage', { value: localStorageMock })

// Mock API
vi.mock('@/api/auth', () => ({
  login: vi.fn(),
  register: vi.fn(),
  getUserInfo: vi.fn()
}))

import { login as loginApi, register as registerApi, getUserInfo } from '@/api/auth'

describe('User Store', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    localStorageMock.clear()
    vi.clearAllMocks()
  })

  it('初始状态应为空', () => {
    const store = useUserStore()
    expect(store.token).toBe('')
    expect(store.user).toBeNull()
    expect(store.isAdmin).toBe(false)
  })

  it('登录成功应设置token和user', async () => {
    const mockResponse = {
      data: {
        token: 'test-token',
        user: { id: 1, username: 'testuser', role: 0 }
      }
    }
    loginApi.mockResolvedValue(mockResponse)

    const store = useUserStore()
    await store.login('testuser', 'password123')

    expect(store.token).toBe('test-token')
    expect(store.user.username).toBe('testuser')
    expect(localStorageMock.setItem).toHaveBeenCalledWith('token', 'test-token')
  })

  it('管理员角色应返回isAdmin为true', async () => {
    const mockResponse = {
      data: {
        token: 'admin-token',
        user: { id: 1, username: 'admin', role: 1 }
      }
    }
    loginApi.mockResolvedValue(mockResponse)

    const store = useUserStore()
    await store.login('admin', 'password123')

    expect(store.isAdmin).toBe(true)
  })

  it('普通用户应返回isAdmin为false', async () => {
    const mockResponse = {
      data: {
        token: 'user-token',
        user: { id: 1, username: 'user', role: 0 }
      }
    }
    loginApi.mockResolvedValue(mockResponse)

    const store = useUserStore()
    await store.login('user', 'password123')

    expect(store.isAdmin).toBe(false)
  })

  it('注册应调用API', async () => {
    registerApi.mockResolvedValue({ code: 200 })

    const store = useUserStore()
    await store.register({
      username: 'newuser',
      password: 'password123',
      email: 'test@example.com'
    })

    expect(registerApi).toHaveBeenCalledWith({
      username: 'newuser',
      password: 'password123',
      email: 'test@example.com'
    })
  })

  it('退出登录应清除状态', async () => {
    const mockResponse = {
      data: {
        token: 'test-token',
        user: { id: 1, username: 'testuser', role: 0 }
      }
    }
    loginApi.mockResolvedValue(mockResponse)

    const store = useUserStore()
    await store.login('testuser', 'password123')
    
    store.logout()

    expect(store.token).toBe('')
    expect(store.user).toBeNull()
    expect(localStorageMock.removeItem).toHaveBeenCalledWith('token')
    expect(localStorageMock.removeItem).toHaveBeenCalledWith('user')
  })

  it('获取用户信息应更新user', async () => {
    const mockUserInfo = {
      data: { id: 1, username: 'testuser', email: 'test@example.com', role: 0 }
    }
    getUserInfo.mockResolvedValue(mockUserInfo)

    const store = useUserStore()
    await store.fetchUserInfo()

    expect(store.user.email).toBe('test@example.com')
  })
})

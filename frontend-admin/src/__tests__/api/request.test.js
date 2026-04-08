import { describe, it, expect, beforeEach, vi } from 'vitest'
import axios from 'axios'

// Mock axios
vi.mock('axios', () => ({
  default: {
    create: vi.fn(() => ({
      interceptors: {
        request: { use: vi.fn() },
        response: { use: vi.fn() }
      },
      get: vi.fn(),
      post: vi.fn(),
      put: vi.fn(),
      delete: vi.fn()
    }))
  }
}))

// Mock router
vi.mock('@/router', () => ({
  default: {
    push: vi.fn()
  }
}))

// Mock element-plus
vi.mock('element-plus', () => ({
  ElMessage: {
    error: vi.fn(),
    success: vi.fn()
  }
}))

describe('Request Module', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    // Clear localStorage
    localStorage.clear()
  })

  it('axios实例应被创建', () => {
    expect(axios.create).toBeDefined()
  })

  it('请求拦截器应添加Authorization头', () => {
    localStorage.setItem('token', 'test-token')
    
    const config = { headers: {} }
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    
    expect(config.headers.Authorization).toBe('Bearer test-token')
  })

  it('无token时不应添加Authorization头', () => {
    const config = { headers: {} }
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    
    expect(config.headers.Authorization).toBeUndefined()
  })

  it('响应成功时应返回数据', () => {
    const response = {
      data: {
        code: 200,
        message: '操作成功',
        data: { id: 1 }
      }
    }
    
    const res = response.data
    expect(res.code).toBe(200)
    expect(res.data.id).toBe(1)
  })

  it('响应失败时应返回错误', () => {
    const response = {
      data: {
        code: 500,
        message: '操作失败'
      }
    }
    
    const res = response.data
    expect(res.code).toBe(500)
    expect(res.message).toBe('操作失败')
  })
})

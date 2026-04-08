import { describe, it, expect } from 'vitest'

// 验证函数
const validators = {
  isValidUsername: (username) => {
    if (!username) return false
    return username.length >= 3 && username.length <= 20
  },
  
  isValidPassword: (password) => {
    if (!password) return false
    return password.length >= 6 && password.length <= 20
  },
  
  isValidEmail: (email) => {
    if (!email) return true // 邮箱可选
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    return emailRegex.test(email)
  },
  
  isValidPhone: (phone) => {
    if (!phone) return true // 手机号可选
    const phoneRegex = /^1[3-9]\d{9}$/
    return phoneRegex.test(phone)
  },
  
  isValidIsbn: (isbn) => {
    if (!isbn) return false
    // 简单的ISBN格式验证
    const isbnRegex = /^978-\d{1,5}-\d{1,7}-\d{1,7}-\d{1}$/
    return isbnRegex.test(isbn)
  }
}

describe('Validators', () => {
  describe('isValidUsername', () => {
    it('有效用户名应返回true', () => {
      expect(validators.isValidUsername('testuser')).toBe(true)
      expect(validators.isValidUsername('abc')).toBe(true)
      expect(validators.isValidUsername('a'.repeat(20))).toBe(true)
    })

    it('无效用户名应返回false', () => {
      expect(validators.isValidUsername('')).toBe(false)
      expect(validators.isValidUsername('ab')).toBe(false)
      expect(validators.isValidUsername('a'.repeat(21))).toBe(false)
      expect(validators.isValidUsername(null)).toBe(false)
    })
  })

  describe('isValidPassword', () => {
    it('有效密码应返回true', () => {
      expect(validators.isValidPassword('password123')).toBe(true)
      expect(validators.isValidPassword('123456')).toBe(true)
      expect(validators.isValidPassword('a'.repeat(20))).toBe(true)
    })

    it('无效密码应返回false', () => {
      expect(validators.isValidPassword('')).toBe(false)
      expect(validators.isValidPassword('12345')).toBe(false)
      expect(validators.isValidPassword('a'.repeat(21))).toBe(false)
      expect(validators.isValidPassword(null)).toBe(false)
    })
  })

  describe('isValidEmail', () => {
    it('有效邮箱应返回true', () => {
      expect(validators.isValidEmail('test@example.com')).toBe(true)
      expect(validators.isValidEmail('user.name@domain.org')).toBe(true)
      expect(validators.isValidEmail('')).toBe(true) // 可选
    })

    it('无效邮箱应返回false', () => {
      expect(validators.isValidEmail('invalid')).toBe(false)
      expect(validators.isValidEmail('test@')).toBe(false)
      expect(validators.isValidEmail('@example.com')).toBe(false)
    })
  })

  describe('isValidPhone', () => {
    it('有效手机号应返回true', () => {
      expect(validators.isValidPhone('13800138000')).toBe(true)
      expect(validators.isValidPhone('15912345678')).toBe(true)
      expect(validators.isValidPhone('')).toBe(true) // 可选
    })

    it('无效手机号应返回false', () => {
      expect(validators.isValidPhone('12345678901')).toBe(false)
      expect(validators.isValidPhone('1380013800')).toBe(false)
      expect(validators.isValidPhone('138001380001')).toBe(false)
    })
  })

  describe('isValidIsbn', () => {
    it('有效ISBN应返回true', () => {
      expect(validators.isValidIsbn('978-7-111-11111-1')).toBe(true)
      expect(validators.isValidIsbn('978-0-123-45678-9')).toBe(true)
    })

    it('无效ISBN应返回false', () => {
      expect(validators.isValidIsbn('')).toBe(false)
      expect(validators.isValidIsbn('123456789')).toBe(false)
      expect(validators.isValidIsbn(null)).toBe(false)
    })
  })
})

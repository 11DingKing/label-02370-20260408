import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'

// 简化的Mock组件用于测试
const LoginForm = {
  template: `
    <div class="login-form">
      <input v-model="username" data-testid="username" />
      <input v-model="password" type="password" data-testid="password" />
      <button @click="submit" data-testid="submit">登录</button>
      <span v-if="errorMsg" data-testid="error">{{ errorMsg }}</span>
    </div>
  `,
  data() {
    return {
      username: '',
      password: '',
      errorMsg: ''
    }
  },
  methods: {
    submit() {
      if (!this.username || !this.password) {
        this.errorMsg = '请填写用户名和密码'
        return
      }
      this.errorMsg = ''
      this.$emit('submit', { username: this.username, password: this.password })
    }
  }
}

describe('Login Form Component', () => {
  let wrapper

  beforeEach(() => {
    setActivePinia(createPinia())
    wrapper = mount(LoginForm)
  })

  it('应渲染登录表单元素', () => {
    expect(wrapper.find('[data-testid="username"]').exists()).toBe(true)
    expect(wrapper.find('[data-testid="password"]').exists()).toBe(true)
    expect(wrapper.find('[data-testid="submit"]').exists()).toBe(true)
  })

  it('应能输入用户名和密码', async () => {
    await wrapper.find('[data-testid="username"]').setValue('testuser')
    await wrapper.find('[data-testid="password"]').setValue('password123')

    expect(wrapper.vm.username).toBe('testuser')
    expect(wrapper.vm.password).toBe('password123')
  })

  it('空表单提交应显示错误信息', async () => {
    await wrapper.find('[data-testid="submit"]').trigger('click')
    
    expect(wrapper.vm.errorMsg).toBe('请填写用户名和密码')
    expect(wrapper.find('[data-testid="error"]').exists()).toBe(true)
  })

  it('只填用户名提交应显示错误', async () => {
    await wrapper.find('[data-testid="username"]').setValue('testuser')
    await wrapper.find('[data-testid="submit"]').trigger('click')
    
    expect(wrapper.vm.errorMsg).toBe('请填写用户名和密码')
  })

  it('填写完整表单应触发submit事件', async () => {
    await wrapper.find('[data-testid="username"]').setValue('testuser')
    await wrapper.find('[data-testid="password"]').setValue('password123')
    await wrapper.find('[data-testid="submit"]').trigger('click')

    expect(wrapper.emitted('submit')).toBeTruthy()
    expect(wrapper.emitted('submit')[0][0]).toEqual({
      username: 'testuser',
      password: 'password123'
    })
  })

  it('成功提交后应清除错误信息', async () => {
    // 先触发错误
    await wrapper.find('[data-testid="submit"]').trigger('click')
    expect(wrapper.vm.errorMsg).toBe('请填写用户名和密码')
    
    // 填写表单后提交
    await wrapper.find('[data-testid="username"]').setValue('testuser')
    await wrapper.find('[data-testid="password"]').setValue('password123')
    await wrapper.find('[data-testid="submit"]').trigger('click')
    
    expect(wrapper.vm.errorMsg).toBe('')
  })
})

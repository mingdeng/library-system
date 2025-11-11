import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))
  
  // 登录
  const userLogin = async (loginData) => {
    const res = await login(loginData)
    token.value = res.data.token
    userInfo.value = {
      userId: res.data.userId,
      username: res.data.username,
      realName: res.data.realName,
      role: res.data.role
    }
    localStorage.setItem('token', token.value)
    localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
    return res
  }
  
  // 退出登录
  const logout = () => {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }
  
  return {
    token,
    userInfo,
    userLogin,
    logout
  }
})


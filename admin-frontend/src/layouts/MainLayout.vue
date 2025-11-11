<template>
  <el-container class="layout-container">
    <el-header class="header">
      <div class="header-left">
        <h2>图书管理系统 - 管理端</h2>
      </div>
      <div class="header-right">
        <span>管理员：{{ userInfo?.realName || userInfo?.username }}</span>
        <el-button link @click="handleLogout">退出登录</el-button>
      </div>
    </el-header>
    <el-container>
      <el-aside width="200px" class="aside">
        <el-menu
          :default-active="activeMenu"
          router
          class="menu"
        >
          <el-menu-item index="/admin/dashboard">
            <el-icon><HomeFilled /></el-icon>
            <span>首页</span>
          </el-menu-item>
          <el-menu-item index="/admin/books">
            <el-icon><Reading /></el-icon>
            <span>图书管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/users">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { HomeFilled, Reading, User } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)
const userInfo = computed(() => userStore.userInfo)

// 退出登录
const handleLogout = () => {
  userStore.logout()
  router.push('/admin/login')
  ElMessage.success('已退出登录')
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.header {
  background: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.header-left h2 {
  margin: 0;
  color: #303133;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.aside {
  background: white;
  border-right: 1px solid #ebeef5;
}

.menu {
  border-right: none;
}

.main {
  background: #f5f7fa;
  padding: 20px;
}
</style>


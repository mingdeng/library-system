<template>
  <div class="detail-container">
    <!-- 顶部导航栏 -->
    <el-header class="header">
      <div class="header-left">
        <el-button @click="goBack">返回首页</el-button>
        <h2>图书管理系统</h2>
      </div>
      <div class="header-right">
        <el-button text @click="goToProfile">个人中心</el-button>
        <el-button text @click="handleLogout">退出登录</el-button>
      </div>
    </el-header>

    <div class="content" v-loading="loading">
      <div v-if="book" class="book-detail">
        <div class="book-main">
          <div class="book-cover-large">
            <img v-if="book.coverUrl" :src="book.coverUrl" :alt="book.title" />
            <div v-else class="no-cover">暂无封面</div>
          </div>
          <div class="book-info">
            <h1 class="book-title">{{ book.title }}</h1>
            <div class="book-meta">
              <p><strong>作者：</strong>{{ book.author || '未知' }}</p>
              <p><strong>出版社：</strong>{{ book.publisher || '未知' }}</p>
              <p><strong>ISBN：</strong>{{ book.isbn || '未知' }}</p>
              <p><strong>分类：</strong>{{ book.category || '未知' }}</p>
              <p><strong>出版日期：</strong>{{ book.publishDate || '未知' }}</p>
            </div>
            <div class="book-status">
              <el-tag :type="book.availableQuantity > 0 ? 'success' : 'danger'">
                {{ book.availableQuantity > 0 ? '可借' : '已借完' }}
              </el-tag>
            </div>
            <div class="book-actions">
              <el-button
                type="primary"
                size="large"
                :disabled="book.availableQuantity <= 0"
                @click="handleBorrow"
                :loading="borrowing"
              >
                借阅
              </el-button>
              <el-button size="large" @click="handleFavorite">收藏</el-button>
            </div>
          </div>
        </div>
        <div class="book-summary">
          <h3>图书简介</h3>
          <p>{{ book.summary || '暂无简介' }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { getBookDetail } from '@/api/book'
import { borrowBook } from '@/api/borrow'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const borrowing = ref(false)
const book = ref(null)

// 加载图书详情
const loadBookDetail = async () => {
  loading.value = true
  try {
    const res = await getBookDetail(route.params.id)
    book.value = res.data
  } catch (error) {
    ElMessage.error('加载图书详情失败')
  } finally {
    loading.value = false
  }
}

// 借阅
const handleBorrow = async () => {
  if (!userStore.userInfo) {
    ElMessage.warning('请先登录')
    router.push('/reader/login')
    return
  }

  try {
    await ElMessageBox.confirm('确定要借阅这本书吗？', '确认借阅', {
      type: 'warning'
    })
    
    borrowing.value = true
    await borrowBook({
      bookId: book.value.id,
      userId: userStore.userInfo.userId
    })
    ElMessage.success('借阅成功')
    // 重新加载详情
    loadBookDetail()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '借阅失败')
    }
  } finally {
    borrowing.value = false
  }
}

// 收藏
const handleFavorite = () => {
  ElMessage.info('收藏功能开发中')
}

// 返回首页
const goBack = () => {
  router.push('/reader/home')
}

// 个人中心
const goToProfile = () => {
  router.push('/reader/profile')
}

// 退出登录
const handleLogout = () => {
  userStore.logout()
  router.push('/reader/login')
  ElMessage.success('已退出登录')
}

onMounted(() => {
  loadBookDetail()
})
</script>

<style scoped>
.detail-container {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.header {
  background: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.header-left h2 {
  margin: 0;
  color: #303133;
}

.header-right {
  display: flex;
  gap: 10px;
}

.content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.book-detail {
  background: white;
  border-radius: 8px;
  padding: 30px;
}

.book-main {
  display: flex;
  gap: 30px;
  margin-bottom: 30px;
}

.book-cover-large {
  width: 300px;
  height: 400px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  border-radius: 4px;
  overflow: hidden;
}

.book-cover-large img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.no-cover {
  color: #909399;
  font-size: 16px;
}

.book-info {
  flex: 1;
}

.book-title {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 20px;
  color: #303133;
}

.book-meta {
  margin-bottom: 20px;
}

.book-meta p {
  margin-bottom: 10px;
  font-size: 16px;
  color: #606266;
}

.book-status {
  margin-bottom: 20px;
}

.book-actions {
  display: flex;
  gap: 15px;
}

.book-summary {
  margin-top: 30px;
  padding-top: 30px;
  border-top: 1px solid #ebeef5;
}

.book-summary h3 {
  margin-bottom: 15px;
  color: #303133;
}

.book-summary p {
  line-height: 1.8;
  color: #606266;
}
</style>


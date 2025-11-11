<template>
  <div class="home-container">
    <!-- 顶部导航栏 -->
    <el-header class="header">
      <div class="header-left">
        <h2>图书管理系统</h2>
      </div>
      <div class="header-right">
        <el-button text @click="goToProfile">个人中心</el-button>
        <el-button text @click="handleLogout">退出登录</el-button>
      </div>
    </el-header>

    <div class="content">
      <!-- 搜索栏 -->
      <div class="search-section">
        <el-input
          v-model="searchKeyword"
          placeholder="🔍 搜索图书..."
          size="large"
          clearable
          @keyup.enter="handleSearch"
          class="search-input"
        >
          <template #append>
            <el-button @click="handleSearch">搜索</el-button>
          </template>
        </el-input>
      </div>

      <!-- 分类筛选 -->
      <div class="category-section">
        <el-radio-group v-model="selectedCategory" @change="handleCategoryChange">
          <el-radio-button value="全部">全部</el-radio-button>
          <el-radio-button value="文学">文学</el-radio-button>
          <el-radio-button value="科技">科技</el-radio-button>
          <el-radio-button value="历史">历史</el-radio-button>
          <el-radio-button value="艺术">艺术</el-radio-button>
        </el-radio-group>
      </div>

      <!-- 图书列表 -->
      <div class="book-list" v-loading="loading">
        <el-empty v-if="!loading && bookList.length === 0" description="暂无图书" />
        <div v-else class="book-grid">
          <div v-for="book in bookList" :key="book.id" class="book-card" @click="goToDetail(book.id)">
            <div class="book-cover">
              <img v-if="book.coverUrl" :src="book.coverUrl" :alt="book.title" />
              <div v-else class="no-cover">暂无封面</div>
            </div>
            <div class="book-info">
              <h3 class="book-title">{{ book.title }}</h3>
              <p class="book-author">{{ book.author }}</p>
              <p class="book-status">
                <el-tag :type="book.availableQuantity > 0 ? 'success' : 'danger'">
                  {{ book.availableQuantity > 0 ? '可借' : '已借完' }}
                </el-tag>
              </p>
              <el-button type="primary" size="small" @click.stop="goToDetail(book.id)">
                查看详情
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div class="pagination" v-if="total > 0">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[12, 24, 48]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { getBookList } from '@/api/book'

const router = useRouter()
const userStore = useUserStore()

const searchKeyword = ref('')
const selectedCategory = ref('全部')
const loading = ref(false)
const bookList = ref([])
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)

// 加载图书列表
const loadBooks = async () => {
  loading.value = true
  try {
    const res = await getBookList({
      current: currentPage.value,
      size: pageSize.value,
      keyword: searchKeyword.value || undefined,
      category: selectedCategory.value === '全部' ? undefined : selectedCategory.value
    })
    bookList.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    ElMessage.error('加载图书列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  loadBooks()
}

// 分类切换
const handleCategoryChange = () => {
  currentPage.value = 1
  loadBooks()
}

// 分页大小改变
const handleSizeChange = () => {
  loadBooks()
}

// 页码改变
const handlePageChange = () => {
  loadBooks()
}

// 查看详情
const goToDetail = (id) => {
  router.push(`/reader/detail/${id}`)
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
  loadBooks()
})
</script>

<style scoped>
.home-container {
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

.search-section {
  margin-bottom: 20px;
}

.search-input {
  max-width: 600px;
}

.category-section {
  margin-bottom: 20px;
}

.book-list {
  min-height: 400px;
}

.book-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
}

.book-card {
  background: white;
  border-radius: 8px;
  padding: 15px;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.book-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.book-cover {
  width: 100%;
  height: 200px;
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  border-radius: 4px;
  overflow: hidden;
}

.book-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.no-cover {
  color: #909399;
  font-size: 14px;
}

.book-info {
  text-align: center;
}

.book-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 8px;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.book-author {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.book-status {
  margin-bottom: 10px;
}

.pagination {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}
</style>


<template>
  <div class="profile-container">
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

    <div class="content">
      <el-tabs v-model="activeTab">
        <!-- 个人信息 -->
        <el-tab-pane label="个人信息" name="info">
          <div class="info-section">
            <el-card>
              <template #header>
                <span>个人信息</span>
              </template>
              <el-descriptions :column="2" border>
                <el-descriptions-item label="用户名">
                  {{ userInfo?.username || '-' }}
                </el-descriptions-item>
                <el-descriptions-item label="姓名">
                  {{ userInfo?.realName || '-' }}
                </el-descriptions-item>
                <el-descriptions-item label="邮箱">
                  {{ userInfo?.email || '-' }}
                </el-descriptions-item>
                <el-descriptions-item label="电话">
                  {{ userInfo?.phone || '-' }}
                </el-descriptions-item>
              </el-descriptions>
            </el-card>
          </div>
        </el-tab-pane>

        <!-- 我的借阅 -->
        <el-tab-pane label="我的借阅" name="borrows">
          <div class="borrow-section">
            <el-table :data="borrowList" v-loading="loading" stripe>
              <el-table-column label="书名" width="220">
                <template #default="{ row }">
                  <el-link type="primary" underline @click="goToBookDetail(getBookId(row))">
                    {{ getBookTitle(row) }}
                  </el-link>
                </template>
              </el-table-column>
              <el-table-column prop="borrowDate" label="借阅日期" width="180">
                <template #default="{ row }">
                  {{ formatDate(row.borrowDate) }}
                </template>
              </el-table-column>
              <el-table-column prop="dueDate" label="归还日期" width="180">
                <template #default="{ row }">
                  {{ formatDate(row.dueDate) }}
                </template>
              </el-table-column>
              <el-table-column prop="status" label="状态" width="120">
                <template #default="{ row }">
                  <el-tag :type="getStatusType(row.status)">
                    {{ getStatusText(row.status) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="150">
                <template #default="{ row }">
                  <el-button
                    v-if="row.status === 'BORROWED'"
                    type="primary"
                    size="small"
                    @click="handleRenew(row)"
                    :loading="renewing"
                  >
                    续借
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { getBorrowList, renewBook } from '@/api/borrow'

const router = useRouter()
const userStore = useUserStore()

const activeTab = ref('info')
const loading = ref(false)
const renewing = ref(false)
const borrowList = ref([])

const userInfo = computed(() => userStore.userInfo)

// 加载借阅列表
const loadBorrowList = async () => {
  if (!userInfo.value?.userId) return
  
  loading.value = true
  try {
    const res = await getBorrowList(userInfo.value.userId)
    borrowList.value = res.data || []
  } catch (error) {
    ElMessage.error('加载借阅列表失败')
  } finally {
    loading.value = false
  }
}

// 续借
const handleRenew = async (record) => {
  try {
    await ElMessageBox.confirm('确定要续借这本书吗？', '确认续借', {
      type: 'warning'
    })
    
    renewing.value = true
    await renewBook({
      recordId: record.id,
      userId: userInfo.value.userId
    })
    ElMessage.success('续借成功')
    loadBorrowList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '续借失败')
    }
  } finally {
    renewing.value = false
  }
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN')
}

// 获取状态类型
const getStatusType = (status) => {
  const map = {
    'BORROWED': 'success',
    'RETURNED': 'info',
    'OVERDUE': 'danger'
  }
  return map[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const map = {
    'BORROWED': '借阅中',
    'RETURNED': '已归还',
    'OVERDUE': '逾期'
  }
  return map[status] || status
}

// 返回首页
const goBack = () => {
  router.push('/reader/home')
}

// 尝试从不同字段中获取图书标题（兼容后端返回结构差异）
const getBookTitle = (row) => {
  // debugger
  if (!row) return '-'
  return (
    row.book?.title || row.bookName || row.bookTitle || row.book?.name || `ID:${row.bookId || row.book?.id || '-'}`
  )
}

// 获取图书ID用于跳转
const getBookId = (row) => {
  if (!row) return null
  return row.book?.id || row.bookId || null
}

// 跳转到图书详情页
const goToBookDetail = (bookId) => {
  if (!bookId) {
    ElMessage.error('图书ID不存在，无法跳转')
    return
  }
  router.push(`/reader/detail/${bookId}`)
}

// 个人中心
const goToProfile = () => {
  // 已在个人中心
}

// 退出登录
const handleLogout = () => {
  userStore.logout()
  router.push('/reader/login')
  ElMessage.success('已退出登录')
}

onMounted(() => {
  loadBorrowList()
})
</script>

<style scoped>
.profile-container {
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

.info-section,
.borrow-section {
  background: white;
  border-radius: 8px;
  padding: 20px;
}
</style>


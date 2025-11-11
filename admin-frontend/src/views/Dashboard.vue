<template>
  <div class="dashboard-container">
    <h2 class="page-title">数据统计</h2>
    
    <!-- 数据卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-value">{{ statistics.totalBooks || 0 }}</div>
            <div class="stat-label">总图书数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-value">{{ statistics.totalUsers || 0 }}</div>
            <div class="stat-label">总用户数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-value">{{ statistics.borrowingCount || 0 }}</div>
            <div class="stat-label">借阅中</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-value">{{ statistics.todayBorrows || 0 }}</div>
            <div class="stat-label">今日借阅</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getStatistics } from '@/api/dashboard'

const statistics = ref({})

// 加载统计数据
const loadStatistics = async () => {
  try {
    const res = await getStatistics()
    statistics.value = res.data || {}
  } catch (error) {
    ElMessage.error('加载统计数据失败')
  }
}

onMounted(() => {
  loadStatistics()
})
</script>

<style scoped>
.dashboard-container {
  background: white;
  border-radius: 8px;
  padding: 20px;
}

.page-title {
  margin-bottom: 20px;
  color: #303133;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  text-align: center;
}

.stat-content {
  padding: 20px;
}

.stat-value {
  font-size: 36px;
  font-weight: bold;
  color: #409EFF;
  margin-bottom: 10px;
}

.stat-label {
  font-size: 16px;
  color: #909399;
}
</style>


<template>
  <div class="home-container">
    <h1>欢迎使用学生评奖评优系统</h1>
    <div class="dashboard">
      <el-card class="dashboard-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <span>系统概览</span>
          </div>
        </template>
        <div class="dashboard-stats">
          <div class="stat-item">
            <el-icon><User /></el-icon>
            <div class="stat-content">
              <div class="stat-value">{{ studentCount }}</div>
              <div class="stat-label">学生总数</div>
            </div>
          </div>
          <div class="stat-item">
            <el-icon><Promotion /></el-icon>
            <div class="stat-content">
              <div class="stat-value">{{ awardCount }}</div>
              <div class="stat-label">奖项总数</div>
            </div>
          </div>
          <div class="stat-item">
            <el-icon><Document /></el-icon>
            <div class="stat-content">
              <div class="stat-value">{{ applicationCount }}</div>
              <div class="stat-label">申请总数</div>
            </div>
          </div>
          <div class="stat-item">
            <el-icon><Check /></el-icon>
            <div class="stat-content">
              <div class="stat-value">{{ approvedCount }}</div>
              <div class="stat-label">已通过申请</div>
            </div>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { User, Promotion, Document, Check } from '@element-plus/icons-vue'
import axios from 'axios'

// 统计数据
const studentCount = ref(0)
const awardCount = ref(0)
const applicationCount = ref(0)
const approvedCount = ref(0)

// 获取统计数据
const getStatistics = async () => {
  try {
    // 这里可以根据实际API调整
    const studentRes = await axios.get('/api/students/count')
    const awardRes = await axios.get('/api/awards/count')
    const applicationRes = await axios.get('/api/student-award-applications/count')
    const approvedRes = await axios.get('/api/student-award-applications/count?status=approved')
    
    studentCount.value = studentRes.data
    awardCount.value = awardRes.data
    applicationCount.value = applicationRes.data
    approvedCount.value = approvedRes.data
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

onMounted(() => {
  getStatistics()
})
</script>

<style scoped>
.home-container {
  padding: 20px;
}

h1 {
  font-size: 28px;
  margin-bottom: 20px;
  color: #333;
}

.dashboard {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.dashboard-card {
  flex: 1;
  min-width: 250px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dashboard-stats {
  display: flex;
  justify-content: space-around;
  flex-wrap: wrap;
  gap: 20px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px;
  border-radius: 8px;
  background-color: #f5f7fa;
  transition: all 0.3s ease;
}

.stat-item:hover {
  background-color: #e6f7ff;
  transform: translateY(-2px);
}

.stat-item .el-icon {
  font-size: 32px;
  color: #409eff;
}

.stat-content {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.stat-label {
  font-size: 14px;
  color: #666;
  margin-top: 5px;
}
</style>
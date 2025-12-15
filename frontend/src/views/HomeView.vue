<template>
  <div class="home-container">
    <h1>欢迎使用学生评奖评优系统</h1>
    <!-- 轮播图组件 -->
    <el-carousel height="80vh" arrow="always" indicator-position="outside" :interval="3000">
      <el-carousel-item v-for="carousel in carousels" :key="carousel.id">
        <div class="carousel-item" :style="{ backgroundImage: `url(${getImageUrl(carousel.imageUrl)})` }">
          <div class="carousel-content">
            <h2 v-if="carousel.title" class="carousel-title">{{ carousel.title }}</h2>
            <p v-if="carousel.description" class="carousel-description">{{ carousel.description }}</p>
          </div>
        </div>
      </el-carousel-item>
    </el-carousel>
    
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

    <!-- 最近评选活动 -->
    <div class="recent-events" style="margin-top: 20px;">
      <el-card shadow="hover">
        <template #header>
          <div class="card-header">
            <span>最近评选活动</span>
          </div>
        </template>
        <el-table :data="recentEvents" stripe style="width: 100%">
          <el-table-column prop="awardName" label="活动名称" />
          <el-table-column prop="currentStatus" label="状态" width="120">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.currentStatus)" size="small">
                {{ getStatusText(scope.row.currentStatus) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="endTime" label="截止日期" width="150" />
        </el-table>
      </el-card>
    </div>

    <!-- 通知和提醒 -->
    <div class="notifications" style="margin-top: 20px;">
      <el-card shadow="hover">
        <template #header>
          <div class="card-header">
            <span>通知和提醒</span>
          </div>
        </template>
        <el-list>
          <el-list-item v-for="notice in recentNotices" :key="notice.id">
            <template #prefix>
              <el-icon>
                <InfoFilled v-if="notice.type === 'info'" />
                <Warning v-else-if="notice.type === 'warning'" />
                <Notification v-else />
              </el-icon>
            </template>
            <div class="notice-content">
              <div class="notice-title">{{ notice.title }}</div>
              <div class="notice-description">{{ notice.description }}</div>
              <div class="notice-time">{{ formatTime(notice.publishTime) }}</div>
            </div>
          </el-list-item>
        </el-list>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { User, Promotion, Document, Check, Bell, Notification, Warning, InfoFilled } from '@element-plus/icons-vue'
import { ElCarousel, ElCarouselItem } from 'element-plus'
import axios from 'axios'

// 轮播图数据
const carousels = ref([])

// 统计数据
const studentCount = ref(0)
const awardCount = ref(0)
const applicationCount = ref(0)
const approvedCount = ref(0)

// 最近评选活动
const recentEvents = ref([])

// 最近通知
const recentNotices = ref([])

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

// 获取最近评选活动
const getRecentEvents = async () => {
  try {
    const res = await axios.get('/api/awards/recent')
    console.log('API返回的最近评选活动数据:', res.data)
    recentEvents.value = res.data
  } catch (error) {
    console.error('获取最近评选活动失败:', error)
  }
}

// 获取最近通知
const getRecentNotices = async () => {
  try {
    const res = await axios.get('/api/notices/recent')
    recentNotices.value = res.data
  } catch (error) {
    console.error('获取最近通知失败:', error)
  }
}

// 获取完整的图片URL
const getImageUrl = (imageUrl) => {
  // 如果是本地图片路径（以/uploads/开头），则拼接完整URL
  if (imageUrl && imageUrl.startsWith('/uploads/')) {
    return `http://localhost:8080${imageUrl}`
  }
  // 否则直接返回（可能是外部URL）
  return imageUrl
}

// 获取活动状态类型
const getStatusType = (status) => {
  switch (status) {
    case '进行中':
      return 'warning'
    case '已完成':
      return 'success'
    case '待开始':
      return 'info'
    case '已关闭':
      return 'danger'
    case '已逾期':
      return 'danger'
    default:
      return 'default'
  }
}

// 获取活动状态文本
const getStatusText = (status) => {
  return status
}

// 不再需要getNoticeIcon函数，已在模板中直接使用v-if/v-else-if/v-else来显示不同图标

// 格式化时间
const formatTime = (timeString) => {
  const date = new Date(timeString)
  const now = new Date()
  const diff = now - date
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  
  if (days === 0) {
    const hours = Math.floor(diff / (1000 * 60 * 60))
    if (hours === 0) {
      const minutes = Math.floor(diff / (1000 * 60))
      return `${minutes}分钟前`
    } else {
      return `${hours}小时前`
    }
  } else if (days < 7) {
    return `${days}天前`
  } else {
    return date.toLocaleDateString()
  }
}

// 获取轮播图数据
const getCarousels = async () => {
  try {
    const res = await axios.get('/api/carousels/active')
    carousels.value = res.data
  } catch (error) {
    console.error('获取轮播图数据失败:', error)
  }
}

onMounted(() => {
  getCarousels()
  getStatistics()
  getRecentEvents()
  getRecentNotices()
})
</script>

<style scoped>
.home-container {
  padding: 20px;
}

/* 轮播图样式 */
.el-carousel {
  margin-bottom: 20px;
}

.carousel-item {
  height: 100%;
  background-size: cover;
  background-position: center;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.carousel-content {
  background-color: rgba(0, 0, 0, 0.5);
  color: white;
  padding: 20px 40px;
  border-radius: 8px;
  text-align: center;
  max-width: 80%;
}

.carousel-title {
  font-size: 28px;
  margin-bottom: 10px;
}

.carousel-description {
  font-size: 16px;
}


/* 页面标题已使用全局样式定义 */

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

/* 最近评选活动样式 */
.recent-events {
  margin-top: 20px;
}

/* 通知和提醒样式 */
.notifications {
  margin-top: 20px;
}

.notice-content {
  width: 100%;
}

.notice-title {
  font-weight: bold;
  margin-bottom: 5px;
  color: #333;
}

.notice-description {
  font-size: 14px;
  color: #666;
  margin-bottom: 5px;
}

.notice-time {
  font-size: 12px;
  color: #999;
  text-align: right;
}
</style>
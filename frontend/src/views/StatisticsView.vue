<template>
  <div class="statistics-container">
    <h2>数据统计</h2>
    
    <!-- 系统概览卡片 -->
    <div class="overview-cards">
      <el-card shadow="hover" class="overview-card" style="background: linear-gradient(135deg, rgba(52, 152, 219, 0.2) 0%, rgba(41, 128, 185, 0.2) 100%);">
        <div class="overview-content">
          <el-icon class="overview-icon"><User /></el-icon>
          <div class="overview-value">{{ overviewData.studentCount }}</div>
          <div class="overview-label">学生总数</div>
        </div>
      </el-card>
      <el-card shadow="hover" class="overview-card" style="background: linear-gradient(135deg, rgba(142, 68, 173, 0.2) 0%, rgba(155, 89, 182, 0.2) 100%);">
        <div class="overview-content">
          <el-icon class="overview-icon"><Trophy /></el-icon>
          <div class="overview-value">{{ overviewData.awardCount }}</div>
          <div class="overview-label">奖项总数</div>
        </div>
      </el-card>
      <el-card shadow="hover" class="overview-card" style="background: linear-gradient(135deg, rgba(46, 204, 113, 0.2) 0%, rgba(39, 174, 96, 0.2) 100%);">
        <div class="overview-content">
          <el-icon class="overview-icon"><Document /></el-icon>
          <div class="overview-value">{{ overviewData.applicationCount }}</div>
          <div class="overview-label">申请总数</div>
        </div>
      </el-card>
      <el-card shadow="hover" class="overview-card" style="background: linear-gradient(135deg, rgba(243, 156, 18, 0.2) 0%, rgba(230, 126, 34, 0.2) 100%);">
        <div class="overview-content">
          <el-icon class="overview-icon"><Check /></el-icon>
          <div class="overview-value">{{ overviewData.approvedCount }}</div>
          <div class="overview-label">已通过申请</div>
        </div>
      </el-card>
    </div>
    
    <!-- 图表区域 -->
    <div class="charts-container">
      <!-- 奖项级别分布 -->
      <el-card shadow="hover" class="chart-card">
        <template #header>
          <div class="card-header">
            <span>奖项级别分布</span>
          </div>
        </template>
        <div class="chart-content">
          <AwardLevelPieChart />
        </div>
      </el-card>
      
      <!-- 申请状态分布 -->
      <el-card shadow="hover" class="chart-card">
        <template #header>
          <div class="card-header">
            <span>申请状态分布</span>
          </div>
        </template>
        <div class="chart-content">
          <ApplicationStatusRingChart />
        </div>
      </el-card>
      
      <!-- 各专业获奖情况 -->
      <el-card shadow="hover" class="chart-card">
        <template #header>
          <div class="card-header">
            <span>各专业获奖情况</span>
          </div>
        </template>
        <div class="chart-content">
          <AwardsByMajorBarChart />
        </div>
      </el-card>
      
      <!-- 审批流程时间分析 -->
      <el-card shadow="hover" class="chart-card">
        <template #header>
          <div class="card-header">
            <span>审批流程时间分析</span>
          </div>
        </template>
        <div class="chart-content">
          <ApprovalTimeWaterfallChart />
        </div>
      </el-card>
      
      <!-- 奖项申请趋势 -->
      <el-card shadow="hover" class="chart-card full-width">
        <template #header>
          <div class="card-header">
            <span>奖项申请趋势</span>
          </div>
        </template>
        <div class="chart-content">
          <ApplicationTrendLineChart />
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import axios from 'axios'
import AwardLevelPieChart from '../components/AwardLevelPieChart.vue'
import ApplicationStatusRingChart from '../components/ApplicationStatusRingChart.vue'
import AwardsByMajorBarChart from '../components/AwardsByMajorBarChart.vue'
import ApprovalTimeWaterfallChart from '../components/ApprovalTimeWaterfallChart.vue'
import ApplicationTrendLineChart from '../components/ApplicationTrendLineChart.vue'
// 引入Element Plus图标
import { User, Trophy, Document, Check } from '@element-plus/icons-vue'

// 概览数据
const overviewData = reactive({
  studentCount: 0,
  awardCount: 0,
  applicationCount: 0,
  approvedCount: 0
})

// 获取概览数据
const getOverviewData = async () => {
  try {
    console.log('开始获取概览数据...')
    const response = await axios.get('/api/statistics/overview')
    console.log('概览数据API请求成功，响应:', response)
    const data = response.data.data
    overviewData.studentCount = data.studentCount || 0
    overviewData.awardCount = data.awardCount || 0
    overviewData.applicationCount = data.applicationCount || 0
    overviewData.approvedCount = data.approvedCount || 0
    console.log('概览数据获取完成:', overviewData)
  } catch (error) {
    console.error('获取概览数据失败:', error)
    console.error('错误详情:', error.response || error.message || error)
    // 可以添加错误提示
  }
}

// 页面加载时初始化
onMounted(() => {
  console.log('数据统计页面加载，开始获取概览数据...')
  // 获取概览数据
  getOverviewData()
})
</script>

<style scoped>
:root {
  --primary-color: #3498db;
  --secondary-color: #2980b9;
  --accent-color: #8e44ad;
  --light-color: #f8f9fa;
  --dark-color: #2c3e50;
  --white-color: #ffffff;
  --success-color: #27ae60;
  --warning-color: #f39c12;
  --danger-color: #e74c3c;
  --shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  --shadow-hover: 0 15px 40px rgba(0, 0, 0, 0.15);
  --transition: all 0.3s ease;
}

/* 页面容器 */
.statistics-container {
  position: relative;
  padding: 30px;
  min-height: 100vh;
  overflow: hidden;
  background-color: var(--light-color);
}

/* 渐变背景 */
.statistics-container::before {
  content: "";
  position: absolute;
  height: 2000px;
  width: 2000px;
  top: -10%;
  right: 48%;
  transform: translateY(-50%);
  background-image: linear-gradient(-45deg, var(--primary-color) 0%, var(--accent-color) 100%);
  border-radius: 50%;
  z-index: 1;
  box-shadow: 0 0 100px rgba(0, 0, 0, 0.1);
}

/* 装饰性气泡 */
.statistics-container::after {
  content: "";
  position: absolute;
  height: 100%;
  width: 100%;
  top: 0;
  left: 0;
  background-image: 
    radial-gradient(circle at 20% 50%, rgba(52, 152, 219, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, rgba(142, 68, 173, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 40% 80%, rgba(41, 128, 185, 0.1) 0%, transparent 50%);
  z-index: 2;
  pointer-events: none;
}

/* 页面内容 */
.statistics-container > * {
  position: relative;
  z-index: 3;
}

/* 页面标题 */
h2 {
  font-size: 2.5rem;
  color: var(--dark-color);
  margin-bottom: 30px;
  font-weight: 700;
  text-align: center;
  letter-spacing: -0.5px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

/* 概览卡片容器 */
.overview-cards {
  display: flex;
  gap: 25px;
  margin-bottom: 30px;
  flex-wrap: wrap;
  justify-content: center;
}

/* 概览卡片 */
.overview-card {
  flex: 1;
  min-width: 220px;
  border-radius: 20px !important;
  overflow: hidden;
  transition: var(--transition);
  box-shadow: var(--shadow) !important;
  border: none !important;
}

.overview-card:hover {
  transform: translateY(-5px);
  box-shadow: var(--shadow-hover) !important;
}

/* 概览卡片内容 */
.overview-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 30px 0;
  position: relative;
}

/* 概览卡片图标 */
.overview-icon {
  font-size: 2.5rem;
  color: rgba(44, 62, 80, 0.3);
  margin-bottom: 15px;
  opacity: 0.5;
}

/* 概览卡片数值 */
.overview-value {
  font-size: 36px;
  font-weight: bold;
  color: var(--dark-color);
  margin-bottom: 8px;
}

/* 概览卡片标签 */
.overview-label {
  font-size: 16px;
  color: var(--dark-color);
  font-weight: 500;
}

/* 图表容器 */
.charts-container {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 25px;
  max-width: 1400px;
  margin: 0 auto;
}

/* 图表卡片 */
.chart-card {
  height: 450px;
  border-radius: 20px !important;
  overflow: hidden;
  transition: var(--transition);
  box-shadow: var(--shadow) !important;
  border: none !important;
  background: rgba(255, 255, 255, 0.95) !important;
  backdrop-filter: blur(10px);
}

.chart-card:hover {
  transform: translateY(-5px);
  box-shadow: var(--shadow-hover) !important;
}

/* 图表卡片头部 */
.card-header {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: 600;
  color: var(--dark-color);
}

/* 图表内容区域 */
.chart-content {
  height: calc(100% - 60px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 20px;
}

/* 图表 */
.chart {
  width: 100%;
  height: 100%;
}

/* 全宽图表 */
.full-width {
  grid-column: span 2;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .charts-container {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .full-width {
    grid-column: span 1;
  }
}

@media (max-width: 768px) {
  .statistics-container {
    padding: 20px;
  }
  
  h2 {
    font-size: 2rem;
  }
  
  .overview-cards {
    gap: 15px;
    margin-bottom: 20px;
  }
  
  .overview-card {
    min-width: 180px;
  }
  
  .overview-value {
    font-size: 32px;
  }
  
  .chart-card {
    height: 400px;
  }
}
</style>

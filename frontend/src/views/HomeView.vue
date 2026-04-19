<template>
  <div class="home-container">
    <h1>欢迎使用学生评奖评优系统</h1>
    <!-- 轮播图组件 -->
    <el-carousel height="80vh" arrow="always" indicator-position="outside" :interval="carouselInterval">
      <el-carousel-item v-for="carousel in carousels" :key="carousel.id">
        <div class="carousel-item" :style="{ backgroundImage: `url(${getImageUrl(carousel.imageUrl)})` }">
          <div class="carousel-content">
            <h2 v-if="carousel.title" class="carousel-title">{{ carousel.title }}</h2>
            <p v-if="carousel.description" class="carousel-description">{{ carousel.description }}</p>
          </div>
        </div>
      </el-carousel-item>
    </el-carousel>
    
    

    <!-- 通知和提醒 -->
    <div class="notifications" style="margin-top: 20px;">
      <el-card shadow="hover">
        <template #header>
          <div class="card-header">
            <span>通知和提醒</span>
          </div>
        </template>
        <el-list>
          <el-list-item 
            v-for="notice in recentNotices" 
            :key="notice.id"
            class="notice-item"
            @click="handleNoticeClick(notice)"
          >
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
    
    <!-- 通知详情弹窗 -->
    <el-dialog
      v-model="noticeDialogVisible"
      :title="'通知详情 - ' + currentNotice.title"
      width="60%"
      top="10vh"
      destroy-on-close
      @close="handleNoticeDialogClose"
    >
      <div class="notice-detail">
        <div class="notice-detail-header">
          <el-icon class="notice-detail-icon">
            <InfoFilled v-if="currentNotice.type === 'info'" />
            <Warning v-else-if="currentNotice.type === 'warning'" />
            <Notification v-else />
          </el-icon>
          <div class="notice-detail-meta">
            <div class="notice-detail-publisher">发布人：{{ currentNotice.publisherName }}</div>
            <div class="notice-detail-time">{{ formatTime(currentNotice.publishTime) }}</div>
          </div>
        </div>
        <div class="notice-detail-content">
          {{ currentNotice.content }}
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Bell, Notification, Warning, InfoFilled } from '@element-plus/icons-vue'
import { ElCarousel, ElCarouselItem } from 'element-plus'
import axios from 'axios'

// 轮播图数据
const carousels = ref([])

// 轮播图切换时间（毫秒）
const carouselInterval = ref(3000)

// 最近评选活动
const recentEvents = ref([])

// 最近通知
const recentNotices = ref([])

// 通知详情弹窗相关
const noticeDialogVisible = ref(false)
const currentNotice = ref({})

// 通知项点击事件处理
const handleNoticeClick = (notice) => {
  currentNotice.value = { ...notice }
  noticeDialogVisible.value = true
}

// 通知详情弹窗关闭处理
const handleNoticeDialogClose = () => {
  noticeDialogVisible.value = false
  // 清空详情数据
  currentNotice.value = {}
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
    // 如果轮播图数据不为空，使用第一张轮播图的切换时间
    if (carousels.value.length > 0) {
      carouselInterval.value = carousels.value[0].intervalTime || 3000
    }
  } catch (error) {
    console.error('获取轮播图数据失败:', error)
  }
}

onMounted(() => {
  getCarousels()
  getRecentEvents()
  getRecentNotices()
})


// 在HomeView中引入AI通义千问的脚本
createAiChat({
       appId:"2003300836708515841",
       // 支持top-left左上, top-right右上, bottom-left左下, bottom-right右下
       iconPosition:"bottom-right"
    })

</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Playfair+Display:wght@400;500;600;700&family=Source+Sans+3:wght@300;400;500;600;700&family=Noto+Sans+SC:wght@300;400;500;600;700&display=swap');

.home-container {
  padding: 0;
  max-width: 100%;
}

.home-container > h1 {
  font-family: 'Playfair Display', 'Noto Serif SC', Georgia, serif;
  font-size: 28px;
  font-weight: 600;
  color: #1A1A2E;
  margin-bottom: 24px;
  padding: 0 4px;
  position: relative;
  display: inline-block;
}

.home-container > h1::after {
  content: "";
  position: absolute;
  bottom: -8px;
  left: 0;
  width: 60px;
  height: 3px;
  background: linear-gradient(90deg, #D4AF37, #E8B96B);
  border-radius: 2px;
}

/* 轮播图样式 */
.el-carousel {
  margin-bottom: 24px;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 
    0 8px 32px rgba(15, 76, 129, 0.12),
    0 4px 16px rgba(15, 76, 129, 0.08);
}

.el-carousel :deep(.el-carousel__container) {
  height: 50vh !important;
  min-height: 400px;
}

.el-carousel :deep(.el-carousel__arrow) {
  background: rgba(255, 255, 255, 0.9);
  color: #0F4C81;
  width: 48px;
  height: 48px;
  border-radius: 50%;
  box-shadow: 0 4px 16px rgba(15, 76, 129, 0.2);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.el-carousel :deep(.el-carousel__arrow:hover) {
  background: #0F4C81;
  color: white;
  transform: scale(1.1);
}

.el-carousel :deep(.el-carousel__indicators--outside) {
  margin-top: 16px;
}

.el-carousel :deep(.el-carousel__indicator--horizontal .el-carousel__button) {
  width: 32px;
  height: 4px;
  border-radius: 2px;
  background: #D4CFC6;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.el-carousel :deep(.el-carousel__indicator--horizontal.is-active .el-carousel__button) {
  background: linear-gradient(90deg, #0F4C81, #1A6B9C);
  width: 48px;
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

.carousel-item::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(
    180deg,
    rgba(15, 76, 129, 0.1) 0%,
    rgba(15, 76, 129, 0.3) 100%
  );
}

.carousel-content {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  color: #1A1A2E;
  padding: 32px 48px;
  border-radius: 16px;
  text-align: center;
  max-width: 70%;
  position: relative;
  z-index: 1;
  box-shadow: 
    0 8px 32px rgba(15, 76, 129, 0.15),
    0 0 0 1px rgba(255, 255, 255, 0.8);
  border: 1px solid rgba(255, 255, 255, 0.5);
  animation: fadeInUp 0.6s cubic-bezier(0.4, 0, 0.2, 1);
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.carousel-title {
  font-family: 'Playfair Display', 'Noto Serif SC', Georgia, serif;
  font-size: 32px;
  font-weight: 600;
  margin-bottom: 12px;
  color: #0F4C81;
  letter-spacing: -0.5px;
}

.carousel-description {
  font-family: 'Source Sans 3', 'Noto Sans SC', sans-serif;
  font-size: 16px;
  color: #4A5568;
  line-height: 1.6;
}

/* 内容网格布局 */
.content-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
  margin-top: 24px;
}

@media (max-width: 1200px) {
  .content-grid {
    grid-template-columns: 1fr;
  }
}

/* 卡片通用样式 */
.notifications :deep(.el-card),
.recent-events :deep(.el-card) {
  border-radius: 16px;
  border: 1px solid rgba(15, 76, 129, 0.08);
  box-shadow: 
    0 4px 24px rgba(15, 76, 129, 0.06),
    0 0 0 1px rgba(255, 255, 255, 0.8);
  overflow: hidden;
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
}

.notifications :deep(.el-card:hover),
.recent-events :deep(.el-card:hover) {
  box-shadow: 
    0 8px 32px rgba(15, 76, 129, 0.1),
    0 0 0 1px rgba(255, 255, 255, 0.9);
  transform: translateY(-2px);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 10px;
  font-family: 'Playfair Display', 'Noto Serif SC', Georgia, serif;
  font-size: 18px;
  font-weight: 600;
  color: #1A1A2E;
}

.card-header::before {
  content: "";
  width: 4px;
  height: 20px;
  background: linear-gradient(180deg, #D4AF37, #E8B96B);
  border-radius: 2px;
}

.notifications :deep(.el-card__header),
.recent-events :deep(.el-card__header) {
  background: linear-gradient(135deg, #FAF8F5 0%, #F5F2ED 100%);
  border-bottom: 1px solid rgba(15, 76, 129, 0.08);
  padding: 16px 20px;
}

/* 通知列表样式 */
.notifications :deep(.el-list) {
  padding: 8px;
}

.notice-item {
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: 12px;
  padding: 16px !important;
  margin: 4px 0;
  border: 1px solid transparent;
}

.notice-item:hover {
  background: linear-gradient(135deg, #FAF8F5 0%, #F5F2ED 100%);
  transform: translateX(4px);
  border-color: rgba(15, 76, 129, 0.1);
  box-shadow: 0 4px 12px rgba(15, 76, 129, 0.06);
}

.notice-item :deep(.el-icon) {
  font-size: 20px;
  color: #1A6B9C;
}

.notice-content {
  width: 100%;
}

.notice-title {
  font-family: 'Source Sans 3', 'Noto Sans SC', sans-serif;
  font-weight: 600;
  font-size: 15px;
  margin-bottom: 6px;
  color: #1A1A2E;
}

.notice-description {
  font-family: 'Source Sans 3', 'Noto Sans SC', sans-serif;
  font-size: 13px;
  color: #4A5568;
  margin-bottom: 8px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  line-height: 1.5;
}

.notice-time {
  font-family: 'Source Sans 3', 'Noto Sans SC', sans-serif;
  font-size: 12px;
  color: #718096;
  text-align: right;
}

/* 表格样式 */
.recent-events :deep(.el-table) {
  border-radius: 12px;
  overflow: hidden;
}

.recent-events :deep(.el-table th.el-table__cell) {
  background: linear-gradient(135deg, #FAF8F5 0%, #F5F2ED 100%);
  color: #1A1A2E;
  font-weight: 600;
  font-family: 'Source Sans 3', 'Noto Sans SC', sans-serif;
  border-bottom: 1px solid rgba(15, 76, 129, 0.1);
}

.recent-events :deep(.el-table--striped .el-table__body tr.el-table__row--striped td.el-table__cell) {
  background: #FAF8F5;
}

.recent-events :deep(.el-table__body tr:hover > td.el-table__cell) {
  background: linear-gradient(135deg, #F5F2ED 0%, #EBE6DE 100%) !important;
}

.recent-events :deep(.el-table td.el-table__cell) {
  border-bottom: 1px solid rgba(15, 76, 129, 0.06);
  color: #1A1A2E;
}

/* 标签样式 */
.recent-events :deep(.el-tag) {
  border-radius: 20px;
  font-weight: 500;
  padding: 4px 12px;
  border: none;
}

.recent-events :deep(.el-tag--warning) {
  background: linear-gradient(135deg, #D4953A 0%, #E8AB52 100%);
  color: white;
}

.recent-events :deep(.el-tag--success) {
  background: linear-gradient(135deg, #2D8B5E 0%, #3DA76F 100%);
  color: white;
}

.recent-events :deep(.el-tag--info) {
  background: linear-gradient(135deg, #1A6B9C 0%, #3D8EB9 100%);
  color: white;
}

.recent-events :deep(.el-tag--danger) {
  background: linear-gradient(135deg, #C44536 0%, #D65A4A 100%);
  color: white;
}

/* 通知详情弹窗样式 */
:deep(.el-dialog) {
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 16px 64px rgba(15, 76, 129, 0.2);
}

:deep(.el-dialog__header) {
  background: linear-gradient(135deg, #FAF8F5 0%, #F5F2ED 100%);
  border-bottom: 1px solid rgba(15, 76, 129, 0.1);
  padding: 20px 24px;
}

:deep(.el-dialog__title) {
  font-family: 'Playfair Display', 'Noto Serif SC', Georgia, serif;
  font-weight: 600;
  color: #1A1A2E;
}

:deep(.el-dialog__body) {
  padding: 24px;
}

.notice-detail {
  padding: 0;
}

.notice-detail-header {
  display: flex;
  align-items: center;
  gap: 16px;
  padding-bottom: 20px;
  border-bottom: 1px solid rgba(15, 76, 129, 0.1);
  margin-bottom: 20px;
}

.notice-detail-icon {
  font-size: 48px;
  color: #1A6B9C;
  background: linear-gradient(135deg, #FAF8F5 0%, #F5F2ED 100%);
  padding: 12px;
  border-radius: 16px;
}

.notice-detail-meta {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.notice-detail-publisher {
  font-family: 'Source Sans 3', 'Noto Sans SC', sans-serif;
  font-size: 14px;
  font-weight: 500;
  color: #1A1A2E;
}

.notice-detail-time {
  font-family: 'Source Sans 3', 'Noto Sans SC', sans-serif;
  font-size: 13px;
  color: #718096;
}

.notice-detail-content {
  font-family: 'Source Sans 3', 'Noto Sans SC', sans-serif;
  font-size: 15px;
  line-height: 1.8;
  color: #1A1A2E;
  white-space: pre-wrap;
  word-break: break-word;
  background: linear-gradient(135deg, #FAF8F5 0%, #F5F2ED 100%);
  padding: 20px;
  border-radius: 12px;
  border: 1px solid rgba(15, 76, 129, 0.08);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .el-carousel :deep(.el-carousel__container) {
    height: 40vh !important;
    min-height: 300px;
  }
  
  .carousel-content {
    max-width: 90%;
    padding: 24px 32px;
  }
  
  .carousel-title {
    font-size: 24px;
  }
  
  .carousel-description {
    font-size: 14px;
  }
  
  .home-container > h1 {
    font-size: 22px;
  }
}
</style>
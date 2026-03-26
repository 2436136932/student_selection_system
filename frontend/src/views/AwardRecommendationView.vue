<template>
  <div class="recommendation-container">
    <div class="page-header">
      <h2>🤖 AI 智能推荐</h2>
      <p class="subtitle">基于您的成绩和获奖记录，为您智能推荐合适的奖项（推荐仅供参考）</p>
    </div>

    <el-card class="info-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <el-icon><InfoFilled /></el-icon>
          <span>推荐说明</span>
        </div>
      </template>
      <div class="info-content">
        <p>💡 我们的 AI 系统会综合分析以下因素为您推荐奖项：</p>
        <ul>
          <li>📊 <strong>成绩匹配度</strong>（权重 40%）：基于您的平均成绩和 GPA</li>
          <li>🏆 <strong>已有奖项</strong>（权重 30%）：您的历史获奖记录</li>
          <li>🎓 <strong>专业匹配</strong>（权重 15%）：奖项与您的专业相关性</li>
          <li>📈 <strong>历史数据</strong>（权重 10%）：该奖项的历史申请情况</li>
          <li>⚔️ <strong>竞争程度</strong>（权重 5%）：当前申请人数与名额比例</li>
        </ul>
      </div>
    </el-card>

    <el-button 
      type="primary" 
      size="large" 
      @click="loadRecommendations"
      :loading="loading"
      class="refresh-btn"
    >
      <el-icon><Refresh /></el-icon>
      {{ loading ? '分析中...' : '开始智能分析' }}
    </el-button>

    <div v-if="recommendations.length > 0" class="recommendations-list">
      <el-card 
        v-for="(item, index) in recommendations" 
        :key="item.awardId"
        class="recommendation-card"
        shadow="hover"
      >
        <div class="card-content">
          <div class="rank-badge" :class="getRankClass(index)">
            <span class="rank-number">{{ index + 1 }}</span>
          </div>

          <div class="award-info">
            <h3 class="award-name">
              {{ item.awardName }}
              <el-tag :type="getLevelType(item.awardLevel)" size="small">
                {{ getLevelText(item.awardLevel) }}
              </el-tag>
            </h3>
            <p class="award-type">{{ item.awardType }}</p>
          </div>

          <div class="scores-section">
            <div class="score-item">
              <div class="score-label">匹配度</div>
              <el-progress 
                :percentage="item.matchScore" 
                :color="getProgressColor(item.matchScore)"
                :stroke-width="12"
              />
            </div>
            <div class="score-item">
              <div class="score-label">获奖概率</div>
              <el-progress 
                :percentage="item.winProbability" 
                :color="getProgressColor(item.winProbability)"
                :stroke-width="12"
              />
            </div>
          </div>

          <div class="competition-tag">
            <el-tag :type="getCompetitionType(item.competitionLevel)">
              {{ item.competitionLevel }}
            </el-tag>
          </div>

          <div class="recommendation-text">
            <el-alert 
              :title="item.recommendation.split('\n')[0]"
              :description="item.recommendation.split('\n').slice(1).join('\n')"
              :type="getAlertType(item.matchScore)"
              :closable="false"
            />
          </div>

          <div class="action-buttons">
            <el-button 
              type="primary" 
              @click="applyAward(item)"
              :disabled="item.matchScore < 50"
            >
              <el-icon><DocumentAdd /></el-icon>
              立即申请
            </el-button>
            <el-button @click="viewDetails(item)">
              <el-icon><View /></el-icon>
              查看详情
            </el-button>
          </div>
        </div>
      </el-card>
    </div>

    <el-empty 
      v-else-if="!loading && analyzed"
      description="暂无推荐奖项，请先完善您的成绩信息"
    />

    <!-- 奖项详情对话框 -->
    <el-dialog 
      v-model="detailDialogVisible" 
      title="奖项详情"
      width="700px"
      :close-on-click-modal="false"
    >
      <div v-if="selectedAward" class="award-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="奖项名称">
            <strong>{{ selectedAward.awardName }}</strong>
          </el-descriptions-item>
          <el-descriptions-item label="奖项级别">
            <el-tag :type="getLevelType(selectedAward.awardLevel)">
              {{ getLevelText(selectedAward.awardLevel) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="奖项类型">
            {{ selectedAward.awardType }}
          </el-descriptions-item>
          <el-descriptions-item label="匹配度">
            <el-progress 
              :percentage="selectedAward.matchScore" 
              :color="getProgressColor(selectedAward.matchScore)"
              :stroke-width="8"
            />
          </el-descriptions-item>
          <el-descriptions-item label="获奖概率">
            <el-progress 
              :percentage="selectedAward.winProbability" 
              :color="getProgressColor(selectedAward.winProbability)"
              :stroke-width="8"
            />
          </el-descriptions-item>
          <el-descriptions-item label="竞争程度">
            <el-tag :type="getCompetitionType(selectedAward.competitionLevel)">
              {{ selectedAward.competitionLevel }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="AI 推荐建议" :span="2">
            <div style="white-space: pre-line; line-height: 1.8;">
              {{ selectedAward.recommendation }}
            </div>
          </el-descriptions-item>
        </el-descriptions>

        <div style="margin-top: 20px; padding: 15px; background: #f5f7fa; border-radius: 8px;">
          <h4 style="margin-top: 0; color: #409eff;">
            <el-icon><TrendCharts /></el-icon>
            匹配度详细分析
          </h4>
          <el-row :gutter="20">
            <el-col :span="12">
              <div class="detail-item">
                <span class="detail-label">📊 成绩匹配：</span>
                <span class="detail-value">{{ (selectedAward.matchScore * 0.4).toFixed(1) }} 分</span>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="detail-item">
                <span class="detail-label">🏆 奖项匹配：</span>
                <span class="detail-value">{{ (selectedAward.matchScore * 0.3).toFixed(1) }} 分</span>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="detail-item">
                <span class="detail-label">🎓 专业匹配：</span>
                <span class="detail-value">{{ (selectedAward.matchScore * 0.15).toFixed(1) }} 分</span>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="detail-item">
                <span class="detail-label">📈 历史数据：</span>
                <span class="detail-value">{{ (selectedAward.matchScore * 0.1).toFixed(1) }} 分</span>
              </div>
            </el-col>
          </el-row>
        </div>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
          <el-button 
            type="primary" 
            @click="applyFromDialog"
            :disabled="selectedAward && selectedAward.matchScore < 50"
          >
            <el-icon><DocumentAdd /></el-icon>
            立即申请
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { InfoFilled, Refresh, DocumentAdd, View, TrendCharts } from '@element-plus/icons-vue'

const router = useRouter()
const loading = ref(false)
const analyzed = ref(false)
const recommendations = ref([])
const detailDialogVisible = ref(false)
const selectedAward = ref(null)

const loadRecommendations = async () => {
  loading.value = true
  try {
    // 从 userInfo 中获取用户 ID
    const userInfoStr = localStorage.getItem('userInfo')
    if (!userInfoStr) {
      ElMessage.error('请先登录学生账号')
      loading.value = false
      return
    }
    
    const userInfo = JSON.parse(userInfoStr)
    const userId = userInfo.id
    
    if (!userId) {
      ElMessage.error('获取用户信息失败，请重新登录')
      loading.value = false
      return
    }

    const response = await axios.get('/api/recommendations/awards', {
      params: { userId }
    })

    if (response.data.success) {
      recommendations.value = response.data.data
      analyzed.value = true
      ElMessage.success(`为您找到 ${response.data.total} 个推荐奖项`)
    } else {
      ElMessage.error(response.data.message || '获取推荐失败')
    }
  } catch (error) {
    console.error('获取推荐奖项失败:', error)
    ElMessage.error('获取推荐奖项失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

const getRankClass = (index) => {
  if (index === 0) return 'rank-gold'
  if (index === 1) return 'rank-silver'
  if (index === 2) return 'rank-bronze'
  return 'rank-normal'
}

const getLevelType = (level) => {
  const types = {
    'national': 'danger',
    'provincial': 'warning',
    'school': 'success',
    'department': 'info'
  }
  return types[level] || 'info'
}

const getLevelText = (level) => {
  const texts = {
    'national': '国家级',
    'provincial': '省级',
    'school': '校级',
    'department': '院级'
  }
  return texts[level] || level
}

const getProgressColor = (value) => {
  if (value >= 80) return '#67c23a'
  if (value >= 60) return '#e6a23c'
  return '#f56c6c'
}

const getCompetitionType = (level) => {
  if (level.includes('较小')) return 'success'
  if (level.includes('适中')) return 'warning'
  return 'danger'
}

const getAlertType = (matchScore) => {
  if (matchScore >= 85) return 'success'
  if (matchScore >= 70) return 'info'
  if (matchScore >= 60) return 'warning'
  return 'error'
}

const applyAward = (item) => {
  // 跳转到奖项申请页面，携带奖项ID参数
  router.push({
    path: '/student-award-applications',
    query: { awardId: item.awardId }
  })
}

const viewDetails = (item) => {
  // 显示奖项详情对话框
  selectedAward.value = item
  detailDialogVisible.value = true
}

const applyFromDialog = () => {
  // 从对话框中点击立即申请
  if (selectedAward.value) {
    detailDialogVisible.value = false
    applyAward(selectedAward.value)
  }
}

onMounted(() => {
  // 页面加载时不自动获取，等用户点击按钮
})
</script>

<style scoped>
.recommendation-container {
  padding: 30px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  min-height: 100vh;
}

.page-header {
  text-align: center;
  margin-bottom: 30px;
}

.page-header h2 {
  font-size: 2.5rem;
  color: #2c3e50;
  margin-bottom: 10px;
}

.subtitle {
  font-size: 1.1rem;
  color: #606266;
}

.info-card {
  margin-bottom: 30px;
  border-radius: 15px;
  max-width: 900px;
  margin-left: auto;
  margin-right: auto;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 1.2rem;
  font-weight: 600;
  color: #409eff;
}

.info-content {
  line-height: 1.8;
}

.info-content ul {
  margin-top: 15px;
  padding-left: 20px;
}

.info-content li {
  margin-bottom: 10px;
}

.refresh-btn {
  display: block;
  margin: 0 auto 40px;
  padding: 15px 40px;
  font-size: 1.1rem;
  border-radius: 25px;
}

.recommendations-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(500px, 1fr));
  gap: 25px;
  max-width: 1400px;
  margin: 0 auto;
}

.recommendation-card {
  border-radius: 20px;
  overflow: hidden;
  transition: all 0.3s ease;
  position: relative;
}

.recommendation-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.15);
}

.card-content {
  padding: 10px;
}

.rank-badge {
  position: absolute;
  top: 15px;
  right: 15px;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 1.2rem;
  color: white;
}

.rank-gold { background: linear-gradient(135deg, #ffd700 0%, #ffed4e 100%); }
.rank-silver { background: linear-gradient(135deg, #c0c0c0 0%, #e8e8e8 100%); }
.rank-bronze { background: linear-gradient(135deg, #cd7f32 0%, #daa520 100%); }
.rank-normal { background: linear-gradient(135deg, #909399 0%, #b3b3b3 100%); }

.award-info {
  margin-bottom: 20px;
  padding-right: 50px;
}

.award-name {
  font-size: 1.4rem;
  color: #2c3e50;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.award-type {
  color: #909399;
  font-size: 0.95rem;
}

.scores-section {
  margin-bottom: 20px;
}

.score-item {
  margin-bottom: 15px;
}

.score-label {
  font-size: 0.9rem;
  color: #606266;
  margin-bottom: 8px;
  font-weight: 500;
}

.competition-tag {
  margin-bottom: 15px;
}

.recommendation-text {
  margin-bottom: 20px;
}

.action-buttons {
  display: flex;
  gap: 10px;
  justify-content: center;
}

/* 奖项详情对话框样式 */
.award-detail {
  padding: 10px 0;
}

.detail-item {
  padding: 10px;
  background: white;
  border-radius: 5px;
  margin-bottom: 10px;
}

.detail-label {
  font-weight: 500;
  color: #606266;
  margin-right: 8px;
}

.detail-value {
  color: #409eff;
  font-weight: 600;
  font-size: 1.1rem;
}

@media (max-width: 768px) {
  .recommendations-list {
    grid-template-columns: 1fr;
  }
  
  .page-header h2 {
    font-size: 2rem;
  }
}
</style>
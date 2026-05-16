<template>
  <div class="recommendation-container">
    <div class="page-header">
      <h2>🤖 AI 智能推荐</h2>
      <p class="subtitle">基于您的成绩和获奖记录，为您智能推荐合适的奖项（推荐仅供参考）</p>
      <!-- 管理员权重配置按钮 -->
      <el-button 
        v-if="userInfo.role === 'admin'"
        type="warning" 
        @click="openWeightDialog"
        class="weight-config-btn"
      >
        <el-icon><Setting /></el-icon>
        权重配置
      </el-button>
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
          <li>📊 <strong>成绩匹配度</strong>（权重 {{ aiWeights.gradeWeight }}%）：基于您的平均成绩和 GPA</li>
          <li>🏆 <strong>已有奖项</strong>（权重 {{ aiWeights.awardWeight }}%）：您的历史获奖记录</li>
          <li>🎓 <strong>专业匹配</strong>（权重 {{ aiWeights.majorWeight }}%）：奖项与您的专业相关性</li>
          <li>📈 <strong>历史数据</strong>（权重 {{ aiWeights.historyWeight }}%）：该奖项的历史申请情况</li>
          <li>⚔️ <strong>竞争程度</strong>（权重 {{ aiWeights.competitionWeight }}%）：当前申请人数与名额比例</li>
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
      v-else-if="analyzed"
      :description="emptyDescription"
    >
      <template v-if="diagnostic" #default>
        <div class="diagnostic-info">
          <p><strong>诊断信息：</strong></p>
          <p>📋 数据库中奖项总数：{{ diagnostic.totalAwardsInDb }}</p>
          <p>✅ 已发布且进行中的奖项：{{ diagnostic.availableAwards }}</p>
          <p>📝 您已申请的奖项：{{ diagnostic.alreadyAppliedCount }}</p>
          <p>📊 您的成绩记录数：{{ diagnostic.scoreCount }}</p>
          <p>🏆 您的获奖记录数：{{ diagnostic.awardRecordCount }}</p>
          <div v-if="diagnostic.awardDetails" class="award-status-list">
            <p v-for="a in diagnostic.awardDetails" :key="a.id" style="font-size:12px;margin:4px 0;">
              「{{ a.name }}」状态={{ a.status }}/{{ a.currentStatus }} 
              <el-tag v-if="a.alreadyApplied" type="warning" size="small">已申请</el-tag>
              <el-tag v-else type="success" size="small">未申请</el-tag>
            </p>
          </div>
        </div>
      </template>
    </el-empty>

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
                <span class="detail-label">📊 成绩匹配（{{ selectedAward.gradeWeight }}%）：</span>
                <span class="detail-value">{{ selectedAward.gradeScore.toFixed(1) }} 分</span>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="detail-item">
                <span class="detail-label">🏆 奖项匹配（{{ selectedAward.awardWeight }}%）：</span>
                <span class="detail-value">{{ selectedAward.awardScore.toFixed(1) }} 分</span>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="detail-item">
                <span class="detail-label">🎓 专业匹配（{{ selectedAward.majorWeight }}%）：</span>
                <span class="detail-value">{{ selectedAward.majorScore.toFixed(1) }} 分</span>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="detail-item">
                <span class="detail-label">📈 历史数据（{{ selectedAward.historyWeight }}%）：</span>
                <span class="detail-value">{{ selectedAward.historyScore.toFixed(1) }} 分</span>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="detail-item">
                <span class="detail-label">⚡ 竞争程度（{{ selectedAward.competitionWeight }}%）：</span>
                <span class="detail-value">{{ selectedAward.competitionScore.toFixed(1) }} 分</span>
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

    <!-- 权重配置对话框 -->
    <el-dialog 
      v-model="weightDialogVisible" 
      title="AI智能推荐权重配置"
      width="700px"
      :close-on-click-modal="false"
    >
      <div class="weight-config-section">
        <p class="weight-config-desc">配置AI智能推荐算法的权重参数，总权重必须为100%</p>
        
        <el-form :model="aiWeights" label-position="top" class="weight-form">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="成绩匹配度权重">
                <el-slider 
                  v-model="aiWeights.gradeWeight" 
                  :min="0" 
                  :max="100" 
                  :step="1"
                  show-input
                />
                <span class="setting-desc">基于学生的平均成绩和GPA</span>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="已有奖项权重">
                <el-slider 
                  v-model="aiWeights.awardWeight" 
                  :min="0" 
                  :max="100" 
                  :step="1"
                  show-input
                />
                <span class="setting-desc">学生的历史获奖记录</span>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="专业匹配权重">
                <el-slider 
                  v-model="aiWeights.majorWeight" 
                  :min="0" 
                  :max="100" 
                  :step="1"
                  show-input
                />
                <span class="setting-desc">奖项与学生专业的相关性</span>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="历史数据权重">
                <el-slider 
                  v-model="aiWeights.historyWeight" 
                  :min="0" 
                  :max="100" 
                  :step="1"
                  show-input
                />
                <span class="setting-desc">该奖项的历史申请情况</span>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="竞争程度权重">
                <el-slider 
                  v-model="aiWeights.competitionWeight" 
                  :min="0" 
                  :max="100" 
                  :step="1"
                  show-input
                />
                <span class="setting-desc">当前申请人数与名额比例</span>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="总权重">
                <el-input 
                  :model-value="totalWeight" 
                  disabled 
                  :class="{ 'weight-error': totalWeight !== 100 }"
                />
                <span class="setting-desc" :class="{ 'weight-error-text': totalWeight !== 100 }">
                  {{ totalWeight === 100 ? '✓ 权重总和正确' : '✗ 权重总和必须为100%' }}
                </span>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="weightDialogVisible = false">取消</el-button>
          <el-button 
            type="primary" 
            @click="saveWeights"
            :disabled="totalWeight !== 100"
          >
            保存配置
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { InfoFilled, Refresh, DocumentAdd, View, TrendCharts, Setting } from '@element-plus/icons-vue'

const router = useRouter()
const loading = ref(false)
const analyzed = ref(false)
const recommendations = ref([])
const diagnostic = ref(null)
const detailDialogVisible = ref(false)
const selectedAward = ref(null)

// 用户信息
const userInfoStr = localStorage.getItem('userInfo')
const userInfo = userInfoStr ? JSON.parse(userInfoStr) : {}

// 权重配置对话框
const weightDialogVisible = ref(false)

// 打开权重配置对话框
const openWeightDialog = async () => {
  await initAIWeights()
  weightDialogVisible.value = true
}

// AI智能推荐权重配置
const aiWeights = ref({
  gradeWeight: 40,
  awardWeight: 30,
  majorWeight: 15,
  historyWeight: 10,
  competitionWeight: 5
})

// 计算总权重
const totalWeight = computed(() => {
  return aiWeights.value.gradeWeight + aiWeights.value.awardWeight + aiWeights.value.majorWeight + aiWeights.value.historyWeight + aiWeights.value.competitionWeight
})

// 空推荐时的描述文案
const emptyDescription = computed(() => {
  if (!diagnostic.value) return '暂无推荐奖项'
  if (diagnostic.value.availableAwards === 0) return '无可用奖项（需管理员发布奖项并设为"进行中"）'
  if (diagnostic.value.alreadyAppliedCount > 0 && diagnostic.value.alreadyAppliedCount >= diagnostic.value.availableAwards)
    return '您已申请所有可用奖项，无可推荐内容'
  if (diagnostic.value.scoreCount === 0) return '您暂未录入成绩，请先联系教师录入'
  return '暂无推荐奖项'
})

// 保存权重配置
const saveWeights = async () => {
  if (totalWeight.value !== 100) {
    ElMessage.error('权重总和必须为100%')
    return
  }
  
  try {
    const response = await axios.post('/api/recommendation-weights', aiWeights.value)
    
    if (response.data.success) {
      localStorage.setItem('aiWeights', JSON.stringify(aiWeights.value))
      ElMessage.success('权重配置已保存')
      weightDialogVisible.value = false
    } else {
      ElMessage.error(response.data.message || '保存失败')
    }
  } catch (error) {
    console.error('保存权重配置失败:', error)
    ElMessage.error('保存权重配置失败，请稍后重试')
  }
}

// 初始化AI权重设置
const initAIWeights = async () => {
  console.log('开始加载权重配置...')
  try {
    const response = await axios.get('/api/recommendation-weights')
    console.log('API响应:', response.data)
    
    if (response.data.success && response.data.data) {
      const weights = response.data.data
      console.log('从API获取的权重:', weights)
      aiWeights.value = {
        gradeWeight: Math.round(Number(weights.gradeWeight)) || 40,
        awardWeight: Math.round(Number(weights.awardWeight)) || 30,
        majorWeight: Math.round(Number(weights.majorWeight)) || 15,
        historyWeight: Math.round(Number(weights.historyWeight)) || 10,
        competitionWeight: Math.round(Number(weights.competitionWeight)) || 5,
        id: weights.id
      }
      console.log('设置后的aiWeights:', aiWeights.value)
      localStorage.setItem('aiWeights', JSON.stringify(aiWeights.value))
    }
  } catch (error) {
    console.error('加载权重配置失败:', error)
    const savedWeights = localStorage.getItem('aiWeights')
    if (savedWeights) {
      try {
        aiWeights.value = JSON.parse(savedWeights)
      } catch (e) {
        console.error('解析本地权重设置失败:', e)
      }
    }
  }
}

const loadRecommendations = async () => {
  loading.value = true
  diagnostic.value = null
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
      diagnostic.value = response.data.diagnostic || null
      analyzed.value = true
      if (response.data.total > 0) {
        ElMessage.success(`为您找到 ${response.data.total} 个推荐奖项`)
      }
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
  // 初始化AI权重设置
  initAIWeights()
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

/* 权重配置按钮样式 */
.weight-config-btn {
  margin-top: 20px;
  border-radius: 20px;
  padding: 8px 20px;
}

/* 权重配置对话框样式 */
.weight-config-section {
  padding: 20px 0;
}

.weight-config-desc {
  margin-bottom: 20px;
  color: #606266;
  font-size: 14px;
}

.weight-form {
  padding: 10px 0;
}

.weight-error {
  border-color: #f56c6c !important;
}

.weight-error-text {
  color: #f56c6c !important;
}

.setting-desc {
  margin-left: 10px;
  color: #909399;
  font-size: 14px;
}

.diagnostic-info {
  text-align: left;
  padding: 10px 20px;
  background: #f5f7fa;
  border-radius: 8px;
  margin-top: 10px;
  max-width: 500px;
}

.diagnostic-info p {
  margin: 6px 0;
  font-size: 13px;
  color: #606266;
}

.award-status-list {
  margin-top: 8px;
  padding: 8px;
  background: #fff;
  border-radius: 4px;
  border: 1px solid #ebeef5;
}

@media (max-width: 768px) {
  .recommendations-list {
    grid-template-columns: 1fr;
  }
  
  .page-header h2 {
    font-size: 2rem;
  }
  
  .weight-form .el-row {
    flex-direction: column;
  }
  
  .weight-form .el-col {
    width: 100% !important;
    margin-bottom: 20px;
  }
}
</style>
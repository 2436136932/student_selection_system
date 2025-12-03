<template>
  <div class="award-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>评奖评优管理</span>
          <el-button v-if="hasRole('admin')" type="primary" @click="dialogVisible = true">
            <el-icon><plus /></el-icon> 新增评奖
          </el-button>
        </div>
      </template>
      <div class="card-body">
        <el-form :model="searchForm" :inline="true" class="search-form">
          <el-form-item label="学生学号">
            <el-input v-model="searchForm.studentId" placeholder="请输入学号"></el-input>
          </el-form-item>
          <el-form-item label="奖项名称">
            <el-input v-model="searchForm.awardName" placeholder="请输入奖项名称"></el-input>
          </el-form-item>
          <el-form-item label="评选年份">
            <el-input v-model="searchForm.year" placeholder="请输入评选年份"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="resetForm">重置</el-button>
          </el-form-item>
        </el-form>

        <el-table :data="awards" style="width: 100%" stripe v-loading="loading">
          <el-table-column prop="awardId" label="奖项ID" width="120"></el-table-column>
          <el-table-column prop="studentId" label="学生学号" width="120"></el-table-column>
          <el-table-column prop="awardName" label="奖项名称" width="150"></el-table-column>
          <el-table-column prop="awardLevel" label="奖项级别" width="100"></el-table-column>
          <el-table-column prop="year" label="评选年份" width="100"></el-table-column>
          <el-table-column prop="description" label="获奖描述" min-width="200"></el-table-column>
          <el-table-column label="操作" width="150" fixed="right" v-if="hasRole('admin')">
            <template #default="scope">
              <el-button size="small" type="primary" @click="handleEdit(scope.row)">
                <el-icon><edit /></el-icon> 编辑
              </el-button>
              <el-button size="small" type="danger" @click="handleDelete(scope.row)">
                <el-icon><delete /></el-icon> 删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination">
          <el-pagination
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page="currentPage"
            :page-sizes="[10, 20, 50, 100]"
            :page-size="pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
          ></el-pagination>
        </div>

        <div class="filter-section" style="margin-top: 20px">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <span>奖项筛选</span>
              </div>
            </template>
            <div class="filter-body">
              <el-form :model="filterForm" :inline="true">
                <el-form-item label="筛选条件">
                  <el-select v-model="filterForm.filterType" placeholder="请选择筛选条件">
                    <el-option label="GPA大于" value="gpa_gt"></el-option>
                    <el-option label="获奖次数大于" value="awards_count_gt"></el-option>
                    <el-option label="特定奖项" value="award_name"></el-option>
                  </el-select>
                </el-form-item>
                <el-form-item label="筛选值">
                  <el-input v-model="filterForm.filterValue" placeholder="请输入筛选值"></el-input>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="handleFilter">筛选</el-button>
                </el-form-item>
              </el-form>
              <div v-if="filteredStudents.length > 0" style="margin-top: 20px">
                <el-table :data="filteredStudents" style="width: 100%" stripe>
                  <el-table-column prop="studentId" label="学生学号" width="120"></el-table-column>
                  <el-table-column prop="name" label="学生姓名" width="120"></el-table-column>
                  <el-table-column prop="gender" label="性别" width="80">
                    <template #default="scope">
                      {{ scope.row.gender === 1 ? '男' : '女' }}
                    </template>
                  </el-table-column>
                  <el-table-column prop="major.name" label="专业" width="150"></el-table-column>
                </el-table>
              </div>
            </div>
          </el-card>
        </div>
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="奖项信息"
      width="500px"
    >
      <el-form :model="form" label-width="100px">
        <el-form-item label="奖项ID">
          <el-input v-model="form.awardId" placeholder="请输入奖项ID"></el-input>
        </el-form-item>
        <el-form-item label="学生学号">
          <el-input v-model="form.studentId" placeholder="请输入学号"></el-input>
        </el-form-item>
        <el-form-item label="奖项名称">
          <el-input v-model="form.awardName" placeholder="请输入奖项名称"></el-input>
        </el-form-item>
        <el-form-item label="奖项级别">
          <el-select v-model="form.awardLevel" placeholder="请选择奖项级别">
            <el-option label="国家级" value="国家级"></el-option>
            <el-option label="省级" value="省级"></el-option>
            <el-option label="校级" value="校级"></el-option>
            <el-option label="院级" value="院级"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="评选年份">
          <el-input v-model="form.year" placeholder="请输入评选年份"></el-input>
        </el-form-item>
        <el-form-item label="获奖描述">
          <el-input v-model="form.description" type="textarea" placeholder="请输入获奖描述"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 数据可视化图表 -->
    <div class="chart-section" style="margin-top: 20px">
      <el-card shadow="hover">
        <template #header>
          <div class="card-header">
            <span>奖项数据统计</span>
          </div>
        </template>
        <div class="chart-body">
          <div style="height: 300px; margin-bottom: 20px;">
              <div v-if="awards.length === 0" class="chart-placeholder">
                <el-empty description="暂无数据" style="margin-top: 80px;"></el-empty>
              </div>
              <div v-else class="chart-container">
                <div class="chart-item" v-for="level in awardLevels" :key="level.value">
                  <div class="chart-bar" :style="{ height: getBarHeight(level.value) + '%', backgroundColor: level.color }"></div>
                  <div class="chart-label">{{ level.label }}</div>
                  <div class="chart-count">{{ getAwardCountByLevel(level.value) }}</div>
                </div>
              </div>
            </div>
          <div class="stats-grid">
            <div class="stat-item">
              <div class="stat-label">总奖项数</div>
              <div class="stat-value">{{ total.value }}</div>
            </div>
            <div class="stat-item">
              <div class="stat-label">国家级奖项</div>
              <div class="stat-value">{{ getAwardCountByLevel('国家级') }}</div>
            </div>
            <div class="stat-item">
              <div class="stat-label">省级奖项</div>
              <div class="stat-value">{{ getAwardCountByLevel('省级') }}</div>
            </div>
            <div class="stat-item">
              <div class="stat-label">校级奖项</div>
              <div class="stat-value">{{ getAwardCountByLevel('校级') }}</div>
            </div>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Edit, Delete, Plus } from '@element-plus/icons-vue'
import axios from 'axios'

// 获取用户信息
const getUserInfo = () => {
  const userInfoStr = localStorage.getItem('userInfo')
  return userInfoStr ? JSON.parse(userInfoStr) : {}
}

// 检查用户是否有指定角色
const hasRole = (role) => {
  const userInfo = getUserInfo()
  return userInfo.role === role
}

const awards = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const dialogVisible = ref(false)
const filteredStudents = ref([])
const loading = ref(false)
const chartLoading = ref(false)

const searchForm = reactive({
  studentId: '',
  awardName: '',
  year: ''
})

const filterForm = reactive({
  filterType: '',
  filterValue: ''
})

const form = reactive({
  awardId: '',
  studentId: '',
  awardName: '',
  awardLevel: '',
  year: '',
  description: ''
})

// 奖项级别配置
const awardLevels = [
  { label: '国家级', value: '国家级', color: '#ff6b6b' },
  { label: '省级', value: '省级', color: '#4ecdc4' },
  { label: '校级', value: '校级', color: '#45b7d1' },
  { label: '院级', value: '院级', color: '#96ceb4' }
]

// 根据奖项级别统计数量
const getAwardCountByLevel = (level) => {
  return awards.value.filter(award => award.awardLevel === level).length
}

// 计算柱状图高度
const getBarHeight = (level) => {
  const maxCount = Math.max(...awardLevels.map(l => getAwardCountByLevel(l.value)))
  if (maxCount === 0) return 0
  return (getAwardCountByLevel(level) / maxCount) * 100
}

// 获取奖项列表
const getAwards = () => {
  loading.value = true
  axios.get('/api/awards', {
    params: {
      page: currentPage.value,
      size: pageSize.value,
      studentId: searchForm.studentId,
      awardName: searchForm.awardName,
      year: searchForm.year
    }
  }).then(response => {
    awards.value = response.data.content
    total.value = response.data.totalElements
    loading.value = false
  }).catch(error => {
    ElMessage.error('获取奖项列表失败')
    console.error('Error fetching awards:', error)
    loading.value = false
  })
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  getAwards()
}

// 重置表单
const resetForm = () => {
  searchForm.studentId = ''
  searchForm.awardName = ''
  searchForm.year = ''
  currentPage.value = 1
  getAwards()
}

// 分页处理
const handleSizeChange = (size) => {
  pageSize.value = size
  getAwards()
}

const handleCurrentChange = (current) => {
  currentPage.value = current
  getAwards()
}

// 新增/编辑奖项
const handleSubmit = () => {
  if (form.awardId) {
    // 编辑奖项
    axios.put(`/api/awards/${form.awardId}`, form)
      .then(response => {
        ElMessage.success('编辑奖项成功')
        dialogVisible.value = false
        getAwards()
      })
      .catch(error => {
        ElMessage.error('编辑奖项失败')
        console.error('Error updating award:', error)
      })
  } else {
    // 新增奖项
    axios.post('/api/awards', form)
      .then(response => {
        ElMessage.success('新增奖项成功')
        dialogVisible.value = false
        getAwards()
      })
      .catch(error => {
        ElMessage.error('新增奖项失败')
        console.error('Error creating award:', error)
      })
  }
}

// 编辑奖项
const handleEdit = (row) => {
  Object.assign(form, row)
  dialogVisible.value = true
}

// 删除奖项
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除该奖项记录吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    axios.delete(`/api/awards/${row.awardId}`)
      .then(response => {
        ElMessage.success('删除奖项成功')
        getAwards()
      })
      .catch(error => {
        ElMessage.error('删除奖项失败')
        console.error('Error deleting award:', error)
      })
  }).catch(() => {
    // 取消删除
  })
}

// 筛选学生
const handleFilter = () => {
  if (!filterForm.filterType || !filterForm.filterValue) {
    ElMessage.warning('请选择筛选条件并输入筛选值')
    return
  }
  
  // 根据筛选条件调用不同的API
  let apiPath = ''
  let params = {}
  
  loading.value = true
  
  if (filterForm.filterType === 'gpa_gt') {
    // GPA大于筛选
    apiPath = '/api/student-filter/scholarship'
    params = { minAverageScore: parseFloat(filterForm.filterValue) }
  } else if (filterForm.filterType === 'awards_count_gt') {
    // 获奖次数大于筛选
    apiPath = '/api/student-filter/honor'
    params = { minAwardCount: parseInt(filterForm.filterValue) }
  } else if (filterForm.filterType === 'award_name') {
    // 特定奖项筛选（需要根据后端API调整）
    apiPath = '/api/student-filter/comprehensive'
    params = { awardName: filterForm.filterValue }
  }
  
  axios.get(apiPath, { params }) 
    .then(response => {
      filteredStudents.value = response.data
      loading.value = false
    }).catch(error => {
      ElMessage.error('筛选学生失败')
      console.error('Error filtering students:', error)
      loading.value = false
    })
}

// 初始化数据
getAwards()
</script>

<style scoped>
.award-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 8px;
}

.search-form .el-form-item {
  margin-bottom: 10px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}

.filter-section {
  margin-top: 20px;
}

.filter-body {
  padding: 10px;
}

.chart-section {
  margin-top: 20px;
}

.chart-body {
  padding: 10px;
}

.chart-placeholder {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
}

.chart-container {
  display: flex;
  justify-content: space-around;
  align-items: flex-end;
  height: 100%;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 8px;
}

.chart-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
  margin: 0 10px;
}

.chart-bar {
  width: 60px;
  border-radius: 4px 4px 0 0;
  transition: height 0.5s ease;
}

.chart-label {
  margin-top: 10px;
  font-size: 14px;
  color: #606266;
}

.chart-count {
  margin-top: 5px;
  font-size: 18px;
  font-weight: bold;
  color: #409eff;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.stat-item {
  background-color: #f5f7fa;
  padding: 20px;
  border-radius: 8px;
  text-align: center;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.stat-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.stat-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 10px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #409eff;
}

@media (max-width: 768px) {
  .award-container {
    padding: 10px;
  }
  
  .search-form {
    flex-direction: column;
    align-items: stretch;
  }
  
  .chart-container {
    padding: 10px;
  }
  
  .chart-bar {
    width: 40px;
  }
}
</style>
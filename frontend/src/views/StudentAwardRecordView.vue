<template>
  <div class="student-award-record-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">获奖记录</h2>
    </div>

    <!-- 搜索条件区域 -->
    <el-card class="search-card" shadow="hover">
      <el-form :inline="true" :model="searchParams" class="search-form">
        <!-- 学号 -->
        <el-form-item label="学号">
          <el-input 
            v-model="searchParams.studentNumber" 
            placeholder="请输入学号"
            clearable
            :disabled="hasRole('student')"
            style="width: 180px"
          ></el-input>
        </el-form-item>

        <!-- 班级 -->
        <el-form-item label="班级">
          <el-input 
            v-model="searchParams.className" 
            placeholder="请输入班级"
            clearable
            :disabled="hasRole('student')"
            style="width: 150px"
          ></el-input>
        </el-form-item>

        <!-- 专业 -->
        <el-form-item label="专业">
          <el-input 
            v-model="searchParams.major" 
            placeholder="请输入专业"
            clearable
            :disabled="hasRole('student')"
            style="width: 200px"
          ></el-input>
        </el-form-item>

        <!-- 奖项名称 -->
        <el-form-item label="奖项名称">
          <el-select 
            v-model="searchParams.awardName" 
            placeholder="请选择奖项名称"
            clearable
            style="width: 200px"
          >
            <el-option 
              v-for="award in awardOptions" 
              :key="award.awardId" 
              :label="award.awardName" 
              :value="award.awardName"
            ></el-option>
          </el-select>
        </el-form-item>

        <!-- 奖项级别 -->
        <el-form-item label="奖项级别">
          <el-select 
            v-model="searchParams.awardLevel" 
            placeholder="请选择奖项级别"
            clearable
            style="width: 150px"
          >
            <el-option label="国家级" value="national"></el-option>
            <el-option label="省级" value="provincial"></el-option>
            <el-option label="校级" value="school"></el-option>
            <el-option label="院级" value="department"></el-option>
          </el-select>
        </el-form-item>

        <!-- 奖项类型 -->
        <el-form-item label="奖项类型">
          <el-select 
            v-model="searchParams.awardType" 
            placeholder="请选择奖项类型"
            clearable
            style="width: 150px"
          >
            <el-option 
              v-for="type in awardTypeOptions" 
              :key="type.value" 
              :label="type.label" 
              :value="type.value"
            ></el-option>
          </el-select>
        </el-form-item>

        <!-- 获奖时间范围 -->
        <el-form-item label="获奖时间">
          <el-date-picker
            v-model="searchParams.awardTimeRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 300px"
          ></el-date-picker>
        </el-form-item>

        <!-- 搜索和重置按钮 -->
        <el-form-item>
          <el-button type="primary" @click="handleSearch" icon="Search">搜索</el-button>
          <el-button @click="resetSearch" icon="Refresh">重置</el-button>
          <el-dropdown trigger="click">
            <el-button type="success" icon="Download">
              导出Excel
              <el-icon class="el-icon--right">
                <ArrowDown />
              </el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="exportExcel(true)">导出全部</el-dropdown-item>
                <el-dropdown-item @click="exportExcel(false)" :disabled="selectedRecords.length === 0">导出选中</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格区域 -->
    <el-card class="table-card" shadow="hover">
      <el-table 
        v-loading="loading" 
        :data="awardRecords" 
        style="width: 100%"
        border
        ref="recordTable"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center"></el-table-column>
        <el-table-column prop="studentName" label="学生姓名" width="120" align="center"></el-table-column>
        <el-table-column prop="studentNumber" label="学号" width="150" align="center"></el-table-column>
        <el-table-column prop="className" label="班级" width="150" align="center"></el-table-column>
        <el-table-column prop="major" label="专业" width="180" align="center"></el-table-column>
        <el-table-column prop="awardName" label="奖项名称" width="180" align="center"></el-table-column>
        <el-table-column prop="awardLevel" label="奖项级别" width="120" align="center">
          <template #default="scope">
            <el-tag 
              v-if="scope.row.awardLevel === 'national'" 
              type="danger"
            >国家级</el-tag>
            <el-tag 
              v-else-if="scope.row.awardLevel === 'provincial'" 
              type="warning"
            >省级</el-tag>
            <el-tag 
              v-else-if="scope.row.awardLevel === 'school'" 
              type="success"
            >校级</el-tag>
            <el-tag 
              v-else-if="scope.row.awardLevel === 'department'" 
              type="info"
            >院级</el-tag>
            <span v-else>{{ scope.row.awardLevel }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="awardType" label="奖项类型" width="120" align="center"></el-table-column>
        <el-table-column prop="awardTime" label="获奖时间" width="180" align="center">
          <template #default="scope">
            {{ formatDate(scope.row.awardTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="记录创建时间" width="180" align="center">
          <template #default="scope">
            {{ formatDate(scope.row.createdAt) }}
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        ></el-pagination>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive, computed } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { formatDate } from '../utils/dateUtils'
import { ArrowDown } from '@element-plus/icons-vue'
import { useUserStore } from '../store/user'

const userStore = useUserStore()

// 搜索参数
const searchParams = reactive({
  awardName: '',
  awardLevel: '',
  awardType: '',
  awardTimeRange: [],
  studentId: '', // 学生ID，管理员和教师可以查看所有，学生只能查看自己的
  studentNumber: '',
  className: '',
  major: ''
})

// 奖项选项
const awardOptions = ref([])

// 奖项类型选项
const awardTypeOptions = ref([])

// 获奖记录列表
const awardRecords = ref([])

// 加载状态
const loading = ref(false)

// 分页信息
const pagination = reactive({
  currentPage: 1,
  pageSize: 10
})

// 总记录数
const total = ref(0)

// 选中的记录
const selectedRecords = ref([])

// 表格引用
const recordTable = ref(null)

// 用户信息
const userInfo = computed(() => userStore.userInfo)

// 获取奖项列表
const getAwards = async () => {
  try {
    const response = await axios.get('/api/awards')
    if (response.data && response.data.records) {
      awardOptions.value = response.data.records
    }
  } catch (error) {
    console.error('获取奖项列表失败:', error)
    ElMessage.error('获取奖项列表失败')
  }
}

// 获取奖项类型列表
const getAwardTypes = async () => {
  try {
    const response = await axios.get('/api/award-types/all')
    if (response.data) {
      // 提取不重复的奖项类型
      const typeSet = new Set()
      response.data.forEach(award => {
        if (award.awardType) {
          typeSet.add(award.awardType)
        }
      })
      // 转换为下拉框选项格式
      awardTypeOptions.value = Array.from(typeSet).map(type => ({
        label: type,
        value: type
      }))
    }
  } catch (error) {
    console.error('获取奖项类型列表失败:', error)
    ElMessage.error('获取奖项类型列表失败')
  }
}

// 获取获奖记录
const getAwardRecords = async () => {
  loading.value = true
  try {
    // 构建查询参数
    const params = {
      page: pagination.currentPage,
      size: pagination.pageSize,
      awardName: searchParams.awardName,
      awardLevel: searchParams.awardLevel,
      awardType: searchParams.awardType,
      studentNumber: searchParams.studentNumber,
      className: searchParams.className,
      major: searchParams.major
    }

    // 添加时间范围参数
    if (searchParams.awardTimeRange && searchParams.awardTimeRange.length === 2) {
      params.startTime = searchParams.awardTimeRange[0]
      params.endTime = searchParams.awardTimeRange[1]
    }

    // 根据用户角色设置学生ID和学号
    const userInfo = getUserInfo()
    if (hasRole('student')) {
      params.studentId = userInfo.id || ''
      // 自动填充学生学号到搜索参数中
      searchParams.studentNumber = userInfo.studentNumber || ''
    }

    const response = await axios.get('/api/student-award-records/condition', { params })
    if (response.data) {
      awardRecords.value = response.data.records || []
      total.value = response.data.total || 0
    }
  } catch (error) {
    console.error('获取获奖记录失败:', error)
    ElMessage.error('获取获奖记录失败')
  } finally {
    loading.value = false
  }
}

// 处理选择变化事件
const handleSelectionChange = (selection) => {
  selectedRecords.value = selection
}

// 搜索处理
const handleSearch = () => {
  pagination.currentPage = 1
  getAwardRecords()
}

// 获取用户信息
const getUserInfo = () => userStore.userInfo

// 检查用户是否有指定角色
const hasRole = (role) => {
  const userInfo = getUserInfo()
  const userRole = userInfo.role || ''
  // 支持中文角色名称和多个角色
  const roleMap = {
    'admin': ['admin', '管理员'],
    'teacher': ['teacher', '教师'],
    'student': ['student', '学生']
  }
  
  const allowedRoles = roleMap[role] || [role]
  return allowedRoles.includes(userRole)
}

// 重置搜索
const resetSearch = () => {
  // 保存学生角色的关键搜索参数
  const { studentNumber, className, major } = searchParams
  
  // 重置搜索参数
  Object.assign(searchParams, {
    awardName: '',
    awardLevel: '',
    awardType: '',
    awardTimeRange: [],
    // 如果是学生角色，保留学号、班级和专业
    studentNumber: hasRole('student') ? studentNumber : '',
    className: hasRole('student') ? className : '',
    major: hasRole('student') ? major : ''
  })
  // 重置分页
  pagination.currentPage = 1
  pagination.pageSize = 10
  // 重新获取数据
  getAwardRecords()
}

// 导出Excel
// exportExcel方法, isExportAll为true时导出全部, 为false时导出选中
const exportExcel = async (isExportAll = true) => {
  try {
    // 构建查询参数
    const params = {
      awardName: searchParams.awardName,
      awardLevel: searchParams.awardLevel,
      awardType: searchParams.awardType,
      studentNumber: searchParams.studentNumber,
      className: searchParams.className,
      major: searchParams.major,
      isExportAll: isExportAll
    }

    // 添加时间范围参数
    if (searchParams.awardTimeRange && searchParams.awardTimeRange.length === 2) {
      params.startTime = searchParams.awardTimeRange[0]
      params.endTime = searchParams.awardTimeRange[1]
    }

    // 根据用户角色设置学生ID
    if (userInfo.value.role === 'student') {
      params.studentId = userInfo.value.id
    }

    // 如果是导出选中，添加选中记录的ID列表
    if (!isExportAll) {
      const selectedIds = selectedRecords.value.map(record => record.id)
      params.recordIds = selectedIds.join(',')
    }

    // 发送请求
    const response = await axios.get('/api/student-award-records/export', {
      params,
      responseType: 'blob' // 重要！设置响应类型为blob
    })

    // 创建下载链接
    const url = window.URL.createObjectURL(new Blob([response.data]))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', `获奖记录_${formatDate(new Date())}.xlsx`)
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)

    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出获奖记录失败:', error)
    ElMessage.error('导出获奖记录失败')
  }
}

// 分页大小变化
const handleSizeChange = (size) => {
  pagination.pageSize = size
  pagination.currentPage = 1
  getAwardRecords()
}

// 当前页码变化
const handleCurrentChange = (currentPage) => {
  pagination.currentPage = currentPage
  getAwardRecords()
}

// 初始化
onMounted(() => {
  getAwards()
  getAwardTypes()
  getAwardRecords()
})
</script>

<style scoped>
.student-award-record-container {
  padding: 20px 0;
}

.page-header {
  margin-bottom: 20px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.search-card {
  margin-bottom: 20px;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  align-items: center;
}

/* 为搜索表单中的按钮添加间距 */
.search-form .el-form-item .el-button {
  margin-right: 10px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

/* 确保最后一个按钮没有右边距 */
.search-form .el-form-item .el-button:last-child {
  margin-right: 0;
}

/* 为导出Excel的下拉按钮添加间距 */
.search-form .el-form-item .el-dropdown {
  margin-right: 10px;
}

/* 确保按钮文字居中 */
.search-form .el-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.table-card {
  margin-bottom: 20px;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>

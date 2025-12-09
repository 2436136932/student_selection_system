<template>
  <div class="application-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>学生奖项申请</span>
          <el-button v-if="hasRole('student')" type="primary" @click="dialogVisible = true">
            <el-icon><plus /></el-icon> 申请奖项
          </el-button>
        </div>
      </template>
      <div class="card-body">
        <div class="search-form">
          <el-form :model="searchForm" :inline="true">
            <el-form-item label="奖项名称">
              <el-input v-model="searchForm.awardName" placeholder="请输入奖项名称"></el-input>
            </el-form-item>
            <el-form-item label="申请状态">
              <el-select v-model="searchForm.status" placeholder="请选择申请状态">
                <el-option label="全部" value=""></el-option>
                <el-option label="待审核" value="0"></el-option>
                <el-option label="通过" value="1"></el-option>
                <el-option label="未通过" value="2"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">搜索</el-button>
              <el-button @click="resetForm">重置</el-button>
            </el-form-item>
          </el-form>
        </div>

        <el-table :data="applications" style="width: 100%" stripe v-loading="loading">
          <el-table-column prop="id" label="申请ID" width="100"></el-table-column>
          <el-table-column prop="studentId" label="学生学号" width="120"></el-table-column>
          <el-table-column prop="award.awardName" label="奖项名称" width="180">
            <template #default="scope">{{ scope.row.award?.awardName || '未知奖项' }}</template>
          </el-table-column>
          <el-table-column prop="award.awardLevel" label="奖项级别" width="120">
            <template #default="scope">{{ scope.row.award?.awardLevel || '未知级别' }}</template>
          </el-table-column>
          <el-table-column prop="applicationTime" label="申请时间" width="180" formatter="formatDate"></el-table-column>
          <el-table-column prop="status" label="申请状态" width="180">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row)">
                {{ getStatusText(scope.row) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" fixed="right" v-if="hasRole('student')">
            <template #default="scope">
              <el-button 
                v-if="scope.row.status === 0" 
                size="small" 
                type="danger" 
                @click="handleCancelApplication(scope.row)"
              >
                <el-icon><delete /></el-icon> 取消申请
              </el-button>
              <el-button 
                v-else 
                size="small" 
                disabled
              >
                已处理
              </el-button>
            </template>
          </el-table-column>
          <el-table-column label="审核操作" width="150" fixed="right" v-if="hasRole('admin') || hasRole('teacher')">
            <template #default="scope">
              <el-button 
                v-if="scope.row.status === 0" 
                size="small" 
                type="success" 
                @click="handleApproveApplication(scope.row)"
              >
                <el-icon><check /></el-icon> 通过
              </el-button>
              <el-button 
                v-if="scope.row.status === 0" 
                size="small" 
                type="danger" 
                @click="handleRejectApplication(scope.row)"
              >
                <el-icon><close /></el-icon> 拒绝
              </el-button>
              <el-button 
                v-else 
                size="small" 
                disabled
              >
                已处理
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
      </div>
    </el-card>

    <!-- 新增申请对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="申请奖项"
      width="500px"
    >
      <el-form :model="form" label-width="100px">
        <el-form-item label="奖项名称" prop="awardId">
          <el-select v-model="form.awardId" placeholder="请选择奖项" style="width: 100%">
            <el-option 
              v-for="award in awards" 
              :key="award.awardId" 
              :label="award.awardName" 
              :value="award.awardId"
            >
              <div>
                <span>{{ award.awardName }}</span>
                <span style="margin-left: 10px; color: #909399;">{{ award.awardLevel }}</span>
              </div>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="学生学号" prop="studentId">
          <el-input v-model="form.studentId" placeholder="请输入学生学号" readonly :disabled="true"></el-input>
        </el-form-item>
        <el-form-item label="申请理由" prop="description">
          <el-input v-model="form.description" type="textarea" placeholder="请输入申请理由" rows="4"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Edit, Delete, Plus, Check, Close } from '@element-plus/icons-vue'
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

// 计算当前用户是否为学生
const isStudent = computed(() => {
  return hasRole('student')
})

const applications = ref([])
const awards = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const dialogVisible = ref(false)
const loading = ref(false)

const searchForm = reactive({
  awardName: '',
  status: ''
})

const form = reactive({
  id: '',
  studentId: getUserInfo().id || '',
  awardId: '',
  description: ''
})

// 格式化日期
const formatDate = (row, column, cellValue) => {
  if (cellValue) {
    const date = new Date(cellValue)
    return date.toLocaleString()
  }
  return ''
}

// 获取状态文本
const getStatusText = (row) => {
  // 支持两级审批流程
  if (row.status === 0) return '待教师审核'
  if (row.status === 1) return '教师审核通过，待管理员审批'
  if (row.status === 2) return '教师审核拒绝'
  if (row.status === 3) return '管理员审批通过'
  if (row.status === 4) return '管理员审批拒绝'
  return '未知状态'
}

// 获取状态标签类型
const getStatusType = (row) => {
  // 支持两级审批流程
  if (row.status === 0) return 'warning'
  if (row.status === 1) return 'info'
  if (row.status === 2) return 'danger'
  if (row.status === 3) return 'success'
  if (row.status === 4) return 'danger'
  return 'info'
}

// 获取申请列表
const getApplications = () => {
  loading.value = true
  let apiPath = '/api/student-award-applications'
  
  // 如果是学生角色，只获取当前学生的申请
  if (isStudent.value) {
    apiPath += `/student/${getUserInfo().id}`
  } else {
    apiPath += '/page'
  }
  
  axios.get(apiPath, {
    params: {
      page: isStudent.value ? undefined : currentPage.value,
      size: isStudent.value ? undefined : pageSize.value,
      awardName: searchForm.awardName,
      status: searchForm.status
    }
  }).then(response => {
    if (isStudent.value) {
      applications.value = response.data
      total.value = response.data.length
    } else {
      applications.value = response.data.records
      total.value = response.data.total
    }
    loading.value = false
  }).catch(error => {
    ElMessage.error('获取申请列表失败')
    console.error('Error fetching applications:', error)
    loading.value = false
  })
}

// 获取奖项列表
const getAwards = () => {
  axios.get('/api/awards').then(response => {
    awards.value = response.data.content || response.data
  }).catch(error => {
    ElMessage.error('获取奖项列表失败')
    console.error('Error fetching awards:', error)
  })
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  getApplications()
}

// 重置表单
const resetForm = () => {
  searchForm.awardName = ''
  searchForm.status = ''
  currentPage.value = 1
  getApplications()
}

// 分页处理
const handleSizeChange = (size) => {
  pageSize.value = size
  getApplications()
}

const handleCurrentChange = (current) => {
  currentPage.value = current
  getApplications()
}

// 提交申请
const handleSubmit = () => {
  // 检查是否已选择奖项
  if (!form.awardId) {
    ElMessage.warning('请选择奖项')
    return
  }
  
  // 检查是否已输入申请理由
  if (!form.description) {
    ElMessage.warning('请输入申请理由')
    return
  }
  
  // 检查是否已申请该奖项
  checkApplicationExists().then(exists => {
    if (exists) {
      ElMessage.warning('您已申请该奖项，请勿重复申请')
      return
    }
    
    // 提交申请
    axios.post('/api/student-award-applications', {
      studentId: form.studentId,
      awardId: form.awardId
    }).then(response => {
      ElMessage.success('申请奖项成功')
      dialogVisible.value = false
      getApplications()
      // 重置表单
      form.awardId = ''
      form.description = ''
    }).catch(error => {
      ElMessage.error('申请奖项失败')
      console.error('Error submitting application:', error)
    })
  })
}

// 检查是否已申请该奖项
const checkApplicationExists = () => {
  return axios.get('/api/student-award-applications/check-exists', {
    params: {
      studentId: form.studentId,
      awardId: form.awardId
    }
  }).then(response => {
    return response.data
  }).catch(error => {
    console.error('Error checking application existence:', error)
    return false
  })
}

// 取消申请
const handleCancelApplication = (row) => {
  ElMessageBox.confirm(`确定要取消该奖项申请吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    axios.delete(`/api/student-award-applications/${row.id}`)
      .then(response => {
        ElMessage.success('取消申请成功')
        getApplications()
      })
      .catch(error => {
        ElMessage.error('取消申请失败')
        console.error('Error canceling application:', error)
      })
  }).catch(() => {
    // 取消操作
  })
}

// 审核通过
const handleApproveApplication = (row) => {
  // 根据用户角色和当前状态决定新状态
  let confirmMessage = '';
  let successMessage = '';
  let apiEndpoint = '';
  
  if (hasRole('teacher')) {
    // 教师审核通过，状态变为待管理员审批
    confirmMessage = '确定要通过该奖项申请吗？通过后将提交给管理员进行二次审批。';
    successMessage = '教师审核通过成功';
    apiEndpoint = `/api/student-award-applications/${row.id}/teacher-approve`;
  } else if (hasRole('admin')) {
    // 管理员审核通过，状态变为最终通过
    confirmMessage = '确定要通过该奖项申请吗？';
    successMessage = '管理员审批通过成功';
    apiEndpoint = `/api/student-award-applications/${row.id}/admin-approve`;
  } else {
    ElMessage.error('您没有权限进行此操作');
    return;
  }
  
  // 显示评论输入框
  ElMessageBox.prompt(confirmMessage, '审核通过', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputType: 'textarea',
    inputPlaceholder: '请输入审核意见（可选）',
    inputValue: '',
    type: 'success'
  }).then(({ value }) => {
    const comments = value || '';
    let status = 1; // 通过状态
    
    axios.put(apiEndpoint, null, {
      params: {
        status: status,
        comments: comments
      }
    }).then(response => {
      ElMessage.success(successMessage)
      getApplications()
    }).catch(error => {
      ElMessage.error('审核通过失败')
      console.error('Error approving application:', error)
    })
  }).catch(() => {
    // 取消操作
  })
}

// 审核拒绝
const handleRejectApplication = (row) => {
  // 根据用户角色和当前状态决定新状态
  let confirmMessage = '';
  let successMessage = '';
  let apiEndpoint = '';
  
  if (hasRole('teacher')) {
    // 教师直接拒绝，状态变为教师拒绝
    confirmMessage = '确定要拒绝该奖项申请吗？';
    successMessage = '教师审核拒绝成功';
    apiEndpoint = `/api/student-award-applications/${row.id}/teacher-approve`;
  } else if (hasRole('admin')) {
    // 管理员拒绝，状态变为管理员拒绝
    confirmMessage = '确定要拒绝该奖项申请吗？';
    successMessage = '管理员审批拒绝成功';
    apiEndpoint = `/api/student-award-applications/${row.id}/admin-approve`;
  } else {
    ElMessage.error('您没有权限进行此操作');
    return;
  }
  
  // 显示评论输入框
  ElMessageBox.prompt(confirmMessage, '审核拒绝', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputType: 'textarea',
    inputPlaceholder: '请输入拒绝理由',
    inputValue: '',
    type: 'danger'
  }).then(({ value }) => {
    const comments = value || '';
    let status = 2; // 拒绝状态
    
    axios.put(apiEndpoint, null, {
      params: {
        status: status,
        comments: comments
      }
    }).then(response => {
      ElMessage.success(successMessage)
      getApplications()
    }).catch(error => {
      ElMessage.error('审核拒绝失败')
      console.error('Error rejecting application:', error)
    })
  }).catch(() => {
    // 取消操作
  })
}

// 初始化数据
onMounted(() => {
  getApplications()
  if (isStudent.value) {
    getAwards()
  }
})
</script>

<style scoped>
.application-container {
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

.pagination {
  margin-top: 20px;
  text-align: right;
}

@media (max-width: 768px) {
  .application-container {
    padding: 10px;
  }
  
  .search-form {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>

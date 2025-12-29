<template>
  <div class="application-container">
    <h2>学生奖项申请</h2>
    <p class="note" @click="goToChatCenter" style="cursor: pointer; color: #3498db;">如对结果存疑，可在聊天中心咨询相关人员</p>
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
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
                <el-option label="待教师审批" value="0"></el-option>
                <el-option label="待管理员审批" value="1"></el-option>
                <el-option label="教师已拒绝" value="2"></el-option>
                <el-option label="管理员已通过" value="3"></el-option>
                <el-option label="管理员已拒绝" value="4"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">搜索</el-button>
              <el-button @click="resetForm">重置</el-button>
            </el-form-item>
          </el-form>
        </div>

        <el-table :data="applications" style="width: 100%" stripe v-loading="loading">
          <el-table-column prop="id" label="申请ID" width="80"></el-table-column>
          <el-table-column prop="student.name" label="学生姓名" width="120">
            <template #default="scope">{{ scope.row.student?.name || '-' }}</template>
          </el-table-column>
          <el-table-column prop="student.studentNumber" label="学号" width="150">
            <template #default="scope">{{ scope.row.student?.studentNumber || '-' }}</template>
          </el-table-column>
          <el-table-column prop="student.major" label="专业" width="150">
            <template #default="scope">{{ scope.row.student?.major || '-' }}</template>
          </el-table-column>
          <el-table-column prop="student.className" label="班级" width="120">
            <template #default="scope">{{ scope.row.student?.className || '-' }}</template>
          </el-table-column>
          <el-table-column prop="award.awardName" label="奖项名称" width="220">
            <template #default="scope">{{ scope.row.award?.awardName || '-' }}</template>
          </el-table-column>
          <el-table-column prop="award.awardLevel" label="奖项级别" width="100">
            <template #default="scope">{{ scope.row.award?.awardLevel || '-' }}</template>
          </el-table-column>
          <el-table-column prop="applicationTime" label="申请日期" width="150">
            <template #default="scope">{{ scope.row.applicationTime ? new Date(scope.row.applicationTime).toLocaleString() : '-' }}</template>
          </el-table-column>
          <el-table-column prop="description" label="申请理由" min-width="200" :show-overflow-tooltip="true">
            <template #default="scope">
              {{ scope.row.description || '-' }}
            </template>
          </el-table-column>
          <el-table-column prop="approvalStatus" label="审批状态" width="120">
            <template #default="scope">
              <el-tag
                :type="{
                  '待教师审批': 'info',
                  '教师已通过': 'primary',
                  '教师已拒绝': 'danger',
                  '待管理员审批': 'warning',
                  '管理员已通过': 'success',
                  '管理员已拒绝': 'danger'
                }[scope.row.approvalStatus || '待教师审批']"
              >
                {{ scope.row.approvalStatus || '待教师审批' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="220" fixed="right" v-if="hasRole('student')">
            <template #default="scope">
              <!-- 调试信息 -->
              <div v-if="false" style="color: red; font-size: 10px;">
                materialPath: {{ scope.row.materialPath }}
                <br>
                materialName: {{ scope.row.materialName }}
              </div>
              <!-- 预览材料按钮 -->
              <el-button 
                size="small" 
                type="primary" 
                @click="previewMaterial(scope.row)"
              >
                <el-icon><Picture /></el-icon> 预览材料
              </el-button>
              <!-- 下载材料按钮 -->
              <el-button 
                size="small" 
                type="success" 
                @click="downloadMaterial(scope.row)"
              >
                <el-icon><Download /></el-icon> 下载材料
              </el-button>
              <!-- 取消申请按钮 -->
              <el-button 
                v-if="scope.row.status === 0" 
                size="small" 
                type="danger" 
                @click="handleCancelApplication(scope.row)"
              >
                <el-icon><delete /></el-icon> 取消申请
              </el-button>
              <!-- 已处理按钮 -->
              <el-button 
                v-else 
                size="small" 
                disabled
              >
                已处理
              </el-button>
            </template>
          </el-table-column>
          <el-table-column label="审核操作" width="250" fixed="right" v-if="hasRole('admin') || hasRole('teacher')">
            <template #default="scope">
              <!-- 调试信息 -->
              <div v-if="false" style="color: red; font-size: 10px;">
                materialPath: {{ scope.row.materialPath }}
                <br>
                materialName: {{ scope.row.materialName }}
              </div>
              <!-- 预览申请材料 -->
              <el-button 
                size="small" 
                type="primary" 
                @click="previewMaterial(scope.row)"
              >
                <el-icon><Picture /></el-icon> 预览材料
              </el-button>
              <!-- 下载申请材料 -->
              <el-button 
                size="small" 
                type="success" 
                @click="downloadMaterial(scope.row)"
              >
                <el-icon><Download /></el-icon> 下载材料
              </el-button>
              <!-- 教师只能在状态为0（待教师审批）时看到审批按钮 -->
              <!-- 管理员可以在状态为1（待管理员审批）时看到审批按钮 -->
              <el-button 
                v-if="(hasRole('teacher') && scope.row.status === 0) || (hasRole('admin') && scope.row.status === 1)" 
                size="small" 
                type="success" 
                @click="handleApproveApplication(scope.row)"
              >
                <el-icon><check /></el-icon> 通过
              </el-button>
              <el-button 
                v-if="(hasRole('teacher') && scope.row.status === 0) || (hasRole('admin') && scope.row.status === 1)" 
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
        <el-form-item label="学生学号" prop="studentNumber">
          <el-input v-model="form.studentNumber" placeholder="请输入学生学号" readonly :disabled="true"></el-input>
        </el-form-item>
        <el-form-item label="申请理由" prop="description">
          <el-input v-model="form.description" type="textarea" placeholder="请输入申请理由" rows="4"></el-input>
        </el-form-item>
        <el-form-item label="申请材料" prop="material">
          <div class="custom-upload-container">
            <!-- 使用Element Plus Upload组件 -->
            <el-upload
              ref="uploadRef"
              class="custom-upload"
              :auto-upload="false"
              :before-upload="beforeUpload"
              :http-request="customUpload"
              :file-list="fileList"
              :limit="1"
              :on-exceed="handleExceed"
              :on-change="handleFileChange"
              accept=".doc,.docx,.pdf,.ppt,.pptx,.png,.jpg,.jpeg"
            >
              <template #trigger>
                <el-button type="primary" :disabled="uploading">
                  <el-icon><Upload /></el-icon> {{ fileList.length > 0 ? fileList[0].name : '选择文件' }}
                </el-button>
              </template>
              
              <!-- 文件列表显示 -->
              <template #file-list="{ files }">
                <div v-if="files.length > 0" class="selected-file-info" style="margin-top: 10px;">
                  <span style="color: #606266;">{{ files[0].name }}</span>
                  <el-button 
                    size="small" 
                    type="danger" 
                    plain 
                    @click="clearFileSelect"
                    style="margin-left: 10px;"
                  >
                    移除
                  </el-button>
                </div>
              </template>
              
              <!-- 上传提示 -->
              <template #tip>
                <div class="el-upload__tip">
                  支持上传 Word、PDF、PPT、PNG、JPG 格式的文件，最大10MB
                </div>
              </template>
            </el-upload>
            
            <!-- 显示上传进度条 -->
            <el-progress 
              v-if="uploadProgress > 0 && uploadProgress < 100" 
              :percentage="uploadProgress" 
              :stroke-width="2" 
              style="margin-top: 10px;"
            />
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 材料预览组件 -->
    <MaterialPreview
      v-model:visible="materialPreviewVisible"
      :url="previewUrl"
      :download-url="downloadUrl"
      :file-name="currentMaterialName"
      :file-size="currentMaterialSize"
      @close="handlePreviewClose"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Edit, Delete, Plus, Check, Close, Upload, Download, Picture } from '@element-plus/icons-vue'
import axios from 'axios'
// 导入材料预览组件
import MaterialPreview from '../components/MaterialPreview.vue'

// 初始化路由
const router = useRouter()

// 获取用户信息
const getUserInfo = () => {
  const userInfoStr = localStorage.getItem('userInfo')
  return userInfoStr ? JSON.parse(userInfoStr) : {}
}

// 检查用户是否有指定角色
const hasRole = (role) => {
  const userInfo = getUserInfo()
  const userRole = userInfo.role || ''
  // 处理角色名称，支持"student"或"ROLE_STUDENT"格式
  return userRole.toLowerCase() === role.toLowerCase() || userRole.toLowerCase() === `role_${role.toLowerCase()}`
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
// 文件上传相关
const uploadRef = ref(null)
const uploadUrl = '/api/student-award-applications/upload'
const selectedFile = ref(null)
const uploading = ref(false)
const materialInfo = reactive({
  materialPath: '',
  materialName: '',
  materialSize: 0,
  materialType: ''
})
// 添加上传进度显示
const uploadProgress = ref(0)
// Element Plus Upload组件相关
const fileList = ref([])

// 材料预览相关
const materialPreviewVisible = ref(false)
const previewUrl = ref('')
const downloadUrl = ref('')
const currentMaterialName = ref('')
const currentMaterialSize = ref(0)

const searchForm = reactive({
  awardName: '',
  status: ''
})

const form = reactive({
  id: '',
  studentNumber: getUserInfo().studentNumber || getUserInfo().id || '',
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

// 验证文件类型和大小
const validateFile = (file) => {
  const allowedTypes = ['.doc', '.docx', '.pdf', '.ppt', '.pptx', '.png', '.jpg', '.jpeg']
  const fileExtension = `.${file.name.split('.').pop().toLowerCase()}`
  const isTypeAllowed = allowedTypes.includes(fileExtension)
  const isLt10M = file.size / 1024 / 1024 < 10

  if (!isTypeAllowed) {
    ElMessage.error('只允许上传 Word、PDF、PPT、PNG、JPG 格式的文件')
    return false
  }
  if (!isLt10M) {
    ElMessage.error('上传文件大小不能超过 10MB')
    return false
  }
  return true
}

// Element Plus Upload组件钩子函数
// 上传前验证
const beforeUpload = (file) => {
  return validateFile(file)
}

// 文件选择变化处理
const handleFileChange = (file, newFileList) => {
  fileList.value = newFileList
  if (newFileList.length > 0) {
    selectedFile.value = newFileList[0].raw
  } else {
    selectedFile.value = null
  }
  
  // 重置材料信息
  materialInfo.materialPath = ''
  materialInfo.materialName = ''
  materialInfo.materialSize = 0
  materialInfo.materialType = ''
}

// 文件超出限制处理
const handleExceed = () => {
  ElMessage.error('只能上传一个文件')
}

// 自定义上传方法
const customUpload = (options) => {
  return new Promise((resolve, reject) => {
    console.log('Starting custom file upload with file:', options.file)
    
    uploading.value = true
    uploadProgress.value = 0
    
    // 创建FormData对象
    const formData = new FormData()
    formData.append('file', options.file)
    
    // 使用axios直接上传文件
    axios.post(uploadUrl, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      },
      // 添加上传进度监听
      onUploadProgress: (progressEvent) => {
        if (progressEvent.total) {
          const percent = Math.round((progressEvent.loaded * 100) / progressEvent.total)
          uploadProgress.value = percent
          console.log(`文件上传进度: ${percent}%`)
        }
      }
    }).then(response => {
      console.log('File upload response:', response)
      
      // 检查响应数据
      const result = response.data
      if (result.success) {
        // 保存材料信息到materialInfo对象
        materialInfo.materialPath = result.materialPath || ''
        materialInfo.materialName = result.materialName || ''
        materialInfo.materialSize = result.materialSize || 0
        materialInfo.materialType = result.materialType || ''
        
        console.log('Material info updated:', materialInfo)
        
        ElMessage.success('文件上传成功')
        resolve(result)
      } else {
        const errorMsg = result.message || '文件上传失败'
        console.error('File upload failed:', errorMsg)
        ElMessage.error(errorMsg)
        reject(new Error(errorMsg))
      }
    }).catch(error => {
      console.error('Error during file upload:', error)
      let errorMsg = '文件上传失败，请重试'
      if (error.response && error.response.data && error.response.data.message) {
        errorMsg = error.response.data.message
      }
      ElMessage.error(errorMsg)
      reject(error)
    }).finally(() => {
      uploading.value = false
      // 延迟重置进度，让用户看到完成状态
      setTimeout(() => {
        uploadProgress.value = 0
      }, 500)
    })
  })
}

// 清除已选择的文件
const clearFileSelect = () => {
  selectedFile.value = null
  fileList.value = []
  uploadProgress.value = 0
  
  // 重置材料信息
  materialInfo.materialPath = ''
  materialInfo.materialName = ''
  materialInfo.materialSize = 0
  materialInfo.materialType = ''
}

// 获取状态文本
const getStatusText = (row) => {
  // 支持两级审批流程
  if (row.status === 0) return '待教师审批'
  if (row.status === 1) return '待管理员审批'
  if (row.status === 2) return '教师已拒绝'
  if (row.status === 3) return '管理员已通过'
  if (row.status === 4) return '管理员已拒绝'
  return '未知状态'
}

// 获取状态标签类型
const getStatusType = (row) => {
  // 支持两级审批流程
  if (row.status === 0) return 'info'
  if (row.status === 1) return 'warning'
  if (row.status === 2) return 'danger'
  if (row.status === 3) return 'success'
  if (row.status === 4) return 'danger'
  return 'info'
}

// 获取申请列表
const getApplications = () => {
  loading.value = true
  let apiPath = '/api/student-award-applications/page/search'
  
  const params = {
    pageNum: currentPage.value,
    pageSize: pageSize.value,
    awardName: searchForm.awardName
  }
  
  // 如果是学生角色，只获取当前学生的申请
  if (isStudent.value) {
    // 使用学号获取申请列表
    const userInfo = getUserInfo()
    if (userInfo.studentNumber) {
      params.studentNumber = userInfo.studentNumber
    }
  }
  
  // 处理状态参数
  if (searchForm.status) {
    params.status = searchForm.status
  }
  
  axios.get(apiPath, {
    params
  }).then(response => {
    let data = response.data.records
    
    // 打印返回的数据结构，用于调试
    console.log('Raw response data:', response.data)
    console.log('Application records:', data)
    
    // 将后端返回的数字状态转换为字符串状态，与AwardView保持一致
    data = data.map(app => {
      // 打印每个申请的详细信息，特别是award字段
      console.log('Application details:', app)
      console.log('Award details in application:', app.award)
      let approvalStatus = '待教师审批'
      if (app.status === 1) {
        approvalStatus = '待管理员审批'
      } else if (app.status === 2) {
        approvalStatus = '教师已拒绝'
      } else if (app.status === 3) {
        approvalStatus = '管理员已通过'
      } else if (app.status === 4) {
        approvalStatus = '管理员已拒绝'
      }
      return {
        ...app,
        approvalStatus,
        applicationDate: app.applicationTime
      }
    })
    
    if (isStudent.value) {
      applications.value = data
      total.value = data.length
    } else {
      applications.value = data
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
    // 后端返回的是分页对象，需要获取records数组
    awards.value = response.data.records || response.data.content || response.data
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
    
    // 先上传文件（如果有选择文件）
    const uploadFilePromise = selectedFile.value ? uploadFile() : Promise.resolve()
    
    uploadFilePromise.then(() => {
      // 打印材料信息，确保它们被正确设置
      console.log('Current materialInfo before submission:', materialInfo)
      
      // 严格验证材料信息完整性，特别是材料路径
      if (selectedFile.value) {
        if (!materialInfo.materialPath || materialInfo.materialPath === '') {
          console.error('Material path is empty after upload, cannot submit application')
          ElMessage.error('材料上传失败，请重新选择文件并上传')
          return
        }
        if (!materialInfo.materialName || materialInfo.materialName === '') {
          console.error('Material name is empty after upload, cannot submit application')
          ElMessage.error('材料上传失败，请重新选择文件并上传')
          return
        }
      }
      
      // 准备提交的申请数据
      const applicationData = {
        awardId: form.awardId,
        description: form.description,
        // 包含材料信息，确保每个字段都有值
        materialPath: materialInfo.materialPath || '',
        materialName: materialInfo.materialName || '',
        materialSize: materialInfo.materialSize || 0,
        materialType: materialInfo.materialType || ''
      }
      
      console.log('Submitting application with complete data:', applicationData)
      
      // 提交申请
      axios.post('/api/student-award-applications', applicationData).then(response => {
        console.log('Application submission successful:', response.data)
        ElMessage.success('申请奖项成功')
        dialogVisible.value = false
        
        // 刷新申请列表，查看最新数据
        getApplications()
        
        // 重置表单
        resetApplicationForm()
      }).catch(error => {
        console.error('Error submitting application:', error)
        console.error('Error details:', {
          status: error.response?.status,
          statusText: error.response?.statusText,
          data: error.response?.data,
          config: error.config
        })
        // 如果后端返回了具体的错误信息，使用后端返回的信息，否则使用默认信息
        const errorMessage = error.response?.data?.message || '申请奖项失败'
        ElMessage.error(errorMessage)
      })
    }).catch(error => {
      console.error('File upload failed:', error)
      ElMessage.error('文件上传失败，请重试')
    })
  })
}

// 上传文件方法（调用Element Plus Upload组件的自定义上传功能）
const uploadFile = () => {
  return new Promise((resolve, reject) => {
    console.log('Starting file upload with selected file:', selectedFile.value)
    
    // 如果没有选择文件，直接resolve
    if (!selectedFile.value || fileList.value.length === 0) {
      console.log('No file selected, resolving...')
      resolve()
      return
    }
    
    try {
      // 使用自定义上传方法
      customUpload({ file: selectedFile.value })
        .then(result => {
          resolve(result)
        })
        .catch(error => {
          reject(error)
        })
    } catch (error) {
      console.error('Unexpected error during file upload:', error)
      uploading.value = false
      uploadProgress.value = 0
      ElMessage.error('文件上传失败，请重试')
      reject(error)
    }
  })
}

// 重置申请表单
const resetApplicationForm = () => {
  console.log('Resetting application form...')
  
  // 重置表单字段
  form.awardId = ''
  form.description = ''
  
  // 重置文件选择状态
  selectedFile.value = null
  fileList.value = []
  uploading.value = false
  uploadProgress.value = 0
  
  // 重置材料信息
  materialInfo.materialPath = ''
  materialInfo.materialName = ''
  materialInfo.materialSize = 0
  materialInfo.materialType = ''
  
  console.log('Form reset completed, materialInfo:', materialInfo)
}

// 检查是否已申请该奖项
const checkApplicationExists = () => {
  return axios.get('/api/student-award-applications/check-exists', {
    params: {
      studentNumber: form.studentNumber,  // 这里form.studentNumber是学号
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

// 处理预览对话框关闭事件
const handlePreviewClose = () => {
  // 重置预览状态
  materialPreviewVisible.value = false
  previewUrl.value = ''
  downloadUrl.value = ''
  currentMaterialName.value = ''
  currentMaterialSize.value = 0
}

// 预览申请材料
const previewMaterial = (row) => {
  if (!row.id) {
    ElMessage.error('申请ID不存在，无法预览材料')
    return
  }
  
  if (!row.materialPath) {
    ElMessage.error('该申请未上传材料')
    return
  }
  
  try {
    // 生成完整的后端URL
    const baseUrl = 'http://localhost:8080'
    const previewPath = `/api/student-award-applications/preview/${row.id}`
    const downloadPath = `/api/student-award-applications/download/${row.id}`
    
    // 使用完整的后端URL
    const fullPreviewUrl = `${baseUrl}${previewPath}`
    const fullDownloadUrl = `${baseUrl}${downloadPath}`
    
    // 添加调试日志，查看生成的URL
    console.log('生成的预览URL:', fullPreviewUrl)
    console.log('生成的下载URL:', fullDownloadUrl)
    
    previewUrl.value = fullPreviewUrl
    downloadUrl.value = fullDownloadUrl
    currentMaterialName.value = row.materialName || 'application_material'
    currentMaterialSize.value = row.materialSize || 0
    
    // 显示预览对话框
    materialPreviewVisible.value = true
  } catch (error) {
    console.error('Error previewing material:', error)
    ElMessage.error('文件预览失败，请重试')
  }
}

// 下载申请材料
const downloadMaterial = async (row) => {
  if (!row.id) {
    ElMessage.error('申请ID不存在，无法下载材料')
    return
  }
  
  try {
    // 生成完整的下载URL
    const baseUrl = 'http://localhost:8080'
    const downloadUrl = `${baseUrl}/api/student-award-applications/download/${row.id}`
    
    // 使用axios获取文件的blob数据，确保携带JWT令牌
    const response = await axios.get(downloadUrl, {
      responseType: 'blob',
      headers: {
        Authorization: localStorage.getItem('token') ? `Bearer ${localStorage.getItem('token')}` : ''
      }
    })
    
    // 创建临时的blob URL
    const blob = new Blob([response.data], { type: response.headers['content-type'] })
    const blobUrl = URL.createObjectURL(blob)
    
    // 使用a标签下载文件
    const link = document.createElement('a')
    link.href = blobUrl
    link.download = row.materialName || 'application_material'
    link.target = '_blank'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    
    // 清理blob URL
    setTimeout(() => {
      URL.revokeObjectURL(blobUrl)
    }, 1000)
    
    ElMessage.success('文件下载已开始')
  } catch (error) {
    console.error('Error downloading material:', error)
    console.error('错误详情:', {
      status: error.response?.status,
      statusText: error.response?.statusText,
      data: error.response?.data,
      config: error.response?.config
    })
    ElMessage.error(`文件下载失败: ${error.response?.status || '未知错误'} - ${error.response?.statusText || '请求失败'}`)
  }
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

// 跳转到聊天中心
const goToChatCenter = () => {
  router.push('/chat')
}

// 初始化数据
onMounted(() => {
  // 检查是否有从奖项管理页面传递过来的奖项ID
  const currentAwardId = localStorage.getItem('currentAwardId')
  const currentAwardName = localStorage.getItem('currentAwardName')
  if (currentAwardId) {
    // 如果有，自动填充奖项名称并执行搜索
    searchForm.awardName = currentAwardName
    handleSearch()
    // 清除localStorage中的数据，避免下次页面加载时自动过滤
    localStorage.removeItem('currentAwardId')
    localStorage.removeItem('currentAwardName')
  } else {
    getApplications()
  }
  if (isStudent.value) {
    getAwards()
  }
})
</script>

<style scoped>
.application-container {
  padding: 20px;
}

.note {
  font-size: 13px;
  color: #606266;
  margin-top: 5px;
  margin-bottom: 15px;
  padding: 8px 12px;
  background: linear-gradient(135deg, rgba(52, 152, 219, 0.1) 0%, rgba(41, 128, 185, 0.1) 100%);
  border-radius: 8px;
  border-left: 3px solid #3498db;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
  padding: 16px;
  background: linear-gradient(135deg, #f8f9fa 0%, #ecf0f1 100%);
  border-radius: 12px;
  border: 1px solid rgba(52, 152, 219, 0.15);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

/* 表格样式优化 */
.el-table {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.el-table:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.el-table__header-wrapper th {
  background: linear-gradient(135deg, #3498db 0%, #2980b9 100%);
  color: white;
  font-weight: 600;
  font-size: 14px;
  padding: 16px;
  border-bottom: none;
}

.el-table__body-wrapper tr {
  transition: background-color 0.2s ease;
}

.el-table__body-wrapper tr:hover > td {
  background: linear-gradient(135deg, rgba(52, 152, 219, 0.08) 0%, rgba(52, 152, 219, 0.12) 100%);
}

.el-table__body-wrapper td {
  padding: 14px 16px;
  border-bottom: 1px solid #f0f0f0;
  font-size: 14px;
  color: #2c3e50;
}

/* 状态标签优化 */
.el-tag {
  font-weight: 500;
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 12px;
  transition: all 0.3s ease;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.el-tag:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

.el-tag--info {
  background: linear-gradient(135deg, #909399 0%, #606266 100%);
  border: none;
  color: white;
}

.el-tag--success {
  background: linear-gradient(135deg, #27ae60 0%, #2ecc71 100%);
  border: none;
  color: white;
}

.el-tag--warning {
  background: linear-gradient(135deg, #f39c12 0%, #f1c40f 100%);
  border: none;
  color: white;
}

.el-tag--danger {
  background: linear-gradient(135deg, #e74c3c 0%, #c0392b 100%);
  border: none;
  color: white;
}

.el-tag--primary {
  background: linear-gradient(135deg, #3498db 0%, #2980b9 100%);
  border: none;
  color: white;
}

/* 分页器优化 */
.pagination {
  margin-top: 20px;
  text-align: right;
}

.el-pagination {
  display: flex;
  align-items: center;
  gap: 8px;
}

.el-pagination button {
  border-radius: 8px;
  transition: all 0.3s ease;
  font-weight: 500;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.el-pagination button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  background: linear-gradient(135deg, #3498db 0%, #2980b9 100%);
  color: white;
  border-color: #3498db;
}

.el-pagination button.is-active {
  background: linear-gradient(135deg, #3498db 0%, #2980b9 100%);
  color: white;
  border-color: #3498db;
  box-shadow: 0 4px 12px rgba(52, 152, 219, 0.3);
}

.el-pagination .el-input__wrapper {
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

@media (max-width: 768px) {
  .application-container {
    padding: 10px;
  }
  
  .search-form {
    padding: 12px;
  }
  
  .el-table {
    font-size: 12px;
  }
  
  .el-table__header-wrapper th {
    padding: 12px;
    font-size: 12px;
  }
  
  .el-table__body-wrapper td {
    padding: 10px 12px;
    font-size: 12px;
  }
}
</style>

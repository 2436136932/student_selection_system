<template>
  <div class="notice-management">
    <div class="page-header">
      <h1>通知管理</h1>
      <el-button type="primary" @click="handleCreate">
        <el-icon><Plus /></el-icon>
        发布新通知
      </el-button>
    </div>

    <!-- 通知列表 -->
    <el-card shadow="hover" class="notice-card">
      <div class="card-header">
        <div class="search-box">
          <el-input v-model="searchForm.title" placeholder="搜索通知标题" clearable style="width: 300px;">
            <template #prepend>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-select v-model="searchForm.status" placeholder="选择状态" clearable style="width: 150px; margin-left: 10px;">
            <el-option label="已发布" value="1" />
            <el-option label="未发布" value="0" />
          </el-select>
          <el-button type="primary" @click="handleSearch" style="margin-left: 10px;">
            搜索
          </el-button>
          <el-button @click="resetSearch" style="margin-left: 10px;">
            重置
          </el-button>
        </div>
      </div>

      <el-table :data="notices" stripe style="width: 100%" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="通知ID" width="80" />
        <el-table-column prop="title" label="通知标题" width="300" show-overflow-tooltip />
        <el-table-column prop="type" label="通知类型" width="120">
          <template #default="scope">
            <el-tag :type="getNoticeTypeTag(scope.row.type)">
              {{ getNoticeTypeText(scope.row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="通知内容" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
              {{ scope.row.status === 1 ? '已发布' : '未发布' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publisherName" label="发布人" width="120" />
        <el-table-column prop="publishTime" label="发布时间" width="180" />
        <el-table-column prop="updateTime" label="更新时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEdit(scope.row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row.id)">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 通知表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="form.id ? '编辑通知' : '发布新通知'"
      width="600px"
    >
      <el-form ref="formRef" :model="form" label-width="80px" :rules="rules">
        <el-form-item label="通知标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入通知标题" />
        </el-form-item>
        
        <el-form-item label="通知类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择通知类型">
            <el-option label="信息" value="info" />
            <el-option label="警告" value="warning" />
            <el-option label="通知" value="notification" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="通知内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="6"
            placeholder="请输入通知内容"
          />
        </el-form-item>
        
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option label="未发布" value="0" />
            <el-option label="已发布" value="1" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确认</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 批量操作 -->
    <div v-if="selectedNotices.length > 0" class="batch-operation">
      <el-button type="danger" @click="handleBatchDelete">
        <el-icon><Delete /></el-icon>
        批量删除 ({{ selectedNotices.length }})
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Search, Edit, Delete } from '@element-plus/icons-vue'
import axios from 'axios'

// 通知列表数据
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const notices = ref([])
const loading = ref(false)

// 搜索表单
const searchForm = reactive({
  title: '',
  status: ''
})

// 通知表单
const dialogVisible = ref(false)
const formRef = ref()
const form = reactive({
  id: null,
  title: '',
  content: '',
  type: 'info',
  status: 1,
  publisherId: null,
  publisherName: ''
})

// 表单验证规则
const rules = reactive({
  title: [
    { required: true, message: '请输入通知标题', trigger: 'blur' },
    { min: 2, max: 100, message: '标题长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入通知内容', trigger: 'blur' },
    { min: 10, message: '内容长度不能少于 10 个字符', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择通知类型', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
})

// 选中的通知
const selectedNotices = ref([])

// 获取用户信息
const getUserInfo = () => {
  const userInfoStr = localStorage.getItem('userInfo')
  return userInfoStr ? JSON.parse(userInfoStr) : null
}

// 获取通知列表
const getNotices = async () => {
  loading.value = true
  try {
    const response = await axios.get('/api/notices/page', {
      params: {
        page: currentPage.value,
        size: pageSize.value,
        title: searchForm.title,
        status: searchForm.status
      }
    })
    notices.value = response.data.records
    total.value = response.data.total
  } catch (error) {
    ElMessage.error('获取通知列表失败，请重试')
    console.error('获取通知列表错误:', error)
  } finally {
    loading.value = false
  }
}

// 处理搜索
const handleSearch = () => {
  currentPage.value = 1
  getNotices()
}

// 重置搜索
const resetSearch = () => {
  searchForm.title = ''
  searchForm.status = ''
  currentPage.value = 1
  getNotices()
}

// 处理分页大小变化
const handleSizeChange = (size) => {
  pageSize.value = size
  getNotices()
}

// 处理当前页码变化
const handleCurrentChange = (page) => {
  currentPage.value = page
  getNotices()
}

// 发布新通知
const handleCreate = () => {
  resetForm()
  dialogVisible.value = true
}

// 编辑通知
const handleEdit = (row) => {
  form.id = row.id
  form.title = row.title
  form.content = row.content
  form.type = row.type
  form.status = row.status
  dialogVisible.value = true
}

// 重置表单
const resetForm = () => {
  // 先重置表单引用，将字段恢复到初始状态
  if (formRef.value) {
    formRef.value.resetFields()
  }
  
  // 然后手动重置所有字段值
  form.id = null
  form.title = ''
  form.content = ''
  form.type = 'info'
  form.status = 1
  form.publisherId = null
  form.publisherName = ''
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  formRef.value.validate((valid) => {
    if (valid) {
      // 获取当前用户信息
      const userInfo = getUserInfo()
      if (userInfo) {
        form.publisherId = userInfo.id
        form.publisherName = userInfo.name
      }

      // 发送请求
      const isEdit = form.id !== null
      const request = isEdit 
        ? axios.put('/api/notices', form)
        : axios.post('/api/notices', form)

      request.then(response => {
        if (response.data) {
          ElMessage.success(isEdit ? '通知更新成功' : '通知发布成功')
          dialogVisible.value = false
          getNotices()
        } else {
          ElMessage.error(isEdit ? '通知更新失败' : '通知发布失败')
        }
      }).catch(error => {
        console.error('提交通知错误:', error)
        ElMessage.error(isEdit ? '通知更新失败，请重试' : '通知发布失败，请重试')
      })
    }
  })
}

// 删除通知
const handleDelete = async (noticeId) => {
  try {
    const response = await axios.delete(`/api/notices/${noticeId}`)
    if (response.data) {
      ElMessage.success('通知删除成功')
      getNotices()
    } else {
      ElMessage.error('通知删除失败')
    }
  } catch (error) {
    console.error('删除通知错误:', error)
    ElMessage.error('通知删除失败，请重试')
  }
}

// 批量删除
const handleBatchDelete = async () => {
  if (selectedNotices.value.length === 0) {
    ElMessage.warning('请先选择要删除的通知')
    return
  }

  try {
    // 由于后端没有批量删除接口，这里需要逐个删除
    for (const notice of selectedNotices.value) {
      await axios.delete(`/api/notices/${notice.id}`)
    }
    ElMessage.success('批量删除成功')
    getNotices()
    selectedNotices.value = []
  } catch (error) {
    console.error('批量删除错误:', error)
    ElMessage.error('批量删除失败，请重试')
  }
}

// 处理选择变更
const handleSelectionChange = (selection) => {
  selectedNotices.value = selection
}

// 获取通知类型标签
const getNoticeTypeTag = (type) => {
  const typeMap = {
    info: 'info',
    warning: 'warning',
    notification: 'success'
  }
  return typeMap[type] || 'info'
}

// 获取通知类型文本
const getNoticeTypeText = (type) => {
  const typeMap = {
    info: '信息',
    warning: '警告',
    notification: '通知'
  }
  return typeMap[type] || '信息'
}

// 页面加载时获取数据
onMounted(() => {
  getNotices()
})
</script>

<style scoped>
.notice-management {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.notice-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.search-box {
  display: flex;
  align-items: center;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.batch-operation {
  margin-top: 10px;
}
</style>
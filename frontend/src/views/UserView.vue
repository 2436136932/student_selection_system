<template>
  <div class="user-management">
    <h2>用户管理</h2>
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <div class="search-container">
            <el-select v-model="searchCondition" placeholder="请选择搜索条件" style="width: 150px; margin-right: 10px;">
              <el-option label="用户名" value="username" />
              <el-option label="姓名" value="name" />
              <el-option label="邮箱" value="email" />
              <el-option label="角色" value="role" />
            </el-select>
            <el-input 
              v-model="searchKeyword" 
              placeholder="请输入搜索关键词" 
              style="width: 200px; margin-right: 10px;"
              clearable
            />
            <el-button type="primary" @click="searchUsers">搜索</el-button>
            <el-button @click="resetSearch">重置</el-button>
          </div>
        </div>
      </template>
      
      <!-- 用户列表 -->
      <el-table :data="users" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="name" label="姓名" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="phone" label="电话" />
        <el-table-column prop="role" label="角色" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button type="primary" size="small" @click="editUser(scope.row)">
              编辑
            </el-button>
            <el-button 
              :type="scope.row.status === 1 ? 'warning' : 'success'" 
              size="small" 
              @click="toggleStatus(scope.row)"
              style="margin-left: 10px"
            >
              {{ scope.row.status === 1 ? '禁用' : '启用' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination" v-if="total > 0">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
        />
      </div>
    </el-card>
    
    <!-- 编辑用户对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="编辑用户信息"
      width="500px"
    >
      <el-form :model="form" label-width="80px" :rules="rules" ref="formRef">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" disabled placeholder="用户名不可修改" />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" type="email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入电话" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role" placeholder="请选择角色">
            <el-option label="管理员" value="admin" />
            <el-option label="教师" value="teacher" />
            <el-option label="学生" value="student" />
          </el-select>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="不修改密码请留空" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">保存</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'

// 响应式数据
const users = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 搜索相关数据
const searchCondition = ref('username')
const searchKeyword = ref('')
const allUsers = ref([]) // 保存原始用户数据，用于搜索和排序

// 编辑对话框
const dialogVisible = ref(false)
const formRef = ref(null)
const form = ref({
  id: null,
  username: '',
  name: '',
  email: '',
  phone: '',
  role: '',
  password: ''
})

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ]
}

// 获取用户列表
const getUsers = async () => {
  loading.value = true
  try {
    // 获取当前页的数据
    const response = await axios.get('/api/users/page', {
      params: {
        current: currentPage.value,
        size: pageSize.value
      }
    })
    
    // 如果是第一页，保存完整数据到allUsers用于搜索和排序
    if (currentPage.value === 1) {
      allUsers.value = response.data.records
    }
    
    users.value = response.data.records
    total.value = response.data.total
  } catch (error) {
    ElMessage.error('获取用户列表失败')
    console.error('获取用户列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 编辑用户
const editUser = (row) => {
  // 深拷贝用户数据到表单
  form.value = { ...row }
  dialogVisible.value = true
}

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    // 准备提交的数据（排除空密码）
    const submitData = { ...form.value }
    if (!submitData.password) {
      delete submitData.password
    }
    
    await axios.put(`/api/users/${form.value.id}`, submitData)
    ElMessage.success('用户信息更新成功')
    dialogVisible.value = false
    getUsers() // 重新获取用户列表
  } catch (error) {
    if (error.name === 'Error') {
      ElMessage.error('更新用户信息失败')
      console.error('更新用户信息失败:', error)
    }
    return false
  }
}

// 切换用户状态（启用/禁用）
const toggleStatus = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  const statusText = newStatus === 1 ? '启用' : '禁用'
  
  try {
    await ElMessageBox.confirm(`确定要${statusText}用户 "${row.name}" 吗？`, '确认操作', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await axios.put(`/api/users/${row.id}`, {
      status: newStatus
    })
    
    ElMessage.success(`用户${statusText}成功`)
    getUsers() // 重新获取用户列表
  } catch (error) {
    if (error.name !== 'CanceledError') {
      ElMessage.error(`用户${statusText}失败`)
      console.error(`用户${statusText}失败:`, error)
    }
  }
}

// 搜索用户
const searchUsers = () => {
  if (!allUsers.value.length) {
    ElMessage.warning('请先获取用户列表')
    return
  }
  
  // 使用线性搜索过滤用户
  const filteredUsers = linearSearch(allUsers.value, searchCondition.value, searchKeyword.value)
  
  // 使用冒泡排序对搜索结果进行排序（按用户名排序）
  const sortedUsers = bubbleSort(filteredUsers, 'username')
  
  // 更新显示的用户列表
  users.value = sortedUsers
  total.value = sortedUsers.length
  currentPage.value = 1 // 搜索结果从第一页开始显示
}

// 重置搜索
const resetSearch = () => {
  searchCondition.value = 'username'
  searchKeyword.value = ''
  getUsers() // 重新获取原始用户列表
}

// 分页处理
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  getUsers()
}

const handleCurrentChange = (current) => {
  currentPage.value = current
  getUsers()
}

// 冒泡排序算法
const bubbleSort = (arr, sortBy) => {
  const sortedArr = [...arr]
  const len = sortedArr.length
  
  for (let i = 0; i < len - 1; i++) {
    for (let j = 0; j < len - 1 - i; j++) {
      // 比较相邻元素，根据sortBy字段进行排序
      if (sortedArr[j][sortBy] > sortedArr[j + 1][sortBy]) {
        // 交换元素
        const temp = sortedArr[j]
        sortedArr[j] = sortedArr[j + 1]
        sortedArr[j + 1] = temp
      }
    }
  }
  
  return sortedArr
}

// 线性搜索算法
const linearSearch = (arr, condition, keyword) => {
  if (!keyword) return arr
  
  const results = []
  const lowerKeyword = keyword.toLowerCase()
  
  for (let i = 0; i < arr.length; i++) {
    const item = arr[i]
    const value = item[condition]
    
    if (value && value.toLowerCase().includes(lowerKeyword)) {
      results.push(item)
    }
  }
  
  return results
}

// 页面加载时获取用户列表
onMounted(() => {
  getUsers()
})
</script>

<style scoped>
.user-management {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-container {
  display: flex;
  align-items: center;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}
</style>
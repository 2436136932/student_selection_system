<template>
  <div class="student-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>学生管理</span>
          <el-button v-if="hasRole('admin')" type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon> 新增学生
          </el-button>
        </div>
      </template>
      <div class="card-body">
        <el-form :model="searchForm" :inline="true" class="search-form">
          <el-form-item label="学号">
            <el-input v-model="searchForm.studentNumber" placeholder="请输入学号"></el-input>
          </el-form-item>
          <el-form-item label="姓名">
            <el-input v-model="searchForm.name" placeholder="请输入姓名"></el-input>
          </el-form-item>
          <el-form-item label="班级">
            <el-input v-model="searchForm.className" placeholder="请输入班级"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="resetForm">重置</el-button>
          </el-form-item>
        </el-form>

        <el-table :data="students" style="width: 100%" stripe>
          <el-table-column prop="studentNumber" label="学号" width="120"></el-table-column>
          <el-table-column prop="name" label="姓名" width="120"></el-table-column>
          <el-table-column prop="gender" label="性别" width="80"></el-table-column>
          <el-table-column prop="birthDate" label="出生日期" width="150"></el-table-column>
          <el-table-column prop="className" label="班级" width="150"></el-table-column>
          <el-table-column prop="major" label="专业" width="150"></el-table-column>
          <el-table-column prop="admissionYear" label="入学年份" width="120"></el-table-column>
          <el-table-column prop="status" label="状态" width="100"></el-table-column>
          <el-table-column prop="phone" label="手机号" width="150"></el-table-column>
          <el-table-column prop="email" label="邮箱" width="200"></el-table-column>
          <el-table-column prop="userId" label="用户ID" width="100"></el-table-column>
          <el-table-column label="操作" width="150" fixed="right" v-if="hasRole('admin')">
            <template #default="scope">
              <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
              <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
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

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="学生信息"
      width="500px"
    >
      <el-form :model="form" label-width="100px">
        <el-form-item label="学号">
          <el-input v-model="form.studentNumber" placeholder="请输入学号"></el-input>
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="form.name" placeholder="请输入姓名" @change="findUserByStudentName"></el-input>
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="form.gender" placeholder="请选择性别">
            <el-option label="男" value="男"></el-option>
            <el-option label="女" value="女"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="出生日期">
          <el-date-picker v-model="form.birthDate" type="date" placeholder="请选择出生日期" style="width: 100%"></el-date-picker>
        </el-form-item>
        <el-form-item label="班级">
          <el-input v-model="form.className" placeholder="请输入班级"></el-input>
        </el-form-item>
        <el-form-item label="专业">
          <el-select v-model="form.major" placeholder="请选择专业" clearable>
            <el-option
              v-for="item in majors"
              :key="item.id"
              :label="item.name"
              :value="item.name"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="入学年份">
          <el-input-number v-model="form.admissionYear" :min="2000" :max="2025" placeholder="请输入入学年份"></el-input-number>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option label="在读" value="1"></el-option>
            <el-option label="毕业" value="0"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" placeholder="请输入手机号"></el-input>
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="请输入邮箱"></el-input>
        </el-form-item>
        <el-form-item label="用户ID">
          <el-input-number v-model="form.userId" placeholder="请输入用户ID"></el-input-number>
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
import { ref, reactive, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
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

// 检查用户是否是管理员或教师
const isAdminOrTeacher = computed(() => {
  const userInfo = getUserInfo()
  return userInfo.role === 'admin' || userInfo.role === 'teacher'
})

// 检查用户是否是管理员
const isAdmin = computed(() => {
  const userInfo = getUserInfo()
  return userInfo.role === 'admin'
})

const students = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const dialogVisible = ref(false)
const majors = ref([])

const searchForm = reactive({
  studentNumber: '',
  name: '',
  className: ''
})

const form = reactive({
  studentNumber: '',
  name: '',
  gender: '',
  birthDate: null,
  className: '',
  major: '',
  admissionYear: null,
  status: 1,
  phone: '',
  email: '',
  userId: null
})

// 获取学生列表
const getStudents = () => {
  axios.get('/api/students', {
    params: {
      page: currentPage.value,
      size: pageSize.value,
      studentNumber: searchForm.studentNumber,
      name: searchForm.name,
      className: searchForm.className
    }
  }).then(response => {
    console.log('学生列表响应数据:', response.data)
    students.value = response.data.records || []
    total.value = response.data.total || 0
  }).catch(error => {
    ElMessage.error('获取学生列表失败')
    console.error('Error fetching students:', error)
  })
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  getStudents()
}

// 重置表单
const resetForm = () => {
  searchForm.studentNumber = ''
  searchForm.name = ''
  searchForm.className = ''
  currentPage.value = 1
  getStudents()
}

// 重置学生表单数据
const resetStudentForm = () => {
  form.studentNumber = ''
  form.name = ''
  form.gender = ''
  form.birthDate = null
  form.className = ''
  form.major = ''
  form.admissionYear = null
  form.status = 1
  form.phone = ''
  form.email = ''
  form.userId = null
  // 移除id属性
  delete form.id
}

// 新增学生
const handleAdd = () => {
  resetStudentForm()
  dialogVisible.value = true
}

// 分页处理
const handleSizeChange = (size) => {
  pageSize.value = size
  getStudents()
}

const handleCurrentChange = (current) => {
  currentPage.value = current
  getStudents()
}

// 新增/编辑学生
const handleSubmit = () => {
  if (form.id) {
    // 编辑学生
    axios.put(`/api/students/${form.id}`, form)
      .then(response => {
        ElMessage.success('编辑学生成功')
        dialogVisible.value = false
        getStudents()
      })
      .catch(error => {
        ElMessage.error('编辑学生失败')
        console.error('Error updating student:', error)
      })
  } else {
    // 新增学生
        console.log('提交的表单数据:', form)
        axios.post('/api/students', form)
          .then(response => {
            console.log('新增学生响应:', response)
            ElMessage.success('新增学生成功')
            dialogVisible.value = false
            getStudents()
          })
          .catch(error => {
            ElMessage.error('新增学生失败')
            console.error('Error creating student:', error)
            if (error.response) {
              console.error('错误响应数据:', error.response.data)
              console.error('错误响应状态:', error.response.status)
              console.error('错误响应头:', error.response.headers)
            } else if (error.request) {
              console.error('请求发送失败:', error.request)
            } else {
              console.error('请求配置错误:', error.message)
            }
          })
  }
}

// 根据姓名查找用户信息
const findUserByStudentName = async () => {
  console.log('开始查找用户信息，姓名:', form.name)
  if (!form.name) {
    console.log('姓名为空，不执行查找')
    return
  }
  
  try {
    // 尝试根据姓名（真实姓名）查找用户
    const url = `/api/users/realname/${encodeURIComponent(form.name)}`
    console.log('请求URL:', url)
    const response = await axios.get(url)
    console.log('用户查询响应:', response)
    if (response.data) {
      // 自动填充用户信息
      form.phone = response.data.phone || ''
      form.email = response.data.email || ''
      form.userId = response.data.id
      console.log('自动填充后的表单数据:', form)
      ElMessage.success('已自动填充用户信息')
    } else {
      console.log('响应数据为空')
      ElMessage.info('未找到匹配的用户信息，请手动输入')
    }
  } catch (error) {
    console.error('查找用户信息时发生错误:', error)
    if (error.response) {
      console.error('错误响应状态:', error.response.status)
      console.error('错误响应数据:', error.response.data)
      if (error.response.status === 404) {
        ElMessage.info('未找到该姓名对应的用户信息，请手动输入')
      } else if (error.response.status === 403) {
        ElMessage.warning('您没有权限访问此信息，请手动输入')
      } else {
        ElMessage.error('获取用户信息失败，请稍后重试')
      }
    } else if (error.request) {
      console.error('请求发送失败:', error.request)
      ElMessage.error('网络连接失败，请检查网络设置')
    } else {
      console.error('请求配置错误:', error.message)
      ElMessage.error('请求配置错误，请稍后重试')
    }
  }
}

// 编辑学生
const handleEdit = (row) => {
  // 先重置表单
  resetStudentForm()
  // 再赋值编辑数据
  Object.assign(form, row)
  dialogVisible.value = true
}

// 删除学生
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除学生 ${row.name} 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    axios.delete(`/api/students/${row.id}`)
      .then(response => {
        ElMessage.success('删除学生成功')
        getStudents()
      })
      .catch(error => {
        ElMessage.error('删除学生失败')
        console.error('Error deleting student:', error)
      })
  }).catch(() => {
    // 取消删除
  })
}

// 获取专业列表
const fetchMajors = () => {
  axios.get('/api/majors')
    .then(response => {
      majors.value = response.data || []
    })
    .catch(error => {
      console.error('获取专业列表失败:', error)
      ElMessage.error('获取专业列表失败')
    })
}

// 初始化数据
getStudents()
fetchMajors()

// 页面加载时打印提示信息
console.log('学生管理页面已加载，自动填充功能说明：')
console.log('1. 输入学号后，系统会自动从用户表中查找匹配的用户信息')
console.log('2. 如果找到匹配的用户信息，会自动填充姓名、手机号、邮箱和用户ID字段')
console.log('3. 如果未找到匹配的用户信息，请手动输入相关信息')
</script>

<style scoped>
.student-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}
</style>
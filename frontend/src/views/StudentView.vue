<template>
  <div class="student-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>学生管理</span>
          <el-button v-if="hasRole('admin')" type="primary" @click="dialogVisible = true">
            <el-icon-plus></el-icon-plus> 新增学生
          </el-button>
        </div>
      </template>
      <div class="card-body">
        <el-form :model="searchForm" :inline="true" class="search-form">
          <el-form-item label="学号">
            <el-input v-model="searchForm.studentId" placeholder="请输入学号"></el-input>
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
          <el-table-column prop="studentId" label="学号" width="120"></el-table-column>
          <el-table-column prop="name" label="姓名" width="120"></el-table-column>
          <el-table-column prop="gender" label="性别" width="80"></el-table-column>
          <el-table-column prop="age" label="年龄" width="80"></el-table-column>
          <el-table-column prop="className" label="班级" width="150"></el-table-column>
          <el-table-column prop="major" label="专业" width="150"></el-table-column>
          <el-table-column prop="gpa" label="GPA" width="80"></el-table-column>
          <el-table-column prop="totalCredits" label="总学分" width="100"></el-table-column>
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
          <el-input v-model="form.studentId" placeholder="请输入学号"></el-input>
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="form.name" placeholder="请输入姓名"></el-input>
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="form.gender" placeholder="请选择性别">
            <el-option label="男" value="男"></el-option>
            <el-option label="女" value="女"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="年龄">
          <el-input-number v-model="form.age" :min="16" :max="30" placeholder="请输入年龄"></el-input-number>
        </el-form-item>
        <el-form-item label="班级">
          <el-input v-model="form.className" placeholder="请输入班级"></el-input>
        </el-form-item>
        <el-form-item label="专业">
          <el-input v-model="form.major" placeholder="请输入专业"></el-input>
        </el-form-item>
        <el-form-item label="GPA">
          <el-input-number v-model="form.gpa" :min="0" :max="4" :step="0.01" placeholder="请输入GPA"></el-input-number>
        </el-form-item>
        <el-form-item label="总学分">
          <el-input-number v-model="form.totalCredits" :min="0" :max="200" placeholder="请输入总学分"></el-input-number>
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
import { ElMessage } from 'element-plus'
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

const searchForm = reactive({
  studentId: '',
  name: '',
  className: ''
})

const form = reactive({
  studentId: '',
  name: '',
  gender: '',
  age: null,
  className: '',
  major: '',
  gpa: null,
  totalCredits: null
})

// 获取学生列表
const getStudents = () => {
  axios.get('/api/students', {
    params: {
      page: currentPage.value,
      size: pageSize.value,
      studentId: searchForm.studentId,
      name: searchForm.name,
      className: searchForm.className
    }
  }).then(response => {
    students.value = response.data.content
    total.value = response.data.totalElements
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
  searchForm.studentId = ''
  searchForm.name = ''
  searchForm.className = ''
  currentPage.value = 1
  getStudents()
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
  if (form.studentId) {
    // 编辑学生
    axios.put(`/api/students/${form.studentId}`, form)
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
    axios.post('/api/students', form)
      .then(response => {
        ElMessage.success('新增学生成功')
        dialogVisible.value = false
        getStudents()
      })
      .catch(error => {
        ElMessage.error('新增学生失败')
        console.error('Error creating student:', error)
      })
  }
}

// 编辑学生
const handleEdit = (row) => {
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
    axios.delete(`/api/students/${row.studentId}`)
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

// 初始化数据
getStudents()
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
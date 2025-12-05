<template>
  <div class="standard-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>评奖标准管理</span>
          <el-button v-if="hasRole('admin') || hasRole('teacher')" type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon> 新增评奖标准
          </el-button>
        </div>
      </template>
      <div class="card-body">
        <el-form :model="searchForm" :inline="true" class="search-form">
          <el-form-item label="标准代码">
            <el-input v-model="searchForm.code" placeholder="请输入标准代码"></el-input>
          </el-form-item>
          <el-form-item label="标准名称">
            <el-input v-model="searchForm.name" placeholder="请输入标准名称"></el-input>
          </el-form-item>
          <el-form-item label="负责人">
            <el-input v-model="searchForm.teacher" placeholder="请输入负责人姓名"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="resetForm">重置</el-button>
          </el-form-item>
        </el-form>

        <el-table :data="standards" style="width: 100%" stripe>
          <el-table-column prop="code" label="标准代码" width="120"></el-table-column>
          <el-table-column prop="name" label="标准名称" width="200"></el-table-column>
          <el-table-column prop="teacher" label="负责人" width="120"></el-table-column>
          <el-table-column prop="weight" label="权重" width="80"></el-table-column>
          <el-table-column prop="capacity" label="达标值" width="80"></el-table-column>
          <el-table-column prop="enrolled" label="符合人数" width="80"></el-table-column>
          <el-table-column prop="semester" label="适用学期" width="120"></el-table-column>
          <el-table-column label="操作" width="150" fixed="right" v-if="hasRole('admin') || hasRole('teacher')">
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
      title="评奖标准信息"
      width="500px"
    >
      <el-form :model="form" label-width="100px">
        <el-form-item label="标准代码">
          <el-input v-model="form.code" placeholder="请输入标准代码"></el-input>
        </el-form-item>
        <el-form-item label="标准名称">
          <el-input v-model="form.name" placeholder="请输入标准名称"></el-input>
        </el-form-item>
        <el-form-item label="负责人">
          <el-input v-model="form.teacher" placeholder="请输入负责人姓名"></el-input>
        </el-form-item>
        <el-form-item label="权重">
          <el-input-number v-model="form.weight" :min="1" :max="10" placeholder="请输入权重"></el-input-number>
        </el-form-item>
        <el-form-item label="达标值">
          <el-input-number v-model="form.capacity" :min="0" :max="100" placeholder="请输入达标值"></el-input-number>
        </el-form-item>
        <el-form-item label="适用学期">
          <el-input v-model="form.semester" placeholder="请输入适用学期（如：2023-2024-1）"></el-input>
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
import { ref, reactive } from 'vue'
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

const standards = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const dialogVisible = ref(false)

const searchForm = reactive({
  code: '',
  name: '',
  teacher: ''
})

const form = reactive({
  id: null,
  code: '',
  name: '',
  teacher: '',
  weight: null,
  capacity: null,
  enrolled: 0,
  semester: ''
})

// 获取评奖标准列表
const getStandards = () => {
  axios.get('/api/standards/page', {
    params: {
      current: currentPage.value,
      size: pageSize.value,
      code: searchForm.code,
      name: searchForm.name,
      teacher: searchForm.teacher
    }
  }).then(response => {
    standards.value = response.data.records
    total.value = response.data.total
  }).catch(error => {
    ElMessage.error('获取评奖标准列表失败')
    console.error('Error fetching standards:', error)
  })
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  getStandards()
}

// 重置表单
const resetForm = () => {
  searchForm.code = ''
  searchForm.name = ''
  searchForm.teacher = ''
  currentPage.value = 1
  getStandards()
}

// 分页处理
const handleSizeChange = (size) => {
  pageSize.value = size
  getStandards()
}

const handleCurrentChange = (current) => {
  currentPage.value = current
  getStandards()
}

// 新增/编辑评奖标准
const handleSubmit = () => {
  if (form.id) {
    // 编辑评奖标准
    axios.put(`/api/standards/${form.id}`, form)
      .then(response => {
        ElMessage.success('编辑评奖标准成功')
        dialogVisible.value = false
        getStandards()
      })
      .catch(error => {
        ElMessage.error('编辑评奖标准失败')
        console.error('Error updating standard:', error)
      })
  } else {
    // 新增评奖标准
    axios.post('/api/standards', form)
      .then(response => {
        ElMessage.success('新增评奖标准成功')
        dialogVisible.value = false
        getStandards()
      })
      .catch(error => {
        ElMessage.error('新增评奖标准失败')
        console.error('Error creating standard:', error)
      })
  }
}

// 新增评奖标准
const handleAdd = () => {
  // 重置表单
  form.id = null
  form.code = ''
  form.name = ''
  form.teacher = ''
  form.weight = null
  form.capacity = null
  form.enrolled = 0
  form.semester = ''
  dialogVisible.value = true
}

// 编辑评奖标准
const handleEdit = (row) => {
  Object.assign(form, row)
  dialogVisible.value = true
}

// 删除评奖标准
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除评奖标准 ${row.name} 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    axios.delete(`/api/standards/${row.id}`)
      .then(response => {
        ElMessage.success('删除评奖标准成功')
        getStandards()
      })
      .catch(error => {
        ElMessage.error('删除评奖标准失败')
        console.error('Error deleting standard:', error)
      })
  }).catch(() => {
    // 取消删除
  })
}

// 初始化数据
getStandards()
</script>

<style scoped>
.standard-container {
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
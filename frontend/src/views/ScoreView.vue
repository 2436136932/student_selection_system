<template>
  <div class="score-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>评分管理</span>
          <el-button v-if="hasRole('admin') || hasRole('teacher')" type="primary" @click="dialogVisible = true">
            <el-icon-plus></el-icon-plus> 录入评分
          </el-button>
        </div>
      </template>
      <div class="card-body">
        <el-form :model="searchForm" :inline="true" class="search-form">
          <el-form-item label="学生学号">
            <el-input v-model="searchForm.studentId" placeholder="请输入学号"></el-input>
          </el-form-item>
          <el-form-item label="标准代码">
            <el-input v-model="searchForm.courseCode" placeholder="请输入标准代码"></el-input>
          </el-form-item>
          <el-form-item label="适用学期">
            <el-input v-model="searchForm.semester" placeholder="请输入适用学期"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="resetForm">重置</el-button>
          </el-form-item>
        </el-form>

        <el-table :data="scores" style="width: 100%" stripe>
          <el-table-column prop="studentId" label="学生学号" width="120"></el-table-column>
          <el-table-column prop="courseCode" label="标准代码" width="120"></el-table-column>
          <el-table-column prop="score" label="评分" width="80"></el-table-column>
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

        <div class="statistics" style="margin-top: 20px">
          <el-card shadow="hover" style="margin-right: 20px; float: left">
            <template #header>
              <div class="card-header">
                <span>评分统计</span>
              </div>
            </template>
            <div class="statistics-body">
              <el-form :model="statisticsForm" :inline="true">
                <el-form-item label="标准代码">
                  <el-input v-model="statisticsForm.courseCode" placeholder="请输入标准代码"></el-input>
                </el-form-item>
                <el-form-item label="适用学期">
                  <el-input v-model="statisticsForm.semester" placeholder="请输入适用学期"></el-input>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="getStatistics">查询统计</el-button>
                </el-form-item>
              </el-form>
              <div v-if="statistics" style="margin-top: 20px">
                <p>总分: {{ statistics.total }}</p>
                <p>平均分: {{ statistics.average.toFixed(2) }}</p>
              </div>
            </div>
          </el-card>
        </div>
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="评分信息"
      width="500px"
    >
      <el-form :model="form" label-width="100px">
        <el-form-item label="学生学号">
          <el-input v-model="form.studentId" placeholder="请输入学号"></el-input>
        </el-form-item>
        <el-form-item label="标准代码">
          <el-input v-model="form.courseCode" placeholder="请输入标准代码"></el-input>
        </el-form-item>
        <el-form-item label="评分">
          <el-input-number v-model="form.score" :min="0" :max="100" placeholder="请输入评分"></el-input-number>
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

const scores = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const dialogVisible = ref(false)
const statistics = ref(null)

const searchForm = reactive({
  studentId: '',
  courseCode: '',
  semester: ''
})

const form = reactive({
  studentId: '',
  courseCode: '',
  score: null,
  semester: ''
})

const statisticsForm = reactive({
  courseCode: '',
  semester: ''
})

// 获取成绩列表
const getScores = () => {
  axios.get('/api/scores', {
    params: {
      page: currentPage.value,
      size: pageSize.value,
      studentId: searchForm.studentId,
      courseCode: searchForm.courseCode,
      semester: searchForm.semester
    }
  }).then(response => {
    scores.value = response.data.content
    total.value = response.data.totalElements
  }).catch(error => {
    ElMessage.error('获取成绩列表失败')
    console.error('Error fetching scores:', error)
  })
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  getScores()
}

// 重置表单
const resetForm = () => {
  searchForm.studentId = ''
  searchForm.courseCode = ''
  searchForm.semester = ''
  currentPage.value = 1
  getScores()
}

// 分页处理
const handleSizeChange = (size) => {
  pageSize.value = size
  getScores()
}

const handleCurrentChange = (current) => {
  currentPage.value = current
  getScores()
}

// 新增/编辑成绩
const handleSubmit = () => {
  if (form.studentId && form.courseCode) {
    // 编辑成绩
    axios.put(`/api/scores`, form)
      .then(response => {
        ElMessage.success('更新成绩成功')
        dialogVisible.value = false
        getScores()
      })
      .catch(error => {
        ElMessage.error('更新成绩失败')
        console.error('Error updating score:', error)
      })
  } else {
    // 新增成绩
    axios.post('/api/scores', form)
      .then(response => {
        ElMessage.success('录入成绩成功')
        dialogVisible.value = false
        getScores()
      })
      .catch(error => {
        ElMessage.error('录入成绩失败')
        console.error('Error creating score:', error)
      })
  }
}

// 编辑成绩
const handleEdit = (row) => {
  Object.assign(form, row)
  dialogVisible.value = true
}

// 删除成绩
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除该成绩记录吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    axios.delete('/api/scores', {
      params: {
        studentId: row.studentId,
        courseCode: row.courseCode
      }
    }).then(response => {
      ElMessage.success('删除成绩成功')
      getScores()
    }).catch(error => {
      ElMessage.error('删除成绩失败')
      console.error('Error deleting score:', error)
    })
  }).catch(() => {
    // 取消删除
  })
}

// 获取成绩统计
const getStatistics = () => {
  axios.get('/api/scores/statistics', {
    params: {
      courseCode: statisticsForm.courseCode,
      semester: statisticsForm.semester
    }
  }).then(response => {
    statistics.value = response.data
  }).catch(error => {
    ElMessage.error('获取成绩统计失败')
    console.error('Error fetching statistics:', error)
  })
}

// 初始化数据
getScores()
</script>

<style scoped>
.score-container {
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

.statistics {
  margin-top: 20px;
}
</style>
<template>
  <div class="score-container">
    <h2>评分管理</h2>
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <el-button v-if="hasRole('admin') || hasRole('teacher')" type="primary" @click="handleAdd">
            <el-icon-plus></el-icon-plus> 录入评分
          </el-button>
        </div>
      </template>
      <div class="card-body">
        <el-form :model="searchForm" :inline="true" class="search-form">
          <el-form-item label="学生学号">
            <el-input 
              v-model="searchForm.studentNumber" 
              placeholder="请输入学号"
              :disabled="hasRole('student')"
            ></el-input>
          </el-form-item>
          <el-form-item label="标准代码">
            <el-input v-model="searchForm.courseCode" placeholder="请输入标准代码"></el-input>
          </el-form-item>
          <el-form-item label="课程名称">
            <el-input v-model="searchForm.courseName" placeholder="请输入课程名称"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="resetForm">重置</el-button>
            <el-dropdown @command="handleSort">
              <el-button type="warning">
                排序
                <el-icon class="el-icon--right">
                  <arrow-down></arrow-down>
                </el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="descending">从高到低排序</el-dropdown-item>
                  <el-dropdown-item command="ascending">从低到高排序</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </el-form-item>
        </el-form>

        <el-table :data="scores" style="width: 100%" stripe>
          <el-table-column prop="studentNumber" label="学生学号" width="120"></el-table-column>
          <el-table-column prop="studentName" label="学生姓名" width="120"></el-table-column>
          <el-table-column prop="courseCode" label="标准代码" width="120"></el-table-column>
          <el-table-column prop="courseName" label="课程名称" width="200"></el-table-column>
          <el-table-column prop="totalScore" label="评分" width="80"></el-table-column>
          <el-table-column prop="grade" label="等级" width="80"></el-table-column>
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
                  <el-form-item label="学号">
                      <el-input 
                        v-model="statisticsForm.studentNumber" 
                        placeholder="请输入学号"
                        :disabled="hasRole('student')"
                      ></el-input>
                    </el-form-item>
                  <el-form-item>
                    <el-button type="primary" @click="getStatistics">查询统计</el-button>
                  </el-form-item>
                </el-form>
              <div v-if="statistics" style="margin-top: 20px">
                <p>总课程数: {{ statistics.totalCount }}</p>
                <p>有效成绩数: {{ statistics.validCount }}</p>
                <p>平均分: {{ statistics.averageScore ? statistics.averageScore.toFixed(2) : '0.00' }}</p>
              </div>
              
              <!-- 评分等级分布图表 -->
              <div v-if="statisticsDetails" style="margin-top: 20px">
                <h4 style="margin-bottom: 10px">评分等级分布</h4>
                <div ref="chartRef" style="width: 100%; height: 300px;"></div>
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
          <el-input v-model="form.studentNumber" placeholder="请输入学号" @change="getStudentNameByNumber"></el-input>
        </el-form-item>
        <el-form-item label="学生姓名">
          <el-input v-model="form.studentName" placeholder="学生姓名" readonly></el-input>
        </el-form-item>
        <el-form-item label="标准代码">
          <el-select v-model="form.courseCode" placeholder="请选择标准代码" @change="getCourseInfoByCode(form.courseCode)">
            <el-option
              v-for="course in courses"
              :key="course.id"
              :label="course.courseCode"
              :value="course.courseCode"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="课程名称">
          <el-input v-model="form.courseName" placeholder="课程名称" readonly></el-input>
        </el-form-item>
        <el-form-item label="评分">
          <el-input-number v-model="form.totalScore" :min="0" :max="100" placeholder="请输入评分" @change="calculateGrade"></el-input-number>
        </el-form-item>
        <el-form-item label="等级">
          <el-input v-model="form.grade" placeholder="等级" readonly></el-input>
        </el-form-item>
        <el-form-item label="适用学期">
          <el-input v-model="form.semester" placeholder="请输入适用学期（如：2025-2026-1）"></el-input>
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
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'
import * as echarts from 'echarts'
import { ArrowDown } from '@element-plus/icons-vue'

// 获取用户信息
const getUserInfo = () => {
  const userInfoStr = localStorage.getItem('userInfo')
  return userInfoStr ? JSON.parse(userInfoStr) : {}
}

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

const scores = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const dialogVisible = ref(false)
const statistics = ref(null)
const statisticsDetails = ref([])
const currentScoreId = ref(null)
const chartRef = ref(null)
let chartInstance = null
const sortDirection = ref(null) // null: 未排序, 'descending': 从高到低, 'ascending': 从低到高

const searchForm = reactive({
  studentNumber: '',
  courseCode: '',
  courseName: ''
})

const form = reactive({
  studentNumber: '',
  studentName: '',
  studentId: '',
  courseCode: '',
  courseName: '',
  totalScore: null,
  grade: '',
  semester: ''
})

const statisticsForm = reactive({
  courseCode: '',
  studentNumber: ''
})

// 课程列表
const courses = ref([])

// 获取所有课程
const getAllCourses = () => {
  axios.get('/api/courses')
    .then(response => {
      courses.value = response.data
    })
    .catch(error => {
      console.error('获取课程列表失败:', error)
    })
}

// 根据课程代码获取课程信息
const getCourseInfoByCode = (courseCode) => {
  if (!courseCode) return
  
  const course = courses.value.find(course => course.courseCode === courseCode)
  if (course) {
    form.courseName = course.courseName
    form.semester = course.semester
  } else {
    // 如果本地没有找到，从服务器获取
    axios.get(`/api/courses/code/${courseCode}`)
      .then(response => {
        if (response.data) {
          form.courseName = response.data.courseName
          form.semester = response.data.semester
        }
      })
      .catch(error => {
        console.error('获取课程信息失败:', error)
      })
  }
}

// 根据学号获取学生姓名
const getStudentNameByNumber = () => {
  if (form.studentNumber) {
    axios.get(`/api/students/id/${form.studentNumber}`)
      .then(response => {
        if (response.data) {
          form.studentName = response.data.name
          form.studentId = response.data.id
        }
      })
      .catch(error => {
        console.error('获取学生信息失败:', error)
        form.studentName = ''
      })
  }
}

// 根据总分计算等级
const calculateGrade = () => {
  const totalScore = form.totalScore
  if (totalScore === null || totalScore === undefined) {
    form.grade = ''
    return
  }
  
  if (totalScore >= 90) {
    form.grade = 'A'
  } else if (totalScore >= 80 && totalScore <= 89) {
    form.grade = 'B'
  } else if (totalScore >= 70 && totalScore <= 79) {
    form.grade = 'C'
  } else if (totalScore >= 60 && totalScore <= 69) {
    form.grade = 'D'
  } else {
    form.grade = 'F'
  }
}

// 冒泡排序算法
const bubbleSort = (arr, direction) => {
  // 创建数组副本，避免修改原数组
  const sortedArr = [...arr]
  const n = sortedArr.length
  
  // 冒泡排序主逻辑
  for (let i = 0; i < n - 1; i++) {
    for (let j = 0; j < n - i - 1; j++) {
      // 获取当前元素和下一个元素的总分
      const currentScore = sortedArr[j].totalScore || 0
      const nextScore = sortedArr[j + 1].totalScore || 0
      
      // 根据排序方向决定比较逻辑
      if (direction === 'ascending') {
        // 升序排序（从低到高），如果当前元素大于下一个元素，则交换
        if (currentScore > nextScore) {
          // 交换元素
          const temp = sortedArr[j]
          sortedArr[j] = sortedArr[j + 1]
          sortedArr[j + 1] = temp
        }
      } else if (direction === 'descending') {
        // 降序排序（从高到低），如果当前元素小于下一个元素，则交换
        if (currentScore < nextScore) {
          // 交换元素
          const temp = sortedArr[j]
          sortedArr[j] = sortedArr[j + 1]
          sortedArr[j + 1] = temp
        }
      }
    }
  }
  
  return sortedArr
}

// 处理排序事件
const handleSort = (command) => {
  sortDirection.value = command
  // 对当前的scores数组进行排序
  scores.value = bubbleSort(scores.value, command)
}

// 获取成绩列表
const getScores = () => {
  // 如果是学生角色，自动填充当前学生学号
  const userInfo = getUserInfo()
  if (hasRole('student')) {
    // 学生只能查看自己的成绩，自动设置学号
    searchForm.studentNumber = userInfo.studentNumber || ''
    console.log('学生角色，自动填充学号：', searchForm.studentNumber)
  }
  
  axios.get('/api/scores/page', {
    params: {
      current: currentPage.value,
      size: pageSize.value,
      studentNumber: searchForm.studentNumber,
      courseCode: searchForm.courseCode,
      courseName: searchForm.courseName
    }
  }).then(response => {
    let records = response.data.records
    // 如果有排序方向，则对数据进行排序
    if (sortDirection.value) {
      records = bubbleSort(records, sortDirection.value)
    }
    scores.value = records
    total.value = response.data.total
    console.log('获取到成绩列表：', scores.value.length, '条记录')
  }).catch(error => {
    ElMessage.error('获取成绩列表失败')
    console.error('Error fetching scores:', error)
  })
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  sortDirection.value = null // 搜索新数据时重置排序状态
  getScores()
}

// 重置表单
const resetForm = () => {
  if (hasRole('student')) {
    // 学生角色只能查看自己的成绩，不能重置学号
    searchForm.courseCode = ''
    searchForm.courseName = ''
    console.log('学生角色，重置表单，保留学号：', searchForm.studentNumber)
  } else {
    // 管理员/教师可以重置所有搜索条件
    searchForm.studentNumber = ''
    searchForm.courseCode = ''
    searchForm.courseName = ''
    console.log('非学生角色，重置所有搜索条件')
  }
  currentPage.value = 1
  sortDirection.value = null // 重置表单时重置排序状态
  getScores()
}

// 重置评分表单
const resetScoreForm = () => {
  form.id = null
  form.studentNumber = ''
  form.studentName = ''
  form.studentId = ''
  form.courseCode = ''
  form.courseName = ''
  form.totalScore = null
  form.grade = ''
  form.semester = ''
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

// 新增成绩
const handleAdd = () => {
  // 重置评分表单
  resetScoreForm()
  dialogVisible.value = true
}

// 新增/编辑成绩
const handleSubmit = () => {
  if (form.studentNumber && form.courseCode) {
    if (form.id) {
      // 编辑成绩
      axios.put(`/api/scores/${form.id}`, form)
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
}

// 编辑评分
const handleEdit = (row) => {
  // 填充表单数据
  form.id = row.id
  form.studentNumber = row.studentNumber || ''
  form.studentName = row.studentName || ''
  form.studentId = row.studentId || ''
  form.courseCode = row.courseCode || ''
  form.courseName = row.courseName || ''
  form.totalScore = row.totalScore || null
  form.grade = row.grade || ''
  form.semester = row.semester || ''
  dialogVisible.value = true
}

// 删除成绩
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除该成绩记录吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    axios.delete(`/api/scores/${row.id}`)
      .then(response => {
        ElMessage.success('删除成绩成功')
        getScores()
      })
      .catch(error => {
        ElMessage.error('删除成绩失败')
        console.error('Error deleting score:', error)
      })
  }).catch(() => {
    // 取消删除
  })
}

// 获取成绩统计
const getStatistics = () => {
  // 如果是学生角色，自动填充当前学生学号
  const userInfo = getUserInfo()
  if (userInfo.role === 'student') {
    // 学生只能查看自己的成绩统计，自动设置学号
    statisticsForm.studentNumber = userInfo.studentNumber || ''
  }
  
  axios.get('/api/scores/statistics', {
    params: {
      courseCode: statisticsForm.courseCode,
      studentNumber: statisticsForm.studentNumber
    }
  }).then(response => {
    statistics.value = response.data
  }).catch(error => {
    ElMessage.error('获取成绩统计失败')
    console.error('Error fetching statistics:', error)
  })
  
  // 获取详细的评分数据，用于图表展示
  axios.get('/api/scores/page', {
    params: {
      current: 1,
      size: 100, // 假设最多显示100条记录
      studentNumber: statisticsForm.studentNumber,
      courseCode: statisticsForm.courseCode
    }
  }).then(response => {
    statisticsDetails.value = response.data.records
    // 更新图表
    updateChart()
  }).catch(error => {
    ElMessage.error('获取评分详情失败')
    console.error('Error fetching statistics details:', error)
  })
}

// 更新评分等级分布图表
const updateChart = () => {
  if (!chartRef.value) return
  
  // 初始化图表
  if (!chartInstance) {
    chartInstance = echarts.init(chartRef.value)
  }
  
  // 统计各个等级的数量
  const gradeCount = {
    'A': 0,
    'B': 0,
    'C': 0,
    'D': 0,
    'F': 0
  }
  
  statisticsDetails.value.forEach(score => {
    if (score.grade && gradeCount.hasOwnProperty(score.grade)) {
      gradeCount[score.grade]++
    }
  })
  
  // 准备图表数据
  const chartData = [
    { name: 'A', value: gradeCount['A'] },
    { name: 'B', value: gradeCount['B'] },
    { name: 'C', value: gradeCount['C'] },
    { name: 'D', value: gradeCount['D'] },
    { name: 'F', value: gradeCount['F'] }
  ]
  
  // 图表配置
  const option = {
    title: {
      text: '评分等级分布',
      left: 'center'
    },
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      data: ['A', 'B', 'C', 'D', 'F']
    },
    series: [
      {
        name: '评分等级',
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['50%', '60%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 20,
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: chartData
      }
    ]
  }
  
  // 设置图表配置
  chartInstance.setOption(option)
}

// 监听窗口大小变化，调整图表大小
window.addEventListener('resize', () => {
  if (chartInstance) {
    chartInstance.resize()
  }
})

// 初始化数据
onMounted(() => {
  getScores()
  getAllCourses()
})
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
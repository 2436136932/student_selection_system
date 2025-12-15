<template>
  <div class="course-container">
    <h2>课程管理</h2>
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <el-button v-if="hasRole('admin')" type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon> 新增课程
          </el-button>
        </div>
      </template>
      <div class="card-body">
        <el-form :model="searchForm" :inline="true" class="search-form">
          <el-form-item label="课程代码">
            <el-input v-model="searchForm.courseCode" placeholder="请输入课程代码"></el-input>
          </el-form-item>
          <el-form-item label="课程名称">
            <el-input v-model="searchForm.courseName" placeholder="请输入课程名称"></el-input>
          </el-form-item>
          <el-form-item label="教师">
            <el-input v-model="searchForm.teacherName" placeholder="请输入教师姓名"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="resetForm">重置</el-button>
          </el-form-item>
        </el-form>

        <el-table :data="courses" style="width: 100%" stripe>
          <el-table-column prop="courseCode" label="课程代码" width="120"></el-table-column>
          <el-table-column prop="courseName" label="课程名称" width="200"></el-table-column>
          <el-table-column prop="credits" label="学分" width="80"></el-table-column>
          <el-table-column prop="hours" label="学时" width="80"></el-table-column>
          <el-table-column prop="teacherName" label="授课老师" width="150"></el-table-column>
          <el-table-column prop="semester" label="学期" width="120"></el-table-column>
          <el-table-column prop="maxStudents" label="最大选课人数" width="150"></el-table-column>
          <el-table-column prop="currentStudents" label="当前选课人数" width="150"></el-table-column>
          <el-table-column prop="status" label="状态" width="100" :formatter="statusFormatter"></el-table-column>
          <el-table-column prop="description" label="课程描述" min-width="200"></el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="scope">
              <el-button size="small" @click="handleViewDetails(scope.row)">查看详情</el-button>
              <el-button size="small" @click="handleEdit(scope.row)" v-if="hasRole('admin')">编辑</el-button>
              <el-button size="small" type="danger" @click="handleDelete(scope.row)" v-if="hasRole('admin')">删除</el-button>
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
      title="课程信息"
      width="600px"
    >
      <el-form :model="form" label-width="120px">
        <el-form-item label="课程代码">
          <el-input v-model="form.courseCode" placeholder="请输入课程代码"></el-input>
        </el-form-item>
        <el-form-item label="课程名称">
          <el-input v-model="form.courseName" placeholder="请输入课程名称"></el-input>
        </el-form-item>
        <el-form-item label="学分">
          <el-input-number v-model="form.credits" :min="0" :max="10" :step="0.5" placeholder="请输入学分"></el-input-number>
        </el-form-item>
        <el-form-item label="学时">
          <el-input-number v-model="form.hours" :min="0" :max="200" placeholder="请输入学时"></el-input-number>
        </el-form-item>
        <el-form-item label="所属院系">
          <el-select v-model="form.department" placeholder="请选择所属院系">
            <el-option
              v-for="dept in departments"
              :key="dept"
              :label="dept"
              :value="dept"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="教师工号">
          <el-input v-model="form.teacherNumber" placeholder="请输入教师工号"></el-input>
        </el-form-item>
        <el-form-item label="学期">
          <el-input v-model="form.semester" placeholder="请输入学期，如：2024-2025学年第一学期"></el-input>
        </el-form-item>
        <el-form-item label="年份">
          <el-input-number v-model="form.year" :min="2000" :max="2030" placeholder="请输入年份"></el-input-number>
        </el-form-item>
        <el-form-item label="最大选课人数">
          <el-input-number v-model="form.maxStudents" :min="10" :max="200" placeholder="请输入最大选课人数"></el-input-number>
        </el-form-item>
        <el-form-item label="当前选课人数">
          <el-input-number v-model="form.currentStudents" :min="0" :max="form.maxStudents" placeholder="请输入当前选课人数"></el-input-number>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option label="开设" value="1"></el-option>
            <el-option label="关闭" value="0"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="课程描述">
          <el-input v-model="form.description" type="textarea" placeholder="请输入课程描述" :rows="4"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 课程详情对话框 -->
    <el-dialog
      v-model="detailVisible"
      title="课程详情"
      width="800px"
    >
      <div v-if="selectedCourse" class="course-detail">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-card shadow="never" class="detail-card">
              <template #header>
                <div class="detail-header">基本信息</div>
              </template>
              <div class="detail-content">
                <div class="detail-item">
                  <span class="detail-label">课程代码：</span>
                  <span class="detail-value">{{ selectedCourse.courseCode }}</span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">课程名称：</span>
                  <span class="detail-value">{{ selectedCourse.courseName }}</span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">学分：</span>
                  <span class="detail-value">{{ selectedCourse.credits }}</span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">学时：</span>
                  <span class="detail-value">{{ selectedCourse.hours }}</span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">授课老师：</span>
                  <span class="detail-value">{{ selectedCourse.teacherName || '未分配' }}</span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">学期：</span>
                  <span class="detail-value">{{ selectedCourse.semester }}</span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">年份：</span>
                  <span class="detail-value">{{ selectedCourse.year }}</span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">最大选课人数：</span>
                  <span class="detail-value">{{ selectedCourse.maxStudents }}</span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">当前选课人数：</span>
                  <span class="detail-value">{{ selectedCourse.currentStudents }}</span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">状态：</span>
                  <el-tag :type="selectedCourse.status === 1 ? 'success' : 'info'" size="small">
                    {{ selectedCourse.status === 1 ? '开设' : '关闭' }}
                  </el-tag>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card shadow="never" class="detail-card">
              <template #header>
                <div class="detail-header">课程描述</div>
              </template>
              <div class="detail-content">
                <div class="detail-item">
                  <p>{{ selectedCourse.description || '暂无课程描述' }}</p>
                </div>
              </div>
            </el-card>
            
            <!-- 教师选择部分 -->
            <el-card shadow="never" class="detail-card" style="margin-top: 20px;">
              <template #header>
                <div class="detail-header">授课老师</div>
              </template>
              <div class="detail-content">
                <div class="teacher-selection">
                  <el-button v-if="hasRole('admin')" size="small" type="primary" @click="getAllTeachers(); showTeacherSelection = true">选择授课老师</el-button>
                  <div class="selected-teachers" style="margin-top: 10px;">
                    <el-tag
                      v-for="teacher in selectedTeachers"
                      :key="teacher.id"
                      closable
                      @close="removeTeacher(teacher)"
                      size="small"
                      v-if="hasRole('admin')"
                    >
                      {{ teacher.name }} ({{ teacher.teacherNumber }})
                    </el-tag>
                    <span v-else-if="selectedTeachers.length > 0">{{ selectedTeachers[0].name }} ({{ selectedTeachers[0].teacherNumber }})</span>
                    <span v-else style="color: #909399; font-size: 14px;">暂无选择教师</span>
                  </div>
                </div>
              </div>
            </el-card>
            
            <!-- 学生选择部分 -->
            <el-card shadow="never" class="detail-card" style="margin-top: 20px;">
              <template #header>
                <div class="detail-header">选课学生</div>
              </template>
              <div class="detail-content">
                <div class="student-selection">
                  <el-button v-if="hasRole('admin')" size="small" type="primary" @click="getAllStudents(); showStudentSelection = true">选择学生</el-button>
                  <div class="selected-students" style="margin-top: 10px;">
                    <el-tag
                      v-for="student in selectedStudents"
                      :key="student.id"
                      closable
                      @close="removeStudent(student)"
                      size="small"
                      v-if="hasRole('admin')"
                    >
                      {{ student.name }} ({{ student.studentNumber }}) - {{ student.department }}
                    </el-tag>
                    <div v-else>
                      <p v-if="selectedStudents.length > 0">已选择 {{ selectedStudents.length }} 名学生</p>
                      <span v-else style="color: #909399; font-size: 14px;">暂无选择学生</span>
                    </div>
                  </div>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 教师选择对话框 -->
    <el-dialog title="选择授课老师" v-model="showTeacherSelection" width="800px">
      <div class="teacher-selection">
        <div class="search-bar">
          <h4 style="margin: 0;">可选教师列表</h4>
          <div>
            <el-input 
              v-model="teacherFilter" 
              placeholder="搜索教师姓名或教师号" 
              clearable
              @input="filterTeachers"
              style="width: 200px; margin-right: 10px;">
            </el-input>
            <el-button type="primary" @click="selectAllTeachers">全选</el-button>
          </div>
        </div>
        <el-table 
          :data="availableTeachers" 
          style="width: 100%" 
          height="400px"
          border
          @row-click="addTeacher">
          <el-table-column prop="teacherNumber" label="教师号" width="120"></el-table-column>
          <el-table-column prop="name" label="姓名" width="100"></el-table-column>
          <el-table-column prop="gender" label="性别" width="80"></el-table-column>
          <el-table-column prop="title" label="职称" width="100"></el-table-column>
          <el-table-column prop="department" label="院系" width="150"></el-table-column>
        </el-table>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showTeacherSelection = false">取消</el-button>
          <el-button type="primary" @click="saveTeacherSelection">确定</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 学生选择对话框 -->
    <el-dialog title="选择选课学生" v-model="showStudentSelection" width="800px">
      <div class="student-selection">
        <div class="search-bar">
          <h4 style="margin: 0;">可选学生列表</h4>
          <div>
            <el-select v-model="studentSearchField" placeholder="搜索字段" style="width: 100px; margin-right: 10px;">
              <el-option label="学号" value="studentNumber"></el-option>
              <el-option label="姓名" value="name"></el-option>
              <el-option label="班级" value="className"></el-option>
              <el-option label="院系" value="department"></el-option>
            </el-select>
            <el-input 
              v-model="studentFilter" 
              placeholder="搜索内容" 
              clearable
              @input="filterStudents"
              style="width: 200px; margin-right: 10px;">
            </el-input>
            <el-button type="primary" @click="filterStudents">搜索</el-button>
            <el-button type="primary" @click="selectAllStudents">全选</el-button>
          </div>
        </div>
        <el-table 
          :data="availableStudents" 
          style="width: 100%" 
          height="400px"
          border
          @row-click="addStudent">
          <el-table-column prop="studentNumber" label="学号" width="120"></el-table-column>
          <el-table-column prop="name" label="姓名" width="100"></el-table-column>
          <el-table-column prop="gender" label="性别" width="80"></el-table-column>
          <el-table-column prop="className" label="班级" width="150"></el-table-column>
          <el-table-column prop="department" label="院系" width="150"></el-table-column>
        </el-table>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showStudentSelection = false">取消</el-button>
          <el-button type="primary" @click="saveStudentSelection">确定</el-button>
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
import { hasRole } from '../utils/role'

// 检查用户是否是管理员或教师
const isAdminOrTeacher = computed(() => {
  return hasRole('admin') || hasRole('teacher')
})

// 检查用户是否是管理员
const isAdmin = computed(() => {
  return hasRole('admin')
})

const courses = ref([])// 院系列表
const departments = ref([
  '人文学院',
  '文学院',
  '历史学院',
  '哲学学院',
  '法学院',
  '经济学院',
  '管理学院',
  '外国语学院',
  '教育学院',
  '音乐学院',
  '美术学院',
  '体育学院',
  '数学与统计学院',
  '物理学院',
  '化学学院',
  '生命科学学院',
  '地理科学学院',
  '计算机学院',
  '电子工程学院',
  '信息工程学院',
  '自动化学院',
  '机械工程学院',
  '材料科学与工程学院',
  '土木工程学院',
  '建筑学院',
  '环境科学与工程学院',
  '交通学院',
  '能源与动力工程学院',
  '航空航天学院',
  '海洋学院',
  '医学院',
  '药学院',
  '公共卫生学院',
  '口腔医学院',
  '护理学院',
  '国际教育学院',
  '继续教育学院',
  '马克思主义学院'
])
const allCourses = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const dialogVisible = ref(false)
const detailVisible = ref(false)
const selectedCourse = ref(null)
const selectedTeachers = ref([])
const selectedStudents = ref([])
const showTeacherSelection = ref(false)
const showStudentSelection = ref(false)

const searchForm = reactive({
  courseCode: '',
  courseName: '',
  teacherName: ''
})

const form = reactive({
  courseCode: '',
  courseName: '',
  credits: 0,
  hours: 0,
  department: '',
  teacherNumber: null,
  semester: '',
  year: new Date().getFullYear(),
  maxStudents: 50,
  currentStudents: 0,
  status: 1,
  description: ''
})

// 状态格式化函数
const statusFormatter = (row, column, cellValue) => {
  return cellValue === 1 ? '开设' : '关闭'
}

// 查看课程详情
const handleViewDetails = (course) => {
  selectedCourse.value = { ...course }
  // 初始化选择的教师和学生列表
  selectedStudents.value = []
  
  // 根据课程教师信息初始化selectedTeachers
  selectedTeachers.value = []
  if (course.teacherId) {
    // 尝试从已有教师列表中查找
    const existingTeacher = allTeachers.value.find(t => t.id === course.teacherId)
    if (existingTeacher) {
      selectedTeachers.value.push(existingTeacher)
    } else {
      // 如果没有找到，单独获取教师信息
      axios.get(`/api/teachers/${course.teacherId}`)
        .then(response => {
          selectedTeachers.value.push(response.data)
        })
        .catch(error => {
          console.error('获取教师信息失败:', error)
        })
    }
  }
  
  // 初始化选择的学生列表
  selectedStudents.value = []
  // 根据课程ID获取已选学生列表
  axios.get(`/api/courses/${course.id}/students`)
    .then(response => {
      selectedStudents.value = response.data || []
    })
    .catch(error => {
      console.error('获取课程学生列表失败:', error)
      // 显示详细的错误信息
      if (error.response) {
        ElMessage.error(`获取课程学生列表失败: ${error.response.status} - ${error.response.data?.message || '未知错误'}`)
      } else if (error.request) {
        ElMessage.error('获取课程学生列表失败: 未收到服务器响应，请检查网络连接')
      } else {
        ElMessage.error(`获取课程学生列表失败: ${error.message}`)
      }
    })
  
  detailVisible.value = true
}

// 获取所有课程用于气泡卡片显示
const getAllCourses = () => {
  axios.get('/api/courses/with-teacher')
    .then(response => {
      allCourses.value = response.data || []
    })
    .catch(error => {
      ElMessage.error('获取课程列表失败')
      console.error('Error fetching all courses:', error)
    })
}

// 页面初始化时获取数据
const initData = () => {
  getAllCourses()
}

// 教师相关数据
const allTeachers = ref([])
const availableTeachers = ref([])
const teacherFilter = ref('')

// 学生相关数据
const allStudents = ref([])
const availableStudents = ref([])
const studentFilter = ref('')
const studentSearchField = ref('studentId')





// 调用初始化函数
initData()

// 获取教师列表
const getAllTeachers = () => {
  axios.get('/api/teachers')
    .then(response => {
      // 后端返回的是IPage对象，教师列表在records属性中
      allTeachers.value = response.data.records || []
      // 筛选出未被选中的教师
      availableTeachers.value = allTeachers.value.filter(teacher => 
        !selectedTeachers.value.some(t => t.id === teacher.id)
      )
    })
    .catch(error => {
      ElMessage.error('获取教师列表失败')
      console.error('Error fetching all teachers:', error)
    })
}

// 获取学生列表
const getAllStudents = () => {
  axios.get('/api/students')
    .then(response => {
      // 后端返回的是IPage对象，学生列表在records属性中
      allStudents.value = response.data.records || []
      // 筛选出未被选中的学生
      availableStudents.value = allStudents.value.filter(student => 
        !selectedStudents.value.some(s => s.id === student.id)
      )
    })
    .catch(error => {
      ElMessage.error('获取学生列表失败')
      console.error('Error fetching all students:', error)
    })
}

// 教师筛选
const filterTeachers = () => {
  availableTeachers.value = allTeachers.value.filter(teacher => 
    !selectedTeachers.value.some(t => t.id === teacher.id) && 
    (teacher.name.toLowerCase().includes(teacherFilter.value.toLowerCase()) ||
     teacher.teacherNumber.toLowerCase().includes(teacherFilter.value.toLowerCase()))
  )
}

// 学生筛选
const filterStudents = () => {
  availableStudents.value = allStudents.value.filter(student => {
    const isNotSelected = !selectedStudents.value.some(s => s.id === student.id)
    const filterValue = studentFilter.value.toLowerCase()
    
    // 确保student对象存在且有相关属性
    if (!student) return false
    
    // 根据选择的搜索字段进行搜索
    switch (studentSearchField.value) {
      case 'studentNumber':
        return isNotSelected && (student.studentNumber || '').toLowerCase().includes(filterValue)
      case 'name':
        return isNotSelected && (student.name || '').toLowerCase().includes(filterValue)
      case 'className':
        return isNotSelected && (student.className || '').toLowerCase().includes(filterValue)
      case 'department':
        return isNotSelected && (student.department || '').toLowerCase().includes(filterValue)
      default:
        return isNotSelected && ((student.name || '').toLowerCase().includes(filterValue) || (student.studentNumber || '').toLowerCase().includes(filterValue))
    }
  })
}

// 全选教师
const selectAllTeachers = () => {
  availableTeachers.value.forEach(teacher => {
    if (!selectedTeachers.value.some(t => t.id === teacher.id)) {
      selectedTeachers.value.push(teacher)
    }
  })
  // 更新可用教师列表
  filterTeachers()
}



// 全选学生
const selectAllStudents = () => {
  availableStudents.value.forEach(student => {
    if (!selectedStudents.value.some(s => s.id === student.id)) {
      selectedStudents.value.push(student)
    }
  })
  // 更新可用学生列表
  filterStudents()
}

// 添加教师
const addTeacher = (teacher) => {
  if (!selectedTeachers.value.some(t => t.id === teacher.id)) {
    selectedTeachers.value.push(teacher)
    // 更新可用教师列表
    filterTeachers()
  }
}

// 添加学生
const addStudent = (student) => {
  if (!selectedStudents.value.some(s => s.id === student.id)) {
    selectedStudents.value.push(student)
    // 更新可用学生列表
    filterStudents()
  }
}

// 移除教师
const removeTeacher = (teacher) => {
  const index = selectedTeachers.value.findIndex(item => item.id === teacher.id)
  if (index > -1) {
    selectedTeachers.value.splice(index, 1)
    // 更新可用教师列表
    if (showTeacherSelection.value) {
      filterTeachers()
    }
    // 如果是编辑状态，更新表单数据
    if (selectedCourse.value) {
      updateCourseTeacher()
    }
  }
}

// 保存教师选择
const saveTeacherSelection = () => {
  showTeacherSelection.value = false
  if (selectedCourse.value) {
    updateCourseTeacher()
  }
}

// 更新课程教师信息
const updateCourseTeacher = () => {
  if (selectedTeachers.value.length > 0) {
    // 当前只支持选择一个教师
    const teacher = selectedTeachers.value[0]
    selectedCourse.value.teacherId = teacher.id
    selectedCourse.value.teacherName = teacher.name
    
    // 调用后端更新课程教师信息
    axios.put(`/api/courses/${selectedCourse.value.id}`, {
      ...selectedCourse.value
    }).then(response => {
      ElMessage.success('教师选择成功')
      getCourses()
    }).catch(error => {
      ElMessage.error('更新教师信息失败')
      console.error('Error updating course teacher:', error)
    })
  } else {
    // 移除教师
    selectedCourse.value.teacherId = null
    selectedCourse.value.teacherName = null
    
    // 调用后端更新课程教师信息
    axios.put(`/api/courses/${selectedCourse.value.id}`, {
      ...selectedCourse.value
    }).then(response => {
      ElMessage.success('已移除教师')
      getCourses()
    }).catch(error => {
      ElMessage.error('更新教师信息失败')
      console.error('Error updating course teacher:', error)
    })
  }
}

// 移除学生
const removeStudent = (student) => {
  const index = selectedStudents.value.findIndex(item => item.id === student.id)
  if (index > -1) {
    selectedStudents.value.splice(index, 1)
    // 更新可用学生列表
    if (showStudentSelection.value) {
      filterStudents()
    }
    // 如果是编辑状态，更新课程学生关系
    if (selectedCourse.value) {
      updateCourseStudents()
    }
  }
}

// 保存学生选择
const saveStudentSelection = () => {
  showStudentSelection.value = false
  if (selectedCourse.value) {
    updateCourseStudents()
  }
}

// 更新课程学生信息
const updateCourseStudents = () => {
  if (selectedCourse.value) {
    // 准备要保存的学生ID列表
    const studentIds = selectedStudents.value.map(student => student.id)
    
    // 调用后端更新课程学生关系
  axios.put(`/api/courses/${selectedCourse.value.id}/students`, {
    studentIds: studentIds
  }).then(response => {
    ElMessage.success('学生选择成功')
    getCourses()
  }).catch(error => {
    console.error('Error updating course students:', error)
    // 显示详细的错误信息
    if (error.response) {
      ElMessage.error(`更新学生信息失败: ${error.response.status} - ${error.response.data?.message || '未知错误'}`)
    } else if (error.request) {
      ElMessage.error('更新学生信息失败: 未收到服务器响应，请检查网络连接')
    } else {
      ElMessage.error(`更新学生信息失败: ${error.message}`)
    }
  })
  }
}

// 获取课程列表
const getCourses = () => {
  axios.get('/api/courses/page', {
    params: {
      current: currentPage.value,
      size: pageSize.value,
      courseCode: searchForm.courseCode,
      courseName: searchForm.courseName,
      teacherName: searchForm.teacherName
    }
  }).then(response => {
    console.log('课程列表响应数据:', response.data)
    courses.value = response.data.records || []
    total.value = response.data.total || 0
  }).catch(error => {
    ElMessage.error('获取课程列表失败')
    console.error('Error fetching courses:', error)
  })
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  getCourses()
}

// 重置表单
const resetForm = () => {
  searchForm.courseCode = ''
  searchForm.courseName = ''
  searchForm.teacherName = ''
  currentPage.value = 1
  getCourses()
}

// 重置课程表单数据
const resetCourseForm = () => {
  form.courseCode = ''
  form.courseName = ''
  form.credits = 0
  form.hours = 0
  form.teacherNumber = null
  form.teacherId = null
  form.semester = ''
  form.year = new Date().getFullYear()
  form.maxStudents = 50
  form.currentStudents = 0
  form.status = 1
  form.description = ''
  // 移除id属性
  delete form.id
}

// 新增课程
const handleAdd = () => {
  resetCourseForm()
  dialogVisible.value = true
}

// 分页处理
const handleSizeChange = (size) => {
  pageSize.value = size
  getCourses()
}

const handleCurrentChange = (current) => {
  currentPage.value = current
  getCourses()
}

// 新增/编辑课程
const handleSubmit = () => {
  // 表单验证
  if (!form.courseCode || !form.courseName || !form.department) {
    ElMessage.warning('请填写完整的课程信息，包括课程代码、课程名称和所属院系')
    return
  }
  
  // 清除可能存在的teacherId
  delete form.teacherId
  
  // 如果提供了教师工号，需要先获取教师ID
  if (form.teacherNumber) {
    // 根据教师工号获取教师信息
    axios.get(`/api/teachers/number/${form.teacherNumber}`)
      .then(teacherResponse => {
        if (teacherResponse.data) {
          // 将教师ID添加到表单中
          form.teacherId = teacherResponse.data.id
          // 移除教师工号字段，因为后端不需要
          delete form.teacherNumber
          
          // 执行保存操作
          saveCourse()
        } else {
          ElMessage.error('未找到该教师工号')
        }
      })
      .catch(error => {
        console.error('Error fetching teacher by number:', error)
        // 添加更严格的空值检查
        if (error && error.response) {
          if (error.response.status === 403) {
            ElMessage.error('您没有权限获取教师信息')
          } else if (error.response.status === 404) {
            ElMessage.error('未找到该教师工号')
          } else {
            ElMessage.error(`获取教师信息失败：${error.response.status}`)
          }
        } else if (error && error.message) {
          ElMessage.error(`获取教师信息失败：${error.message}`)
        } else {
          ElMessage.error('获取教师信息失败，请检查网络连接或服务器状态')
        }
      })
  } else {
    // 没有教师工号，设置teacherId为null
    form.teacherId = null
    // 移除教师工号字段，因为后端不需要
    delete form.teacherNumber
    saveCourse()
  }
}

// 保存课程的公共函数
const saveCourse = () => {
  if (form.id) {
    // 编辑课程
    axios.put(`/api/courses/${form.id}`, form)
      .then(response => {
        ElMessage.success('编辑课程成功')
        dialogVisible.value = false
        getCourses()
      })
      .catch(error => {
        ElMessage.error('编辑课程失败')
        console.error('Error updating course:', error)
      })
  } else {
    // 新增课程
    console.log('提交的表单数据:', form)
    console.log('表单中的department字段:', form.department)
    console.log('department字段类型:', typeof form.department)
    console.log('department字段是否为空:', !form.department)
    axios.post('/api/courses', form)
      .then(response => {
        console.log('新增课程响应:', response)
        ElMessage.success('新增课程成功')
        dialogVisible.value = false
        getCourses()
      })
      .catch(error => {
        ElMessage.error('新增课程失败')
        console.error('Error creating course:', error)
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

// 编辑课程
const handleEdit = (row) => {
  // 先重置表单
  resetCourseForm()
  // 再赋值编辑数据
  Object.assign(form, row)
  dialogVisible.value = true
}

// 删除课程
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除课程 ${row.courseName} 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    axios.delete(`/api/courses/${row.id}`)
      .then(response => {
        ElMessage.success('删除课程成功')
        getCourses()
      })
      .catch(error => {
        ElMessage.error('删除课程失败')
        console.error('Error deleting course:', error)
      })
  }).catch(() => {
    // 取消删除
  })
}

// 初始化数据
getCourses()
getAllCourses()
</script>

<style scoped>
.course-container {
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

/* 课程气泡卡片样式 */
.course-bubbles {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.course-bubble {
  width: 300px;
  min-height: 250px;
  border-radius: 10px;
  transition: all 0.3s ease;
}

.course-bubble:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.bubble-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 10px;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 10px;
}

.course-code {
  font-weight: bold;
  color: #409eff;
  font-size: 14px;
}

.course-name {
  font-weight: bold;
  font-size: 16px;
}

.bubble-content {
  margin-bottom: 15px;
}

.bubble-item {
  margin-bottom: 8px;
  display: flex;
  align-items: center;
}

.bubble-label {
  font-size: 14px;
  color: #606266;
  width: 80px;
}

.bubble-value {
  font-size: 14px;
  color: #303133;
}

.bubble-description {
  display: block;
}

.bubble-description .bubble-label {
  display: block;
  margin-bottom: 5px;
}

.bubble-description .bubble-value {
  display: block;
  word-break: break-all;
}

.bubble-footer {
  text-align: right;
}

/* 课程详情样式 */
.course-detail {
  padding: 10px 0;
}

.detail-card {
  margin-bottom: 20px;
}

.detail-header {
  font-weight: bold;
  font-size: 16px;
  color: #303133;
}

.detail-content {
  padding: 10px 0;
}

.detail-item {
  margin-bottom: 12px;
  display: flex;
  align-items: center;
}

.detail-label {
  width: 100px;
  font-size: 14px;
  color: #606266;
}

.detail-value {
  font-size: 14px;
  color: #303133;
}

/* 选择教师和学生样式 */
.teacher-selection,
.student-selection {
  margin-bottom: 15px;
}

.selected-teachers,
.selected-students {
  margin-top: 10px;
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
}

.search-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}
</style>
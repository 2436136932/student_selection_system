<template>
  <div class="teacher-container">
    <h2>教师管理</h2>
    
    <!-- 搜索表单 -->
    <div class="search-form">
      <el-form :inline="true" :model="searchForm" class="demo-form-inline">
        <el-form-item label="工号">
          <el-input v-model="searchForm.teacherNumber" placeholder="教师工号" clearable></el-input>
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="searchForm.name" placeholder="教师姓名" clearable></el-input>
        </el-form-item>
        <el-form-item label="部门">
          <el-input v-model="searchForm.department" placeholder="所在部门" clearable></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 新增按钮 -->
    <div class="button-group">
      <el-button v-if="hasRole('admin')" type="primary" @click="dialogVisible = true">新增教师</el-button>
    </div>

    <!-- 数据表格 -->
    <el-table :data="teachers" style="width: 100%" stripe>
      <el-table-column prop="id" label="ID" width="80"></el-table-column>
      <el-table-column prop="teacherNumber" label="工号" width="120"></el-table-column>
      <el-table-column prop="name" label="姓名" width="100"></el-table-column>
      <el-table-column prop="gender" label="性别" width="80"></el-table-column>
      <el-table-column prop="title" label="职称" width="100"></el-table-column>
      <el-table-column prop="department" label="部门" width="150"></el-table-column>
      <el-table-column prop="phone" label="电话" width="120"></el-table-column>
      <el-table-column prop="email" label="邮箱" width="150"></el-table-column>
      <el-table-column label="操作" width="150">
        <template #default="scope">
          <el-button v-if="hasRole('admin')" type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button v-if="hasRole('admin')" type="danger" size="small" @click="handleDelete(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total">
      </el-pagination>
    </div>

    <!-- 对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="教师信息"
      width="50%">
      <el-form :model="teacher" :rules="rules" ref="teacherFormRef" label-width="100px">
        <el-form-item label="工号" prop="teacherNumber">
          <el-input v-model="teacher.teacherNumber" placeholder="请输入教师工号"></el-input>
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="teacher.name" placeholder="请输入教师姓名" @change="findUserByTeacherName"></el-input>
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="teacher.gender">
            <el-radio label="男">男</el-radio>
            <el-radio label="女">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="职称" prop="title">
          <el-input v-model="teacher.title" placeholder="请输入职称"></el-input>
        </el-form-item>
        <el-form-item label="部门" prop="department">
          <el-select v-model="teacher.department" placeholder="请选择所在部门">
            <el-option v-for="dept in departments" :key="dept" :label="dept" :value="dept"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="teacher.phone" placeholder="请输入电话"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="teacher.email" placeholder="请输入邮箱"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSave">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'
import { hasRole, getUserInfo } from '../utils/role'

// 响应式数据
const teachers = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const dialogVisible = ref(false)
const teacherFormRef = ref()

// 部门列表数据
const departments = ref([
  '人文学院', '文学院', '历史学院', '哲学学院', '法学院', 
  '经济学院', '管理学院', '外国语学院', '教育学院', '音乐学院',
  '美术学院', '体育学院', '数学与统计学院', '物理学院', '化学学院',
  '生命科学学院', '地理科学学院', '计算机学院', '电子工程学院', '机械工程学院',
  '材料科学与工程学院', '建筑学院', '环境科学与工程学院', '交通学院', '航空航天学院',
  '医学院', '药学院', '公共卫生学院', '口腔医学院', '护理学院',
  '马克思主义学院'
])

const searchForm = reactive({
  teacherNumber: '',
  name: '',
  department: ''
})

const teacher = reactive({
  id: null,
  teacherNumber: '',
  name: '',
  gender: '男',
  title: '',
  department: '',
  phone: '',
  email: '',
  userId: null
})

const rules = {
  teacherNumber: [
    { required: true, message: '请输入教师工号', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入教师姓名', trigger: 'blur' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  title: [
    { required: true, message: '请输入职称', trigger: 'blur' }
  ],
  department: [
    { required: true, message: '请输入所在部门', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入电话', trigger: 'blur' },
    { pattern: /^1[3456789]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ]
}

// 生命周期钩子
onMounted(() => {
  getTeachers()
})

// 方法
const getTeachers = () => {
  axios.get('/api/teachers', {
    params: {
      page: currentPage.value,
      size: pageSize.value,
      teacherNumber: searchForm.teacherNumber,
      name: searchForm.name,
      department: searchForm.department
    }
  }).then(response => {
    teachers.value = response.data.records
    total.value = response.data.total
  }).catch(error => {
    console.error('获取教师列表失败:', error)
    ElMessage.error('获取教师列表失败')
  })
}

const handleSearch = () => {
  currentPage.value = 1
  getTeachers()
}

const handleReset = () => {
  searchForm.teacherNumber = ''
  searchForm.name = ''
  searchForm.department = ''
  currentPage.value = 1
  getTeachers()
}

const handleSizeChange = (val) => {
  pageSize.value = val
  getTeachers()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  getTeachers()
}

const handleEdit = (row) => {
  // 深拷贝，避免直接修改表格数据
  Object.assign(teacher, JSON.parse(JSON.stringify(row)))
  dialogVisible.value = true
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除该教师吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    axios.delete(`/api/teachers/${id}`).then(() => {
      ElMessage.success('删除成功')
      getTeachers()
    }).catch(error => {
      console.error('删除教师失败:', error)
      ElMessage.error('删除教师失败')
    })
  }).catch(() => {
    ElMessage.info('已取消删除')
  })
}

// 根据姓名查找用户信息
const findUserByTeacherName = async () => {
  console.log('开始查找用户信息，姓名:', teacher.name)
  if (!teacher.name) {
    console.log('姓名为空，不执行查找')
    return
  }
  
  try {
    // 尝试根据姓名（真实姓名）查找用户
    const url = `/api/users/realname/${encodeURIComponent(teacher.name)}`
    console.log('请求URL:', url)
    const response = await axios.get(url)
    console.log('用户查询响应:', response)
    if (response.data) {
      // 自动填充用户信息
      teacher.phone = response.data.phone || ''
      teacher.email = response.data.email || ''
      teacher.userId = response.data.id
      console.log('自动填充后的表单数据:', teacher)
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

const handleSave = () => {
  console.log('开始保存教师信息');
  console.log('表单数据:', JSON.stringify(teacher, null, 2));
  
  teacherFormRef.value.validate((valid) => {
    if (valid) {
      console.log('表单验证通过');
      
      if (teacher.id) {
        // 编辑
        console.log('执行编辑操作');
        axios.put(`/api/teachers/${teacher.id}`, teacher).then(() => {
          ElMessage.success('更新成功')
          dialogVisible.value = false
          getTeachers()
        }).catch(error => {
          console.error('更新教师失败:', error)
          console.error('错误响应:', error.response)
          ElMessage.error('更新教师失败')
        })
      } else {
        // 新增
        console.log('执行新增操作');
        console.log('当前用户信息:', JSON.stringify(getUserInfo(), null, 2));
        console.log('当前用户角色:', getUserInfo().role);
        console.log('是否为管理员:', hasRole('admin'));
        console.log('是否为admin角色(大写):', hasRole('ADMIN'));
        
        if (!hasRole('admin')) {
          console.error('用户没有管理员权限，新增教师操作被禁止');
          ElMessage.error('权限不足：您不是管理员，无法新增教师');
          return;
        }
        
        console.log('准备调用新增教师API');
        axios.post('/api/teachers', teacher)
        .then(response => {
          console.log('新增教师API调用成功:', response);
          ElMessage.success('新增成功')
          dialogVisible.value = false
          getTeachers()
        })
        .catch(error => {
          console.error('新增教师API调用失败:', error);
          console.error('错误响应状态:', error.response ? error.response.status : '无响应');
          console.error('错误响应数据:', error.response ? error.response.data : '无数据');
          console.error('错误配置:', error.config);
          console.error('用户角色:', getUserInfo().role);
          
          // 显示更详细的错误信息
          let errorMsg = '新增教师失败'
          if (error.response) {
            if (error.response.status === 403) {
              errorMsg = '权限不足：您没有权限执行此操作';
              console.error('403错误 - 权限不足');
            } else if (error.response.status === 500) {
              errorMsg = '服务器内部错误，请稍后重试';
              console.error('500错误 - 服务器内部错误');
            } else if (error.response.data && error.response.data.message) {
              errorMsg += `: ${error.response.data.message}`;
            } else {
              errorMsg += `: HTTP ${error.response.status}`;
            }
          } else if (error.request) {
            errorMsg = '网络错误：服务器无响应';
            console.error('网络错误 - 服务器无响应');
          } else {
            errorMsg += `: ${error.message}`;
          }
          
          ElMessage.error(errorMsg);
        })
      }
    } else {
      console.log('表单验证失败');
    }
  })
}
</script>

<style scoped>
.teacher-container {
  padding: 20px;
}

.search-form {
  margin-bottom: 20px;
}

.button-group {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}
</style>
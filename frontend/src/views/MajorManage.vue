<template>
  <div class="major-container">
    <h2>专业管理</h2>
    
    <!-- 专业管理卡片 -->
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>专业列表</span>
          <div style="display: flex; gap: 10px;">
            <el-select v-model="majorSearchDepartment" placeholder="按院系筛选" style="width: 150px;">
              <el-option label="全部院系" value=""></el-option>
              <el-option
                v-for="dept in departments"
                :key="dept"
                :label="dept"
                :value="dept"
              ></el-option>
            </el-select>
            <el-button size="small" type="primary" @click="getAllMajors">
              搜索
            </el-button>
            <el-button v-if="hasRole('admin')" size="small" type="primary" @click="showAddMajorDialog">
              添加专业
            </el-button>
          </div>
        </div>
      </template>
      <div class="card-body">
        <el-table :data="currentMajors" style="width: 100%" stripe>
          <el-table-column prop="id" label="专业ID" width="80"></el-table-column>
          <el-table-column prop="name" label="专业名称" width="150"></el-table-column>
          <el-table-column prop="department" label="所属院系" width="150"></el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="180" :formatter="dateFormatter"></el-table-column>
          <el-table-column label="操作" width="250" fixed="right">
            <template #default="scope">
              <el-button v-if="hasRole('admin')" size="small" type="primary" @click="showDepartmentAssignmentDialog(scope.row)" style="margin-right: 10px;">
                分配院系
              </el-button>
              <el-button v-if="hasRole('admin')" size="small" type="danger" @click="handleDeleteMajor(scope.row)">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <div style="margin-top: 20px; display: flex; justify-content: flex-end;">
          <el-pagination
            @size-change="handleMajorSizeChange"
            @current-change="handleMajorCurrentChange"
            :current-page="majorCurrentPage"
            :page-sizes="[10, 20, 50, 100]"
            :page-size="majorPageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="majorsTotal"
          ></el-pagination>
        </div>
      </div>
    </el-card>

    <!-- 专业分配对话框 -->
    <el-dialog title="分配院系" v-model="isDepartmentAssignmentVisible" width="500px">
      <div class="department-assignment">
        <el-form :model="departmentForm" label-width="100px">
          <el-form-item label="专业名称">
            <el-input v-model="departmentForm.majorName" disabled></el-input>
          </el-form-item>
          <el-form-item label="所属院系">
            <el-select v-model="departmentForm.department" placeholder="请选择院系" style="width: 100%">
              <el-option
                v-for="dept in departments"
                :key="dept"
                :label="dept"
                :value="dept">
              </el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="isDepartmentAssignmentVisible = false">取消</el-button>
          <el-button type="primary" @click="saveDepartmentAssignment">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 添加专业对话框 -->
    <el-dialog
      v-model="isAddMajorVisible"
      title="添加专业"
      width="500px"
    >
      <el-form :model="majorForm" label-width="100px">
        <el-form-item label="专业名称">
          <el-input v-model="majorForm.name" placeholder="请输入专业名称"></el-input>
        </el-form-item>
        <el-form-item label="所属院系">
          <el-select v-model="majorForm.department" placeholder="请选择院系">
            <el-option
              v-for="dept in departments"
              :key="dept"
              :label="dept"
              :value="dept"
            >
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="isAddMajorVisible = false">取消</el-button>
          <el-button type="primary" @click="handleAddMajor">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'
import { hasRole } from '../utils/role'

// 专业相关数据
const majors = ref([])
const currentMajors = ref([])
const majorCurrentPage = ref(1)
const majorPageSize = ref(10)
const majorsTotal = ref(0)
const majorSearchDepartment = ref('')
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
const isDepartmentAssignmentVisible = ref(false)
const isAddMajorVisible = ref(false)
const currentMajor = ref(null)

const departmentForm = reactive({
  majorName: '',
  department: ''
})

const majorForm = reactive({
  name: '',
  department: ''
})

// 日期格式化函数
const dateFormatter = (row, column, cellValue) => {
  if (!cellValue) return ''
  const date = new Date(cellValue)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

// 获取所有专业
const getAllMajors = () => {
  const params = {
    page: majorCurrentPage.value,
    size: majorPageSize.value
  }
  if (majorSearchDepartment.value) {
    params.department = majorSearchDepartment.value
  }
  
  axios.get('/api/majors/search', { params })
    .then(response => {
      majors.value = response.data.records || []
      currentMajors.value = majors.value
      majorsTotal.value = response.data.total || 0
    })
    .catch(error => {
      ElMessage.error('获取专业列表失败')
      console.error('Error fetching all majors:', error)
    })
}

// 处理专业分页大小变化
const handleMajorSizeChange = (size) => {
  majorPageSize.value = size
  getAllMajors()
}

// 处理专业当前页变化
const handleMajorCurrentChange = (current) => {
  majorCurrentPage.value = current
  getAllMajors()
}

// 显示添加专业对话框
const showAddMajorDialog = () => {
  // 重置表单
  majorForm.name = ''
  majorForm.department = ''
  isAddMajorVisible.value = true
}

// 处理添加专业
const handleAddMajor = () => {
  // 表单验证
  if (!majorForm.name || !majorForm.department) {
    ElMessage.warning('请填写完整的专业信息')
    return
  }
  
  // 调用API添加专业
  axios.post('/api/majors', majorForm)
    .then(response => {
      ElMessage.success('专业添加成功')
      isAddMajorVisible.value = false
      // 重新获取专业列表
      getAllMajors()
    })
    .catch(error => {
      ElMessage.error('添加专业失败')
      console.error('Error adding major:', error)
    })
}

// 显示院系分配对话框
const showDepartmentAssignmentDialog = (major) => {
  currentMajor.value = major
  departmentForm.majorName = major.name
  departmentForm.department = major.department
  isDepartmentAssignmentVisible.value = true
}

// 保存院系分配
const saveDepartmentAssignment = () => {
  if (!departmentForm.department) {
    ElMessage.warning('请选择院系')
    return
  }

  axios.put(`/api/majors/id/${currentMajor.value.id}`, {
    ...currentMajor.value,
    department: departmentForm.department
  })
    .then(response => {
      ElMessage.success('院系分配成功')
      // 更新本地专业列表
      const index = majors.value.findIndex(m => m.id === currentMajor.value.id)
      if (index !== -1) {
        majors.value[index].department = departmentForm.department
      }
      isDepartmentAssignmentVisible.value = false
    })
    .catch(error => {
      ElMessage.error('院系分配失败')
      console.error('Error saving department assignment:', error)
    })
}

// 删除专业
const handleDeleteMajor = (row) => {
  ElMessageBox.confirm(`确定要删除专业 ${row.name} 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    axios.delete(`/api/majors/id/${row.id}`)
      .then(response => {
        ElMessage.success('删除专业成功')
        // 重新获取专业列表
        getAllMajors()
      })
      .catch(error => {
        ElMessage.error('删除专业失败')
        console.error('Error deleting major:', error)
      })
  }).catch(() => {
    // 取消删除
  })
}

// 页面加载时获取专业列表
onMounted(() => {
  getAllMajors()
})
</script>

<style scoped>
.major-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-body {
  padding: 20px 0;
}
</style>
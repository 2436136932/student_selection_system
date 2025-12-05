<template>
  <div class="award-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>奖项管理</span>
        </div>
      </template>
      <el-tabs v-model="activeTab" type="card">
        <el-tab-pane label="奖项类型管理" name="awards">
          <div class="card-header" style="margin-top: 10px;">
            <el-button v-if="hasRole('admin')" type="primary" @click="dialogVisible = true">
              <el-icon><plus /></el-icon> 新增奖项类型
            </el-button>
          </div>
          <div class="card-body">
            <el-form :model="searchForm" :inline="true" class="search-form">
              <el-form-item label="奖项名称">
                <el-input v-model="searchForm.awardName" placeholder="请输入奖项名称"></el-input>
              </el-form-item>
              <el-form-item label="奖项类型">
                <el-input v-model="searchForm.awardType" placeholder="请输入奖项类型"></el-input>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleSearch">搜索</el-button>
                <el-button @click="resetForm">重置</el-button>
              </el-form-item>
            </el-form>

            <el-table :data="awards" style="width: 100%" stripe v-loading="loading">
              <el-table-column prop="id" label="奖项ID" width="80"></el-table-column>
              <el-table-column prop="awardName" label="奖项名称" width="150"></el-table-column>
              <el-table-column prop="awardLevel" label="奖项级别" width="100"></el-table-column>
              <el-table-column prop="awardType" label="奖项类型" width="100"></el-table-column>
              <el-table-column prop="description" label="奖项描述" min-width="200"></el-table-column>
              <el-table-column prop="requirement" label="评奖要求" min-width="200"></el-table-column>
              <el-table-column label="操作" width="150" fixed="right" v-if="hasRole('admin')">
                <template #default="scope">
                  <el-button size="small" type="primary" @click="handleEdit(scope.row)">
                    <el-icon><edit /></el-icon> 编辑
                  </el-button>
                  <el-button size="small" type="danger" @click="handleDelete(scope.row)">
                    <el-icon><delete /></el-icon> 删除
                  </el-button>
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

            <div class="filter-section" style="margin-top: 20px">
              <el-card shadow="hover">
                <template #header>
                  <div class="card-header">
                    <span>学生筛选</span>
                  </div>
                </template>
                <div class="filter-body">
                  <el-form :model="filterForm" :inline="true" class="student-filter-form">
                    <el-form-item label="GPA大于">
                      <el-input-number v-model="filterForm.minGpa" :min="0" :max="100" :step="0.1" placeholder="请输入GPA"></el-input-number>
                    </el-form-item>
                    <el-form-item label="获奖次数大于">
                      <el-input-number v-model="filterForm.minAwardCount" :min="0" :step="1" placeholder="请输入次数"></el-input-number>
                    </el-form-item>
                    <el-form-item label="奖项级别">
                      <el-select v-model="filterForm.awardLevel" placeholder="请选择级别">
                        <el-option label="全部" value=""></el-option>
                        <el-option label="国家级" value="国家级"></el-option>
                        <el-option label="省级" value="省级"></el-option>
                        <el-option label="校级" value="校级"></el-option>
                        <el-option label="院级" value="院级"></el-option>
                      </el-select>
                    </el-form-item>
                    <el-form-item label="专业">
                      <el-select v-model="filterForm.majorId" placeholder="请选择专业">
                        <el-option label="全部" value=""></el-option>
                        <el-option v-for="major in majors" :key="major.id" :label="major.name" :value="major.id"></el-option>
                      </el-select>
                    </el-form-item>
                    <el-form-item label="年级">
                      <el-input-number v-model="filterForm.year" :min="2000" :max="new Date().getFullYear()" :step="1" placeholder="请输入年级"></el-input-number>
                    </el-form-item>
                    <el-form-item>
                      <el-button type="primary" @click="handleFilter">筛选</el-button>
                      <el-button @click="resetFilter">重置</el-button>
                    </el-form-item>
                  </el-form>
                  <div v-if="filteredStudents.length > 0" style="margin-top: 20px">
                    <el-table :data="filteredStudents" style="width: 100%" stripe>
                      <el-table-column prop="studentNumber" label="学生学号" width="120"></el-table-column>
                      <el-table-column prop="name" label="学生姓名" width="120"></el-table-column>
                      <el-table-column prop="gender" label="性别" width="80">
                        <template #default="scope">
                          {{ scope.row.gender === '男' ? '男' : '女' }}
                        </template>
                      </el-table-column>
                      <el-table-column prop="major" label="专业" width="150"></el-table-column>
                      <el-table-column prop="className" label="班级" width="150"></el-table-column>
                      <el-table-column prop="averageScore" label="平均成绩" width="120"></el-table-column>
                      <el-table-column prop="awardCount" label="获奖次数" width="120"></el-table-column>
                    </el-table>
                  </div>
                  <div v-else-if="filterApplied" class="no-data">
                    <el-empty description="暂无符合条件的学生" style="margin-top: 20px;"></el-empty>
                  </div>
                </div>
              </el-card>
            </div>
          </div>
        </el-tab-pane>
        <el-tab-pane label="评奖标准管理" name="criteria">
          <div class="card-header" style="margin-top: 10px;">
            <el-button v-if="hasRole('admin')" type="primary" @click="criteriaDialogVisible = true">
              <el-icon><plus /></el-icon> 新增评奖标准
            </el-button>
          </div>
          <div class="card-body">
            <el-form :model="criteriaSearchForm" :inline="true" class="search-form">
              <el-form-item label="奖项">
                <el-select v-model="criteriaSearchForm.awardId" placeholder="请选择奖项">
                  <el-option label="全部" value=""></el-option>
                  <el-option v-for="award in awards" :key="award.id" :label="award.awardName" :value="award.id"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="标准类型">
                <el-input v-model="criteriaSearchForm.criterionType" placeholder="请输入标准类型"></el-input>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleCriteriaSearch">搜索</el-button>
                <el-button @click="resetCriteriaForm">重置</el-button>
              </el-form-item>
            </el-form>

            <el-table :data="criteria" style="width: 100%" stripe v-loading="criteriaLoading">
              <el-table-column prop="id" label="标准ID" width="80"></el-table-column>
              <el-table-column prop="criterionName" label="标准名称" width="150"></el-table-column>
              <el-table-column prop="criterionType" label="标准类型" width="120"></el-table-column>
              <el-table-column prop="weight" label="权重" width="80"></el-table-column>
              <el-table-column prop="threshold" label="阈值" width="80"></el-table-column>
              <el-table-column prop="award.awardName" label="所属奖项" width="150"></el-table-column>
              <el-table-column prop="description" label="标准描述" min-width="200"></el-table-column>
              <el-table-column label="操作" width="150" fixed="right" v-if="hasRole('admin')">
                <template #default="scope">
                  <el-button size="small" type="primary" @click="handleCriteriaEdit(scope.row)">
                    <el-icon><edit /></el-icon> 编辑
                  </el-button>
                  <el-button size="small" type="danger" @click="handleCriteriaDelete(scope.row)">
                    <el-icon><delete /></el-icon> 删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>

            <div class="pagination">
              <el-pagination
                @size-change="handleCriteriaSizeChange"
                @current-change="handleCriteriaCurrentChange"
                :current-page="criteriaCurrentPage"
                :page-sizes="[10, 20, 50, 100]"
                :page-size="criteriaPageSize"
                layout="total, sizes, prev, pager, next, jumper"
                :total="criteriaTotal"
              ></el-pagination>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 新增/编辑奖项类型对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="奖项类型信息"
      width="500px"
    >
      <el-form :model="form" label-width="100px">
        <el-form-item v-if="form.id" label="奖项ID">
          <el-input v-model="form.id" placeholder="奖项ID" disabled></el-input>
        </el-form-item>
        <el-form-item label="奖项名称">
          <el-input v-model="form.awardName" placeholder="请输入奖项名称"></el-input>
        </el-form-item>
        <el-form-item label="奖项级别">
          <el-select v-model="form.awardLevel" placeholder="请选择奖项级别">
            <el-option label="国家级" value="国家级"></el-option>
            <el-option label="省级" value="省级"></el-option>
            <el-option label="校级" value="校级"></el-option>
            <el-option label="院级" value="院级"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="奖项类型">
          <el-input v-model="form.awardType" placeholder="请输入奖项类型"></el-input>
        </el-form-item>
        <el-form-item label="奖项描述">
          <el-input v-model="form.description" type="textarea" placeholder="请输入奖项描述" :rows="3"></el-input>
        </el-form-item>
        <el-form-item label="评奖要求">
          <el-input v-model="form.requirement" type="textarea" placeholder="请输入评奖要求" :rows="3"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 新增/编辑评奖标准对话框 -->
    <el-dialog
      v-model="criteriaDialogVisible"
      title="评奖标准信息"
      width="500px"
    >
      <el-form :model="criteriaForm" label-width="100px">
        <el-form-item v-if="criteriaForm.id" label="标准ID">
          <el-input v-model="criteriaForm.id" placeholder="标准ID" disabled></el-input>
        </el-form-item>
        <el-form-item label="标准名称">
          <el-input v-model="criteriaForm.criterionName" placeholder="请输入标准名称"></el-input>
        </el-form-item>
        <el-form-item label="标准类型">
          <el-input v-model="criteriaForm.criterionType" placeholder="请输入标准类型"></el-input>
        </el-form-item>
        <el-form-item label="权重">
          <el-input-number v-model="criteriaForm.weight" :min="0" :max="100" :step="0.1" placeholder="请输入权重"></el-input-number>
        </el-form-item>
        <el-form-item label="阈值">
          <el-input v-model="criteriaForm.threshold" placeholder="请输入阈值"></el-input>
        </el-form-item>
        <el-form-item label="所属奖项">
          <el-select v-model="criteriaForm.awardId" placeholder="请选择奖项">
            <el-option v-for="award in awards" :key="award.id" :label="award.awardName" :value="award.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="标准描述">
          <el-input v-model="criteriaForm.description" type="textarea" placeholder="请输入标准描述" :rows="3"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="criteriaDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleCriteriaSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { hasRole } from '../utils/role'
import axios from 'axios'

export default {
  name: 'AwardView',
  setup() {
    // 奖项类型管理
    const dialogVisible = ref(false)
    const loading = ref(false)
    const currentPage = ref(1)
    const pageSize = ref(10)
    const total = ref(0)
    const awards = ref([])
    const majors = ref([])
    const activeTab = ref('awards')
    const form = reactive({
      id: '',
      awardName: '',
      awardLevel: '',
      awardType: '',
      description: '',
      requirement: ''
    })
    const searchForm = reactive({
      awardName: '',
      awardType: ''
    })

    // 学生筛选
    const filterForm = reactive({
      minGpa: 0,
      minAwardCount: 0,
      awardLevel: '',
      majorId: '',
      year: 0
    })
    const filterApplied = ref(false)
    const filteredStudents = ref([])

    // 评奖标准管理
    const criteriaDialogVisible = ref(false)
    const criteriaLoading = ref(false)
    const criteriaCurrentPage = ref(1)
    const criteriaPageSize = ref(10)
    const criteriaTotal = ref(0)
    const criteria = ref([])
    const criteriaForm = reactive({
      id: '',
      criterionName: '',
      criterionType: '',
      weight: 0,
      threshold: '',
      awardId: '',
      description: ''
    })
    const criteriaSearchForm = reactive({
      awardId: '',
      criterionType: ''
    })

    // 获取奖项类型列表
    const getAwards = async () => {
      loading.value = true
      try {
        const response = await axios.get('/api/award-types', {
          params: {
            page: currentPage.value,
            size: pageSize.value,
            awardName: searchForm.awardName,
            awardType: searchForm.awardType
          }
        })
        awards.value = response.data.content
        total.value = response.data.totalElements
      } catch (error) {
        ElMessage.error('获取奖项类型列表失败')
        awards.value = []
      } finally {
        loading.value = false
      }
    }

    // 获取专业列表
    const getMajors = async () => {
      try {
        const response = await axios.get('/api/majors')
        majors.value = response.data
      } catch (error) {
        ElMessage.error('获取专业列表失败')
        majors.value = []
      }
    }

    // 获取评奖标准列表
    const getCriteria = async () => {
      criteriaLoading.value = true
      try {
        const response = await axios.get('/api/award-criteria', {
          params: {
            page: criteriaCurrentPage.value,
            size: criteriaPageSize.value,
            awardId: criteriaSearchForm.awardId,
            criterionType: criteriaSearchForm.criterionType
          }
        })
        criteria.value = response.data.content
        criteriaTotal.value = response.data.totalElements
      } catch (error) {
        ElMessage.error('获取评奖标准列表失败')
        criteria.value = []
      } finally {
        criteriaLoading.value = false
      }
    }

    // 新增/编辑奖项类型
    const handleSubmit = async () => {
      try {
        if (form.id) {
          // 编辑
          await axios.put(`/api/award-types/${form.id}`, form)
          ElMessage.success('奖项类型更新成功')
        } else {
          // 新增
          await axios.post('/api/award-types', form)
          ElMessage.success('奖项类型新增成功')
        }
        dialogVisible.value = false
        getAwards()
        resetForm()
      } catch (error) {
        ElMessage.error('操作失败，请重试')
      }
    }

    // 编辑奖项类型
    const handleEdit = (row) => {
      Object.assign(form, row)
      dialogVisible.value = true
    }

    // 删除奖项类型
    const handleDelete = async (row) => {
      try {
        await axios.delete(`/api/award-types/${row.id}`)
        ElMessage.success('奖项类型删除成功')
        getAwards()
      } catch (error) {
        ElMessage.error('删除失败，请重试')
      }
    }

    // 搜索奖项类型
    const handleSearch = () => {
      currentPage.value = 1
      getAwards()
    }

    // 重置搜索表单
    const resetForm = () => {
      searchForm.awardName = ''
      searchForm.awardType = ''
      currentPage.value = 1
      getAwards()
    }

    // 分页处理
    const handleSizeChange = (size) => {
      pageSize.value = size
      getAwards()
    }

    const handleCurrentChange = (current) => {
      currentPage.value = current
      getAwards()
    }

    // 新增/编辑评奖标准
    const handleCriteriaSubmit = async () => {
      try {
        if (criteriaForm.id) {
          // 编辑
          await axios.put(`/api/award-criteria/${criteriaForm.id}`, criteriaForm)
          ElMessage.success('评奖标准更新成功')
        } else {
          // 新增
          await axios.post('/api/award-criteria', criteriaForm)
          ElMessage.success('评奖标准新增成功')
        }
        criteriaDialogVisible.value = false
        getCriteria()
        resetCriteriaForm()
      } catch (error) {
        ElMessage.error('操作失败，请重试')
      }
    }

    // 编辑评奖标准
    const handleCriteriaEdit = (row) => {
      Object.assign(criteriaForm, row)
      criteriaDialogVisible.value = true
    }

    // 删除评奖标准
    const handleCriteriaDelete = async (row) => {
      try {
        await axios.delete(`/api/award-criteria/${row.id}`)
        ElMessage.success('评奖标准删除成功')
        getCriteria()
      } catch (error) {
        ElMessage.error('删除失败，请重试')
      }
    }

    // 搜索评奖标准
    const handleCriteriaSearch = () => {
      criteriaCurrentPage.value = 1
      getCriteria()
    }

    // 重置搜索表单
    const resetCriteriaForm = () => {
      criteriaSearchForm.awardId = ''
      criteriaSearchForm.criterionType = ''
      criteriaCurrentPage.value = 1
      getCriteria()
    }

    // 分页处理
    const handleCriteriaSizeChange = (size) => {
      criteriaPageSize.value = size
      getCriteria()
    }

    const handleCriteriaCurrentChange = (current) => {
      criteriaCurrentPage.value = current
      getCriteria()
    }

    // 学生筛选
    const handleFilter = async () => {
      try {
        const response = await axios.get('/api/students/filter', {
          params: {
            minGpa: filterForm.minGpa,
            minAwardCount: filterForm.minAwardCount,
            awardLevel: filterForm.awardLevel,
            majorId: filterForm.majorId,
            year: filterForm.year
          }
        })
        filteredStudents.value = response.data
        filterApplied.value = true
      } catch (error) {
        ElMessage.error('筛选失败，请重试')
        filteredStudents.value = []
        filterApplied.value = true
      }
    }

    // 重置筛选表单
    const resetFilter = () => {
      filterForm.minGpa = 0
      filterForm.minAwardCount = 0
      filterForm.awardLevel = ''
      filterForm.majorId = ''
      filterForm.year = 0
      filteredStudents.value = []
      filterApplied.value = false
    }

    // 页面加载时获取数据
    onMounted(() => {
      getAwards()
      getMajors()
      getCriteria()
    })

    return {
      // 奖项类型管理
      dialogVisible,
      loading,
      currentPage,
      pageSize,
      total,
      awards,
      form,
      searchForm,
      activeTab,
      majors,

      // 学生筛选
      filterForm,
      filterApplied,
      filteredStudents,

      // 评奖标准管理
      criteriaDialogVisible,
      criteriaLoading,
      criteriaCurrentPage,
      criteriaPageSize,
      criteriaTotal,
      criteria,
      criteriaForm,
      criteriaSearchForm,

      // 角色判断
      hasRole,

      // 奖项类型管理方法
      getAwards,
      handleSubmit,
      handleEdit,
      handleDelete,
      handleSearch,
      resetForm,
      handleSizeChange,
      handleCurrentChange,

      // 评奖标准管理方法
      handleCriteriaSubmit,
      handleCriteriaEdit,
      handleCriteriaDelete,
      handleCriteriaSearch,
      resetCriteriaForm,
      handleCriteriaSizeChange,
      handleCriteriaCurrentChange,

      // 学生筛选方法
      handleFilter,
      resetFilter
    }
  }
}
</script>

<style scoped>
.award-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.no-data {
  text-align: center;
  padding: 20px;
  color: #909399;
}

.filter-section {
  margin-top: 30px;
}

.student-filter-form {
  margin-bottom: 20px;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

@media (max-width: 768px) {
  .award-container {
    padding: 10px;
  }
  
  .search-form {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>
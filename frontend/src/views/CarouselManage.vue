<template>
  <div class="carousel-manage-container">
    <h2>轮播图管理</h2>
    
    <div class="header-actions">
      <div class="search-container">
        <el-select v-model="searchCondition" placeholder="请选择搜索条件" style="width: 150px; margin-right: 10px;">
          <el-option label="标题" value="title" />
          <el-option label="描述" value="description" />
          <el-option label="状态" value="status" />
        </el-select>
        <el-input v-model="searchKeyword" placeholder="请输入搜索关键词" style="width: 200px; margin-right: 10px;" clearable />
        <el-button type="primary" @click="searchCarousels">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </div>
      <el-button type="primary" @click="dialogVisible = true" style="margin-left: 10px;">
        <el-icon><Plus /></el-icon>
        添加轮播图
      </el-button>
    </div>
    
    <el-table :data="carousels" border stripe style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="description" label="描述" />
      <el-table-column prop="imageUrl" label="图片URL" show-overflow-tooltip />
      <el-table-column prop="sortOrder" label="排序" width="100" />
      <el-table-column prop="intervalTime" label="切换时间(秒)" width="150">
        <template #default="scope">
          {{ (scope.row.intervalTime || 3000) / 1000 }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
            {{ scope.row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" width="180" />
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button type="primary" size="small" @click="handleEdit(scope.row)">
            <el-icon><Edit /></el-icon>
            编辑
          </el-button>
          <el-button type="danger" size="small" @click="handleDelete(scope.row)">
            <el-icon><Delete /></el-icon>
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <!-- 分页组件 -->
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
    
    <!-- 轮播图对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="editingCarousel.id ? '编辑轮播图' : '添加轮播图'"
      width="500px"
    >
      <el-form :model="editingCarousel" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="editingCarousel.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input
            v-model="editingCarousel.description"
            type="textarea"
            placeholder="请输入描述"
            rows="3"
          />
        </el-form-item>
        <el-form-item label="图片URL">
          <el-input v-model="editingCarousel.imageUrl" placeholder="请输入图片URL或上传本地图片" />
        </el-form-item>
        <el-form-item label="本地图片">
          <el-upload
            class="image-uploader"
            :http-request="handleUploadRequest"
            :on-success="handleImageUploadSuccess"
            :before-upload="handleBeforeUpload"
            :show-file-list="true"
            accept="image/*"
            multiple="false"
          >
            <el-button size="small" type="primary">选择图片</el-button>
            <template #tip>
              <div class="el-upload__tip">只能上传jpg/png文件，且不超过2MB</div>
            </template>
          </el-upload>
        </el-form-item>
        <el-form-item v-if="editingCarousel.imageUrl" label="图片预览">
          <div class="image-preview">
            <img :src="getImageUrl(editingCarousel.imageUrl)" alt="轮播图预览" />
          </div>
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="editingCarousel.sortOrder" :min="0" />
        </el-form-item>
        <el-form-item label="切换时间">
          <el-input-number 
            v-model="intervalSeconds" 
            :min="1" 
            :max="60" 
            :precision="0" 
            placeholder="请输入切换时间（秒）"
          />
          <span style="margin-left: 10px;">秒</span>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="editingCarousel.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
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
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'
import axios from 'axios'

// 轮播图列表
const carousels = ref([])

// 分页相关数据
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 搜索相关数据
const searchCondition = ref('title')
const searchKeyword = ref('')
const currentData = ref([]) // 当前显示的数据（原始数据或搜索结果）

// 对话框状态
const dialogVisible = ref(false)

// 编辑中的轮播图
const editingCarousel = reactive({
  id: null,
  title: '',
  description: '',
  imageUrl: '',
  sortOrder: 0,
  intervalTime: 3000,
  status: 1
})

// 切换时间（秒），用于表单显示和输入
const intervalSeconds = ref(3)

// 分页事件处理
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  // 重新分页显示数据
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  carousels.value = currentData.value.slice(start, end)
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  // 重新分页显示数据
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  carousels.value = currentData.value.slice(start, end)
}

// 原始轮播图数据（用于分页和搜索）
const allCarousels = ref([])

// 冒泡排序算法
const bubbleSort = (arr, sortBy) => {
  const sortedArr = [...arr]
  const len = sortedArr.length
  for (let i = 0; i < len - 1; i++) {
    for (let j = 0; j < len - 1 - i; j++) {
      if (sortedArr[j][sortBy] > sortedArr[j + 1][sortBy]) {
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
    let value = item[condition]
    
    // 特殊处理状态字段
    if (condition === 'status') {
      value = value === 1 ? '启用' : '禁用'
    }
    
    if (value && String(value).toLowerCase().includes(lowerKeyword)) {
      results.push(item)
    }
  }
  
  return results
}

// 获取轮播图列表
const getCarousels = async () => {
  try {
    const res = await axios.get('/api/carousels')
    allCarousels.value = res.data
    currentData.value = [...allCarousels.value]
    total.value = res.data.length
    // 分页处理
    const start = (currentPage.value - 1) * pageSize.value
    const end = start + pageSize.value
    carousels.value = currentData.value.slice(start, end)
  } catch (error) {
    console.error('获取轮播图列表失败:', error)
    ElMessage.error('获取轮播图列表失败')
  }
}

// 打开编辑对话框
const handleEdit = (carousel) => {
  Object.assign(editingCarousel, carousel)
  // 将毫秒转换为秒显示在表单中
  intervalSeconds.value = carousel.intervalTime ? carousel.intervalTime / 1000 : 3
  dialogVisible.value = true
}

// 打开删除确认
const handleDelete = (carousel) => {
  ElMessageBox.confirm(
    `确定要删除轮播图"${carousel.title || carousel.id}"吗？`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  )
    .then(async () => {
      try {
        await axios.delete(`/api/carousels/${carousel.id}`)
        ElMessage.success('删除成功')
        getCarousels()
      } catch (error) {
        console.error('删除轮播图失败:', error)
        ElMessage.error('删除轮播图失败')
      }
    })
    .catch(() => {
      // 用户取消删除
    })
}

// 保存轮播图
const handleSave = async () => {
  try {
    // 将秒转换为毫秒
    editingCarousel.intervalTime = intervalSeconds.value * 1000
    
    if (editingCarousel.id) {
      // 编辑模式
      await axios.put(`/api/carousels/${editingCarousel.id}`, editingCarousel)
      ElMessage.success('更新成功')
    } else {
      // 添加模式
      await axios.post('/api/carousels', editingCarousel)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    getCarousels()
    resetForm()
  } catch (error) {
    console.error('保存轮播图失败:', error)
    ElMessage.error('保存轮播图失败')
  }
}

// 重置表单
const resetForm = () => {
  Object.assign(editingCarousel, {
    id: null,
    title: '',
    description: '',
    imageUrl: '',
    sortOrder: 0,
    intervalTime: 3000,
    status: 1
  })
  // 重置秒数
  intervalSeconds.value = 3
}

// 对话框关闭时重置表单
const handleDialogClose = () => {
  resetForm()
}

// 图片上传成功回调
const handleImageUploadSuccess = (response) => {
  if (response.status === 200) {
    // 将上传成功的图片URL设置到表单中
    editingCarousel.imageUrl = response.data
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error('图片上传失败：' + response.data)
  }
}

// 上传前验证
const handleBeforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件！')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('上传图片大小不能超过 2MB！')
    return false
  }
  return true
}

// 自定义上传请求（解决el-upload无法携带token的问题）
const handleUploadRequest = (options) => {
  const { file, onSuccess, onError } = options
  const formData = new FormData()
  formData.append('file', file)
  
  // 使用axios发送请求，自动携带token（通过全局拦截器）
  axios.post('/api/carousels/upload', formData)
    .then(response => {
      // 调用el-upload的成功回调
      onSuccess(response)
    })
    .catch(error => {
      // 调用el-upload的失败回调
      onError(error)
    })
}

// 搜索轮播图函数
const searchCarousels = () => {
  if (!allCarousels.value.length) {
    ElMessage.warning('请先获取轮播图列表')
    return
  }
  
  // 使用线性搜索过滤轮播图数据
  const filteredCarousels = linearSearch(allCarousels.value, searchCondition.value, searchKeyword.value)
  
  // 使用冒泡排序对搜索结果进行排序
  const sortedCarousels = bubbleSort(filteredCarousels, 'id')
  
  // 更新当前数据和总条数
  currentData.value = sortedCarousels
  total.value = sortedCarousels.length
  
  // 重置当前页码并分页显示结果
  currentPage.value = 1
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  carousels.value = currentData.value.slice(start, end)
}

// 重置搜索函数
const resetSearch = () => {
  searchCondition.value = 'title'
  searchKeyword.value = ''
  
  // 重置当前数据为所有轮播图数据
  currentData.value = [...allCarousels.value]
  total.value = allCarousels.value.length
  
  // 重置当前页码并分页显示所有数据
  currentPage.value = 1
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  carousels.value = currentData.value.slice(start, end)
}

// 获取完整的图片URL
const getImageUrl = (imageUrl) => {
  // 如果是本地图片路径（以/uploads/开头），则拼接完整URL
  if (imageUrl && imageUrl.startsWith('/uploads/')) {
    return `${imageUrl}`
  }
  // 否则直接返回（可能是外部URL）
  return imageUrl
}

// 页面加载时获取轮播图列表
onMounted(() => {
  getCarousels()
})
</script>

<style scoped>
.carousel-manage-container {
  padding: 20px;
}

.header-actions {
  margin-bottom: 20px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
}

.search-container {
  display: flex;
  align-items: center;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
}
</style>

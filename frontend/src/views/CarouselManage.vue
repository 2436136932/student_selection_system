<template>
  <div class="carousel-manage-container">
    <h1>轮播图管理</h1>
    
    <div class="header-actions">
      <el-button type="primary" @click="dialogVisible = true">
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

// 对话框状态
const dialogVisible = ref(false)

// 编辑中的轮播图
const editingCarousel = reactive({
  id: null,
  title: '',
  description: '',
  imageUrl: '',
  sortOrder: 0,
  status: 1
})

// 获取轮播图列表
const getCarousels = async () => {
  try {
    const res = await axios.get('/api/carousels')
    carousels.value = res.data
  } catch (error) {
    console.error('获取轮播图列表失败:', error)
    ElMessage.error('获取轮播图列表失败')
  }
}

// 打开编辑对话框
const handleEdit = (carousel) => {
  Object.assign(editingCarousel, carousel)
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
    status: 1
  })
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

// 获取完整的图片URL
const getImageUrl = (imageUrl) => {
  // 如果是本地图片路径（以/uploads/开头），则拼接完整URL
  if (imageUrl && imageUrl.startsWith('/uploads/')) {
    return `http://localhost:8080${imageUrl}`
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
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
}
</style>

<template>
  <div class="material-preview">
    <el-dialog
      v-model="dialogVisible"
      :title="previewTitle"
      width="80%"
      top="10vh"
      destroy-on-close
      @close="handleClose"
    >
      <div class="preview-content">
        <!-- 加载状态 -->
        <div v-if="loading" class="loading-preview">
          <el-spinner type="ring" size="64px" />
          <div>正在加载预览文件...</div>
        </div>
        
        <!-- 加载失败状态 -->
        <div v-else-if="loadError" class="load-error-preview">
          <el-icon class="error-icon"><DocumentRemove /></el-icon>
          <div class="error-message">
            <h3>文件加载失败</h3>
            <p>{{ errorMessage }}</p>
            <el-button type="primary" @click="retryLoadFile">
              <el-icon><RefreshRight /></el-icon> 重试
            </el-button>
          </div>
        </div>
        
        <!-- 图片预览 -->
        <div v-else-if="fileType === 'image' && blobUrl" class="image-preview">
          <el-image
            :src="blobUrl"
            fit="contain"
            style="max-height: 70vh;"
            @error="handleImageError"
          >
            <template #error>
              <div class="image-error">
                <el-icon><Picture /></el-icon>
                <div>图片加载失败</div>
              </div>
            </template>
          </el-image>
        </div>
        
        <!-- PDF预览 -->
        <div v-else-if="fileType === 'pdf' && blobUrl" class="pdf-preview">
          <iframe
            :src="blobUrl"
            frameborder="0"
            sandbox="allow-same-origin allow-scripts allow-popups allow-downloads"
            style="width: 100%; height: 70vh;"
            @error="handleIframeError"
            @load="handleIframeLoad"
          ></iframe>
          <div v-if="pdfLoadError" class="pdf-error">
            <el-icon><Document /></el-icon>
            <div>
              <h3>PDF加载失败</h3>
              <p>请尝试下载后查看</p>
            </div>
          </div>
        </div>
        
        <!-- 文档预览 -->
        <div v-else-if="(fileType === 'doc' || fileType === 'docx')" class="doc-preview">
          <div class="doc-info">
            <el-icon><Document /></el-icon>
            <div>
              <h3>{{ fileName }}</h3>
              <p>文件类型：{{ fileType === 'doc' ? 'Word 文档' : 'Word 文档' }}</p>
              <p>文件大小：{{ formatFileSize(fileSize) }}</p>
            </div>
          </div>
          <div class="doc-actions">
            <el-button type="primary" @click="handleDownload">
              <el-icon><Download /></el-icon> 下载文件
            </el-button>
            <el-button type="info" @click="openInNewTab">
              <el-icon><Link /></el-icon> 在新标签页打开
            </el-button>
          </div>
        </div>
        
        <!-- PPT预览 -->
        <div v-else-if="(fileType === 'ppt' || fileType === 'pptx')" class="ppt-preview">
          <div class="ppt-info">
            <el-icon><Document /></el-icon>
            <div>
              <h3>{{ fileName }}</h3>
              <p>文件类型：{{ fileType === 'ppt' ? 'PowerPoint 演示文稿' : 'PowerPoint 演示文稿' }}</p>
              <p>文件大小：{{ formatFileSize(fileSize) }}</p>
            </div>
          </div>
          <div class="ppt-actions">
            <el-button type="primary" @click="handleDownload">
              <el-icon><Download /></el-icon> 下载文件
            </el-button>
            <el-button type="info" @click="openInNewTab">
              <el-icon><Link /></el-icon> 在新标签页打开
            </el-button>
          </div>
        </div>
        
        <!-- 其他文件类型 -->
        <div v-else class="other-preview">
          <div class="other-info">
            <el-icon><Document /></el-icon>
            <div>
              <h3>{{ fileName }}</h3>
              <p>文件类型：{{ fileType.toUpperCase() }}</p>
              <p>文件大小：{{ formatFileSize(fileSize) }}</p>
            </div>
          </div>
          <div class="other-actions">
            <el-button type="primary" @click="handleDownload">
              <el-icon><Download /></el-icon> 下载文件
            </el-button>
          </div>
        </div>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">关闭</el-button>
          <el-button type="primary" @click="handleDownload">
            <el-icon><Download /></el-icon> 下载
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, watch, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Picture, Document, Download, Link, DocumentRemove, RefreshRight } from '@element-plus/icons-vue'
import axios from 'axios'

// Props
const props = defineProps({
  // 预览URL
  url: {
    type: String,
    required: true
  },
  // 下载URL
  downloadUrl: {
    type: String,
    required: true
  },
  // 文件名
  fileName: {
    type: String,
    required: true
  },
  // 文件大小（字节）
  fileSize: {
    type: Number,
    default: 0
  },
  // 显示预览对话框
  visible: {
    type: Boolean,
    default: false
  }
})

// Emits
const emit = defineEmits(['update:visible', 'close'])

// Dialog visible
const dialogVisible = ref(props.visible)
// PDF加载错误状态
const pdfLoadError = ref(false)
// 加载状态
const loading = ref(false)
// Blob URL，用于存储从服务器获取的文件内容
const blobUrl = ref('')
// 加载错误状态
const loadError = ref(false)
// 错误信息
const errorMessage = ref('')

// Watch for visible prop changes
watch(() => props.visible, (newVal) => {
  dialogVisible.value = newVal
  // 重置状态
  if (newVal) {
    resetPreviewState()
    loadFileContent()
  } else {
    // 清理Blob URL
    cleanupBlobUrl()
  }
})

// Watch for dialog visible changes
watch(dialogVisible, (newVal) => {
  emit('update:visible', newVal)
})

// 计算文件类型
const fileType = computed(() => {
  const ext = props.fileName.split('.').pop().toLowerCase()
  if (['png', 'jpg', 'jpeg', 'gif', 'bmp', 'svg'].includes(ext)) {
    return 'image'
  } else if (ext === 'pdf') {
    return 'pdf'
  } else if (ext === 'doc' || ext === 'docx') {
    return 'doc'
  } else if (ext === 'ppt' || ext === 'pptx') {
    return 'ppt'
  } else {
    return ext
  }
})

// 预览标题
const previewTitle = computed(() => {
  return `预览 - ${props.fileName}`
})

// 重置预览状态
const resetPreviewState = () => {
  pdfLoadError.value = false
  loadError.value = false
  errorMessage.value = ''
  blobUrl.value = ''
}

// 加载文件内容
const loadFileContent = async () => {
  loading.value = true
  loadError.value = false
  errorMessage.value = ''
  try {
    // 添加调试日志，查看完整的请求URL和axios配置
    console.log('开始加载文件内容，props.url:', props.url)
    console.log('axios.defaults.baseURL:', axios.defaults.baseURL)
    
    // 确保请求路径正确，使用完整的后端URL
    let requestUrl = props.url
    // 如果是相对路径，确保使用axios的baseURL或者直接使用完整URL
    if (requestUrl.startsWith('/api')) {
      // 使用完整的后端URL
      requestUrl = `http://localhost:8080${requestUrl}`
    }
    
    // 添加调试日志，查看最终请求URL
    console.log('最终请求URL:', requestUrl)
    
    // 使用axios获取文件内容，设置responseType为blob
    const response = await axios.get(requestUrl, {
      responseType: 'blob',
      headers: {
        // 显式添加Authorization头，确保认证信息被正确携带
        Authorization: localStorage.getItem('token') ? `Bearer ${localStorage.getItem('token')}` : ''
      }
    })
    
    console.log('文件内容加载成功，状态码:', response.status)
    console.log('响应头:', response.headers)
    
    // 创建Blob URL
    const blob = new Blob([response.data], { type: response.headers['content-type'] })
    blobUrl.value = URL.createObjectURL(blob)
    
    loading.value = false
  } catch (error) {
    console.error('加载文件内容失败:', error)
    console.error('错误详情:', {
      status: error.response?.status,
      statusText: error.response?.statusText,
      data: error.response?.data,
      config: error.response?.config
    })
    
    // 设置错误状态
    loading.value = false
    loadError.value = true
    errorMessage.value = `加载失败: ${error.response?.status || '未知错误'} - ${error.response?.statusText || '请求失败'}`
    
    // 清理Blob URL
    cleanupBlobUrl()
  }
}

// 重试加载文件
const retryLoadFile = () => {
  loadFileContent()
}

// 清理Blob URL
const cleanupBlobUrl = () => {
  if (blobUrl.value) {
    URL.revokeObjectURL(blobUrl.value)
    blobUrl.value = ''
  }
}

// 组件卸载时清理Blob URL
onUnmounted(() => {
  cleanupBlobUrl()
})

// 格式化文件大小
const formatFileSize = (size) => {
  if (size === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(size) / Math.log(k))
  return parseFloat((size / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// 处理关闭
const handleClose = () => {
  emit('close')
}

// 处理下载
const handleDownload = () => {
  try {
    // 确保下载URL使用完整的后端URL
    let downloadUrl = props.downloadUrl
    if (downloadUrl.startsWith('/api')) {
      downloadUrl = `http://localhost:8080${downloadUrl}`
    }
    
    const link = document.createElement('a')
    link.href = downloadUrl
    link.download = props.fileName
    link.target = '_blank'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    ElMessage.success('文件下载已开始')
  } catch (error) {
    console.error('文件下载失败:', error)
    ElMessage.error('文件下载失败，请重试')
  }
}

// 在新标签页打开
const openInNewTab = () => {
  // 确保URL使用完整的后端URL
  let openUrl = props.url
  if (openUrl.startsWith('/api')) {
    openUrl = `http://localhost:8080${openUrl}`
  }
  window.open(openUrl, '_blank')
}

// 处理图片加载失败
const handleImageError = () => {
  ElMessage.error('图片加载失败，可能是网络问题或图片格式不支持')
  // 显示加载失败状态
  loadError.value = true
  errorMessage.value = '图片加载失败，可能是网络问题或图片格式不支持'
}

// 处理iframe加载失败
const handleIframeError = () => {
  console.error('PDF加载失败，URL:', blobUrl.value)
  pdfLoadError.value = true
  ElMessage.error('PDF预览失败，请尝试下载后查看')
}

// 处理iframe加载成功
const handleIframeLoad = () => {
  pdfLoadError.value = false
  console.log('PDF加载成功，URL:', blobUrl.value)
}
</script>

<style scoped>
.material-preview {
  .preview-content {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    min-height: 50vh;
    padding: 20px;
    
    .loading-preview {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      min-height: 50vh;
      color: #409eff;
      font-size: 16px;
      
      .el-spinner {
        margin-bottom: 20px;
      }
    }
    
    .load-error-preview {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      min-height: 50vh;
      color: #f56c6c;
      text-align: center;
      
      .error-icon {
        font-size: 64px;
        margin-bottom: 20px;
      }
      
      .error-message {
        h3 {
          margin: 0 0 10px 0;
          font-size: 18px;
          color: #303133;
        }
        
        p {
          margin: 10px 0 20px 0;
          color: #606266;
          font-size: 14px;
        }
      }
    }
    
    .image-preview {
      display: flex;
      justify-content: center;
      align-items: center;
      width: 100%;
      height: 100%;
      
      .el-image {
        max-width: 100%;
        max-height: 70vh;
      }
      
      .image-error {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        color: #909399;
        font-size: 14px;
        
        .el-icon {
          font-size: 48px;
          margin-bottom: 10px;
        }
      }
    }
    
    .pdf-preview {
      width: 100%;
      height: 70vh;
      border: 1px solid #ebeef5;
      border-radius: 4px;
      overflow: hidden;
      position: relative;
    }
    
    .pdf-error {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      background-color: rgba(255, 255, 255, 0.9);
      z-index: 10;
      
      .el-icon {
        font-size: 64px;
        color: #f56c6c;
        margin-bottom: 20px;
      }
      
      h3 {
        margin: 0 0 10px 0;
        font-size: 18px;
        color: #303133;
      }
      
      p {
        margin: 5px 0;
        color: #606266;
        font-size: 14px;
      }
    }
    
    .doc-preview,
    .ppt-preview,
    .other-preview {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      width: 100%;
      padding: 40px;
      text-align: center;
      
      .doc-info,
      .ppt-info,
      .other-info {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        margin-bottom: 30px;
        
        .el-icon {
          font-size: 64px;
          color: #409eff;
          margin-bottom: 20px;
        }
        
        h3 {
          margin: 0 0 10px 0;
          font-size: 18px;
          color: #303133;
        }
        
        p {
          margin: 5px 0;
          color: #606266;
          font-size: 14px;
        }
      }
      
      .doc-actions,
      .ppt-actions,
      .other-actions {
        display: flex;
        gap: 10px;
      }
    }
  }
  
  .dialog-footer {
    text-align: right;
  }
}
</style>
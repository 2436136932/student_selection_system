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
        <!-- 图片预览 -->
        <div v-if="fileType === 'image'" class="image-preview">
          <el-image
            :src="previewUrl"
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
        <div v-else-if="fileType === 'pdf'" class="pdf-preview">
          <iframe
            :src="previewUrl"
            frameborder="0"
            style="width: 100%; height: 70vh;"
          ></iframe>
        </div>
        
        <!-- 文档预览 -->
        <div v-else-if="fileType === 'doc' || fileType === 'docx'" class="doc-preview">
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
        <div v-else-if="fileType === 'ppt' || fileType === 'pptx'" class="ppt-preview">
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
import { ref, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Picture, Document, Download, Link } from '@element-plus/icons-vue'
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

// Watch for visible prop changes
watch(() => props.visible, (newVal) => {
  dialogVisible.value = newVal
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

// 预览URL
const previewUrl = computed(() => {
  return props.url
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
    const link = document.createElement('a')
    link.href = props.downloadUrl
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
  window.open(props.url, '_blank')
}

// 处理图片加载失败
const handleImageError = () => {
  ElMessage.error('图片加载失败，可能是网络问题或图片格式不支持')
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
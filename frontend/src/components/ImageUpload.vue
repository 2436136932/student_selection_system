<template>
  <div class="image-upload" :class="{ 'dragging': isDragging, 'has-error': error }">
    <div 
      class="upload-area"
      @click="handleClick"
      @dragover.prevent="handleDragOver"
      @dragleave.prevent="handleDragLeave"
      @drop.prevent="handleDrop"
    >
      <div v-if="!imageUrl" class="upload-placeholder">
        <div class="upload-icon">
          <el-icon :size="48"><Upload /></el-icon>
        </div>
        <div class="upload-text">
          <p class="upload-title">{{ title || '点击或拖拽上传图片' }}</p>
          <p class="upload-hint">{{ hint || '支持 JPG、PNG、GIF 格式，最大 5MB' }}</p>
        </div>
      </div>
      
      <div v-else class="image-preview">
        <img :src="imageUrl" :alt="fileName" />
        <div class="image-overlay">
          <el-button type="primary" size="small" @click.stop="handleRemove">
            <el-icon><Delete /></el-icon>
            删除
          </el-button>
          <el-button type="success" size="small" @click.stop="handlePreview">
            <el-icon><View /></el-icon>
            预览
          </el-button>
        </div>
      </div>
      
      <input
        ref="fileInputRef"
        type="file"
        accept="image/jpeg,image/jpg,image/png,image/gif"
        @change="handleFileChange"
        style="display: none"
      />
    </div>
    
    <div v-if="error" class="error-message">
      <el-icon><Warning /></el-icon>
      {{ error }}
    </div>
    
    <div v-if="uploadProgress > 0 && uploadProgress < 100" class="progress-container">
      <el-progress 
        :percentage="uploadProgress" 
        :stroke-width="8"
        :show-text="true"
        :text-inside="true"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { Upload, Delete, View, Warning } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  title: {
    type: String,
    default: ''
  },
  hint: {
    type: String,
    default: ''
  },
  maxSize: {
    type: Number,
    default: 5
  },
  disabled: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue', 'upload', 'remove', 'preview'])

const fileInputRef = ref(null)
const isDragging = ref(false)
const uploadProgress = ref(0)
const error = ref('')

const imageUrl = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const fileName = computed(() => {
  if (!imageUrl.value) return ''
  const parts = imageUrl.value.split('/')
  return parts[parts.length - 1]
})

const handleClick = () => {
  if (props.disabled) return
  fileInputRef.value?.click()
}

const handleDragOver = (event) => {
  if (props.disabled) return
  isDragging.value = true
  event.dataTransfer.dropEffect = 'copy'
}

const handleDragLeave = () => {
  isDragging.value = false
}

const handleDrop = (event) => {
  if (props.disabled) return
  isDragging.value = false
  error.value = ''
  
  const files = event.dataTransfer.files
  if (files.length > 0) {
    handleFile(files[0])
  }
}

const handleFileChange = (event) => {
  const files = event.target.files
  if (files.length > 0) {
    handleFile(files[0])
  }
}

const handleFile = (file) => {
  error.value = ''
  
  if (!file.type.startsWith('image/')) {
    error.value = '只能上传图片文件'
    ElMessage.error('只能上传图片文件')
    return
  }
  
  const maxSize = props.maxSize * 1024 * 1024
  if (file.size > maxSize) {
    error.value = `图片大小不能超过 ${props.maxSize}MB`
    ElMessage.error(`图片大小不能超过 ${props.maxSize}MB`)
    return
  }
  
  uploadFile(file)
}

const uploadFile = (file) => {
  uploadProgress.value = 0
  
  const reader = new FileReader()
  reader.onload = (event) => {
    imageUrl.value = event.target.result
    uploadProgress.value = 100
    emit('upload', file)
    ElMessage.success('图片上传成功')
  }
  
  reader.onerror = () => {
    error.value = '图片读取失败'
    ElMessage.error('图片读取失败')
    uploadProgress.value = 0
  }
  
  reader.onprogress = (event) => {
    if (event.lengthComputable) {
      uploadProgress.value = Math.round((event.loaded / event.total) * 100)
    }
  }
  
  reader.readAsDataURL(file)
}

const handleRemove = () => {
  imageUrl.value = ''
  uploadProgress.value = 0
  error.value = ''
  emit('remove')
  ElMessage.success('图片已删除')
}

const handlePreview = () => {
  emit('preview', imageUrl.value)
}
</script>

<style scoped>
.image-upload {
  width: 100%;
}

.upload-area {
  width: 100%;
  min-height: 200px;
  border: 2px dashed #dcdfe6;
  border-radius: 14px;
  background: #f8f9fa;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.upload-area:hover {
  border-color: #3498db;
  background: rgba(52, 152, 219, 0.05);
}

.upload-area.dragging {
  border-color: #3498db;
  background: rgba(52, 152, 219, 0.1);
  transform: scale(1.02);
}

.image-upload.has-error .upload-area {
  border-color: #e74c3c;
  background: rgba(231, 76, 60, 0.05);
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 24px;
  text-align: center;
}

.upload-icon {
  color: #3498db;
  margin-bottom: 16px;
  animation: bounce 2s ease-in-out infinite;
}

.upload-text {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.upload-title {
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0;
}

.upload-hint {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

.image-preview {
  width: 100%;
  height: 200px;
  position: relative;
  overflow: hidden;
  border-radius: 14px;
}

.image-preview img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  transition: transform 0.3s ease;
}

.image-preview:hover img {
  transform: scale(1.05);
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.image-preview:hover .image-overlay {
  opacity: 1;
}

.error-message {
  margin-top: 12px;
  padding: 8px 12px;
  background: rgba(231, 76, 60, 0.1);
  border: 1px solid #e74c3c;
  border-radius: 8px;
  color: #e74c3c;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 8px;
  animation: slideIn 0.3s ease;
}

.progress-container {
  margin-top: 12px;
}

@keyframes bounce {
  0%, 20%, 50%, 80%, 100% {
    transform: translateY(0);
  }
  40% {
    transform: translateY(-10px);
  }
  60% {
    transform: translateY(-5px);
  }
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .upload-area {
    min-height: 160px;
  }
  
  .image-preview {
    height: 160px;
  }
  
  .upload-icon {
    font-size: 36px;
  }
  
  .upload-title {
    font-size: 14px;
  }
  
  .upload-hint {
    font-size: 12px;
  }
}
</style>

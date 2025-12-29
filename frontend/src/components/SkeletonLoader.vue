<template>
  <div class="skeleton-loader" :class="type">
    <!-- 卡片骨架 -->
    <template v-if="type === 'card'">
      <div class="skeleton-card">
        <div class="skeleton-header">
          <div class="skeleton-avatar"></div>
          <div class="skeleton-title"></div>
        </div>
        <div class="skeleton-body">
          <div class="skeleton-line" v-for="i in 3" :key="i"></div>
        </div>
      </div>
    </template>
    
    <!-- 表格骨架 -->
    <template v-else-if="type === 'table'">
      <div class="skeleton-table">
        <div class="skeleton-table-header">
          <div class="skeleton-cell" v-for="i in 5" :key="i"></div>
        </div>
        <div class="skeleton-table-body">
          <div class="skeleton-table-row" v-for="row in 5" :key="row">
            <div class="skeleton-cell" v-for="i in 5" :key="i"></div>
          </div>
        </div>
      </div>
    </template>
    
    <!-- 列表骨架 -->
    <template v-else-if="type === 'list'">
      <div class="skeleton-list">
        <div class="skeleton-list-item" v-for="i in 5" :key="i">
          <div class="skeleton-avatar"></div>
          <div class="skeleton-content">
            <div class="skeleton-title"></div>
            <div class="skeleton-line"></div>
          </div>
        </div>
      </div>
    </template>
    
    <!-- 文本骨架 -->
    <template v-else-if="type === 'text'">
      <div class="skeleton-text">
        <div class="skeleton-line" v-for="i in lines" :key="i"></div>
      </div>
    </template>
    
    <!-- 图片骨架 -->
    <template v-else-if="type === 'image'">
      <div class="skeleton-image"></div>
    </template>
    
    <!-- 输入框骨架 -->
    <template v-else-if="type === 'input'">
      <div class="skeleton-input"></div>
    </template>
    
    <!-- 按钮骨架 -->
    <template v-else-if="type === 'button'">
      <div class="skeleton-button"></div>
    </template>
    
    <!-- 默认卡片骨架 -->
    <template v-else>
      <div class="skeleton-card">
        <div class="skeleton-header">
          <div class="skeleton-avatar"></div>
          <div class="skeleton-title"></div>
        </div>
        <div class="skeleton-body">
          <div class="skeleton-line" v-for="i in 3" :key="i"></div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
defineProps({
  type: {
    type: String,
    default: 'card',
    validator: (value) => ['card', 'table', 'list', 'text', 'image', 'input', 'button'].includes(value)
  },
  lines: {
    type: Number,
    default: 3
  }
})
</script>

<style scoped>
.skeleton-loader {
  width: 100%;
  height: 100%;
}

/* 通用骨架动画 */
.skeleton-loader * {
  animation: shimmer 1.5s infinite;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
}

/* 卡片骨架 */
.skeleton-card {
  background: white;
  border-radius: 14px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.skeleton-header {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}

.skeleton-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  margin-right: 12px;
}

.skeleton-title {
  flex: 1;
  height: 24px;
  border-radius: 6px;
}

.skeleton-body {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.skeleton-line {
  height: 16px;
  border-radius: 4px;
}

.skeleton-line:nth-child(1) {
  width: 100%;
}

.skeleton-line:nth-child(2) {
  width: 80%;
}

.skeleton-line:nth-child(3) {
  width: 60%;
}

/* 表格骨架 */
.skeleton-table {
  background: white;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.skeleton-table-header {
  display: flex;
  padding: 12px 16px;
  background: #f8f9fa;
  border-bottom: 1px solid #e4e7ed;
}

.skeleton-table-body {
  padding: 12px 16px;
}

.skeleton-table-row {
  display: flex;
  margin-bottom: 12px;
}

.skeleton-cell {
  flex: 1;
  height: 40px;
  border-radius: 6px;
  margin-right: 12px;
}

.skeleton-cell:last-child {
  margin-right: 0;
}

/* 列表骨架 */
.skeleton-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.skeleton-list-item {
  display: flex;
  align-items: center;
  background: white;
  padding: 12px 16px;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.skeleton-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

/* 文本骨架 */
.skeleton-text {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

/* 图片骨架 */
.skeleton-image {
  width: 100%;
  height: 200px;
  border-radius: 10px;
  background: white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

/* 输入框骨架 */
.skeleton-input {
  width: 100%;
  height: 48px;
  border-radius: 8px;
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

/* 按钮骨架 */
.skeleton-button {
  width: 120px;
  height: 40px;
  border-radius: 8px;
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

/* 骨架动画 */
@keyframes shimmer {
  0% {
    background-position: -200% 0;
  }
  100% {
    background-position: 200% 0;
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .skeleton-card {
    padding: 16px;
  }
  
  .skeleton-avatar {
    width: 40px;
    height: 40px;
  }
  
  .skeleton-title {
    height: 20px;
  }
  
  .skeleton-line {
    height: 14px;
  }
  
  .skeleton-table-header,
  .skeleton-table-body {
    padding: 8px 12px;
  }
  
  .skeleton-cell {
    height: 36px;
    margin-right: 8px;
  }
  
  .skeleton-list-item {
    padding: 10px 12px;
  }
  
  .skeleton-image {
    height: 150px;
  }
}
</style>

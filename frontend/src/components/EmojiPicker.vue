<template>
  <div class="emoji-picker" v-if="visible" ref="pickerRef">
    <div class="emoji-picker-header">
      <span class="emoji-picker-title">选择表情</span>
      <el-button type="text" @click="handleClose" class="close-btn">
        <el-icon><Close /></el-icon>
      </el-button>
    </div>
    
    <div class="emoji-categories">
      <div 
        v-for="(emojis, category) in emojiData" 
        :key="category"
        class="emoji-category"
        v-show="activeCategory === category"
      >
        <div class="category-title">{{ getCategoryName(category) }}</div>
        <div class="emoji-grid">
          <span 
            v-for="emoji in emojis" 
            :key="emoji"
            class="emoji-item"
            @click="handleSelectEmoji(emoji)"
            :title="emoji"
          >
            {{ emoji }}
          </span>
        </div>
      </div>
    </div>
    
    <div class="emoji-tabs">
      <button 
        v-for="(emojis, category) in emojiData" 
        :key="category"
        class="emoji-tab"
        :class="{ 'active': activeCategory === category }"
        @click="activeCategory = category"
      >
        {{ getCategoryIcon(category) }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'
import { Close } from '@element-plus/icons-vue'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['select', 'close'])

const pickerRef = ref(null)
const activeCategory = ref('smileys')

const emojiData = {
  smileys: ['😀', '😃', '😄', '😁', '😆', '😅', '🤣', '😂', '🙂', '🙃', '😉', '😊', '😇', '🥰', '😉', '😋', '😎', '🤩', '🥳', '😏', '😍', '🥰'],
  people: ['👋', '🤚�', '👂', '🧕', '👦', '👧', '👨', '👩', '👪', '🧑', '👱', '👲', '👳', '👴', '👵', '👶', '👷', '👸', '👹', '👺', '👻', '👼', '👽', '👾', '👿', '💀', '💁', '💂', '💃', '💄', '💅', '💆', '💇', '💈', '💉', '💊', '💋', '💌', '💍', '💎', '💏', '💐', '💑', '💒', '💓', '💔', '💕', '💖', '💗', '💘', '💙', '💚', '💛', '💜', '💝'],
  animals: ['🐶', '🐱', '🐭', '🐰', '🐻', '🐼', '🐸', '🐹', '🦊', '🦋', '🦌', '🦍', '🦎', '🦏', '🦐', '🦑', '🦒', '🦓', '🦔', '🦕', '🦖', '🦗', '🦘', '🦙', '🦚', '🦛', '🦜', '🦝', '🦞', '🦟', '🦠', '🦡', '🦢', '🦣', '🦤', '🦥', '🦦', '🦧', '🦨', '🦩', '🦪', '🦫', '🦬', '🦭', '🦮', '🦯', '🦰', '🦱', '🦲', '🦳', '🦴', '🦵', '🦶', '🦷', '🦸', '🦹', '🦺', '🦻', '🦼', '🦽', '🦾', '🦿'],
  food: ['🍏', '🍐', '🍑', '🍒', '🍓', '🍔', '🍕', '🍖', '🍗', '🍘', '🍙', '🍚', '🍛', '🍜', '🍝', '🍞', '🍟', '🍠', '🍡', '🍢', '🍣', '🍤', '🍥', '🍦', '🍧', '🍨', '🍩', '🍪', '🍫', '🍬', '🍭', '🍮', '🍯', '🍰', '🍱', '🍲', '🍳', '🍴', '🍵', '🍶', '🍷', '🍸', '🍹', '🍺', '🍻', '🍼', '🍽', '🍾', '🍿'],
  activities: ['⚽', '⚾', '⛷', '⛸', '⛹', '⛺', '⛻', '⛼', '⛽', '⛾', '⛿', '🀀', '🀁', '🀂', '🀃', '🀄', '🀅', '🀆', '🀇', '🀈', '🀉', '🀊', '🀋', '🀌', '🀍', '🀎', '🀏', '🀐', '🀑', '🀒', '🀓', '🀔', '🀕', '🀖', '🀗', '🀘', '🀙', '🀚', '🀛', '🀜', '🀝', '🀞', '🀟', '🀠', '🀡', '🀢', '🀣', '🀤', '🀥', '🀦', '🀧', '🀨', '🀩', '🀪', '🀫', '🀬', '🀭', '🀮', '🀯', '🀰', '🀱', '🀲', '🀳', '🀴', '🀵', '🀶', '🀷', '🀸', '🀹', '🀺', '🀻', '🀼', '🀽', '🀾', '🀿'],
  objects: ['🀀', '🀁', '🀂', '🀃', '🀄', '🀅', '🀆', '🀇', '🀈', '🀉', '🀊', '🀋', '🀌', '🀍', '🀎', '🀏', '🀐', '🀑', '🀒', '🀓', '🀔', '🀕', '🀖', '🀗', '🀘', '🀙', '🀚', '🀛', '🀜', '🀝', '🀞', '🀟', '🀠', '🀡', '🀢', '🀣', '🀤', '🀥', '🀦', '🀧', '🀨', '🀩', '🀪', '🀫', '🀬', '🀭', '🀮', '🀯', '🀰', '🀱', '🀲', '🀳', '🀴', '🀵', '🀶', '🀷', '🀸', '🀹', '🀺', '🀻', '🀼', '🀽', '🀾', '🀿'],
  symbols: ['🀀', '🀁', '🀂', '🀃', '🀄', '🀅', '🀆', '🀇', '🀈', '🀉', '🀊', '🀋', '🀌', '🀍', '🀎', '🀏', '🀐', '🀑', '🀒', '🀓', '🀔', '🀕', '🀖', '🀗', '🀘', '🀙', '🀚', '🀛', '🀜', '🀝', '🀞', '🀟', '🀠', '🀡', '🀢', '🀣', '🀤', '🀥', '🀦', '🀧', '🀨', '🀩', '🀪', '🀫', '🀬', '🀭', '🀮', '🀯', '🀰', '🀱', '🀲', '🀳', '🀴', '🀵', '🀶', '🀷', '🀸', '🀹', '🀺', '🀻', '🀼', '🀽', '🀾', '🀿']
}

const getCategoryName = (category) => {
  const names = {
    smileys: '笑脸',
    people: '人物',
    animals: '动物',
    food: '食物',
    activities: '活动',
    objects: '物品',
    symbols: '符号'
  }
  return names[category] || category
}

const getCategoryIcon = (category) => {
  const icons = {
    smileys: '😀',
    people: '👋',
    animals: '🐶',
    food: '🍏',
    activities: '⚽',
    objects: '🀀',
    symbols: '🀀'
  }
  return icons[category] || '😀'
}

const handleSelectEmoji = (emoji) => {
  emit('select', emoji)
}

const handleClose = () => {
  emit('close')
}

const handleClickOutside = (event) => {
  if (pickerRef.value && !pickerRef.value.contains(event.target)) {
    handleClose()
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
.emoji-picker {
  position: absolute;
  bottom: 60px;
  right: 20px;
  width: 320px;
  max-height: 400px;
  background: white;
  border-radius: 14px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  z-index: 9999;
  overflow: hidden;
  animation: scaleIn 0.2s ease;
  backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.emoji-picker-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: linear-gradient(135deg, #f8f9fa 0%, #ecf0f1 100%);
  border-bottom: 1px solid #e4e7ed;
}

.emoji-picker-title {
  font-size: 14px;
  font-weight: 600;
  color: #2c3e50;
}

.close-btn {
  padding: 4px;
  font-size: 16px;
  color: #909399;
}

.close-btn:hover {
  color: #e74c3c;
}

.emoji-categories {
  max-height: 320px;
  overflow-y: auto;
  padding: 12px;
}

.emoji-category {
  margin-bottom: 16px;
}

.category-title {
  font-size: 12px;
  font-weight: 600;
  color: #909399;
  margin-bottom: 8px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.emoji-grid {
  display: grid;
  grid-template-columns: repeat(8, 1fr);
  gap: 8px;
}

.emoji-item {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  font-size: 24px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s ease;
  user-select: none;
}

.emoji-item:hover {
  background: rgba(52, 152, 219, 0.1);
  transform: scale(1.2);
}

.emoji-item:active {
  transform: scale(0.95);
}

.emoji-tabs {
  display: flex;
  padding: 8px 12px;
  background: #f8f9fa;
  border-top: 1px solid #e4e7ed;
  gap: 4px;
  overflow-x: auto;
}

.emoji-tab {
  flex-shrink: 0;
  width: 36px;
  height: 36px;
  border: none;
  background: white;
  border-radius: 8px;
  font-size: 20px;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.emoji-tab:hover {
  background: rgba(52, 152, 219, 0.1);
}

.emoji-tab.active {
  background: linear-gradient(135deg, #3498db 0%, #2980b9 100%);
  color: white;
  box-shadow: 0 2px 8px rgba(52, 152, 219, 0.3);
}

@keyframes scaleIn {
  from {
    opacity: 0;
    transform: scale(0.8);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

/* 滚动条美化 */
.emoji-categories::-webkit-scrollbar {
  width: 6px;
}

.emoji-categories::-webkit-scrollbar-track {
  background: #f8f9fa;
}

.emoji-categories::-webkit-scrollbar-thumb {
  background: linear-gradient(135deg, #3498db 0%, #2980b9 100%);
  border-radius: 3px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .emoji-picker {
    width: 280px;
    max-height: 350px;
    bottom: 50px;
    right: 10px;
  }
  
  .emoji-categories {
    max-height: 280px;
  }
  
  .emoji-grid {
    grid-template-columns: repeat(7, 1fr);
  }
  
  .emoji-item {
    width: 28px;
    height: 28px;
    font-size: 20px;
  }
  
  .emoji-tab {
    width: 32px;
    height: 32px;
    font-size: 18px;
  }
}
</style>

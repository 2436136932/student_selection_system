<template>
  <!-- 节日装饰层 - 仅在非肃穆主题且启用装饰时显示 -->
  <div v-if="shouldShow" class="holiday-decoration-container">
    <!-- 春节装饰 -->
    <template v-if="decorType === 'spring'">
      <div v-for="n in 12" :key="`spring-${n}`" class="decor-item decor-sway"
        :style="getSpringStyle(n)">
        <svg width="40" height="40" viewBox="0 0 100 100">
          <rect x="45" y="0" width="10" height="40" fill="#C41E3A"/>
          <circle cx="50" cy="55" r="25" fill="#FFD700" opacity="0.9"/>
          <text x="50" y="62" text-anchor="middle" fill="#C41E3A" font-size="20" font-weight="bold">福</text>
        </svg>
      </div>
    </template>

    <!-- 灯笼装饰 -->
    <template v-if="decorType === 'lanterns'">
      <div v-for="n in 8" :key="`lantern-${n}`" class="decor-item decor-sway"
        :style="getLanternStyle(n)">
        <svg width="50" height="70" viewBox="0 0 100 140">
          <rect x="40" y="0" width="20" height="20" fill="#8B4513"/>
          <ellipse cx="50" cy="70" rx="40" ry="50" fill="#FF6B6B" opacity="0.9"/>
          <ellipse cx="50" cy="70" rx="30" ry="40" fill="#FFD700" opacity="0.5"/>
          <rect x="45" y="120" width="10" height="20" fill="#8B4513"/>
        </svg>
      </div>
    </template>

    <!-- 爱心装饰（情人节/七夕） -->
    <template v-if="decorType === 'hearts'">
      <div v-for="n in 20" :key="`heart-${n}`" class="decor-item decor-fall"
        :style="getHeartStyle(n)">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="#E84393" opacity="0.6">
          <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/>
        </svg>
      </div>
    </template>

    <!-- 星星装饰（七夕） -->
    <template v-if="decorType === 'stars'">
      <div v-for="n in 30" :key="`star-${n}`" class="decor-item decor-twinkle"
        :style="getStarStyle(n)">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="#DDA0DD" opacity="0.7">
          <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
        </svg>
      </div>
    </template>

    <!-- 树叶装饰（劳动节） -->
    <template v-if="decorType === 'leaves'">
      <div v-for="n in 15" :key="`leaf-${n}`" class="decor-item decor-fall"
        :style="getLeafStyle(n)">
        <svg width="28" height="28" viewBox="0 0 24 24" fill="#27AE60" opacity="0.6">
          <path d="M17,8C8,10 5.9,16.17 3.82,21.34L5.71,22L6.66,19.7C7.14,19.87 7.64,20 8,20C19,20 22,3 22,3C21,5 14,5.25 9,6.25C4,7.25 2,11.5 2,13.5C2,15.5 3.75,17.25 3.75,17.25C7,8 17,8 17,8Z"/>
        </svg>
      </div>
    </template>

    <!-- 气球装饰（儿童节） -->
    <template v-if="decorType === 'balloons'">
      <div v-for="n in 10" :key="`balloon-${n}`" class="decor-item decor-float"
        :style="getBalloonStyle(n)">
        <svg width="36" height="48" viewBox="0 0 100 130">
          <ellipse cx="50" cy="50" rx="35" ry="45" :fill="getBalloonColor(n)" opacity="0.8"/>
          <polygon points="50,95 45,105 55,105" :fill="getBalloonColor(n)" opacity="0.8"/>
          <line x1="50" y1="105" x2="50" y2="130" stroke="#999" stroke-width="2"/>
        </svg>
      </div>
    </template>

    <!-- 龙舟装饰（端午节） -->
    <template v-if="decorType === 'dragon'">
      <div v-for="n in 6" :key="`dragon-${n}`" class="decor-item decor-float"
        :style="getDragonStyle(n)">
        <svg width="60" height="30" viewBox="0 0 120 60">
          <ellipse cx="60" cy="30" rx="55" ry="20" fill="#16A085" opacity="0.7"/>
          <circle cx="25" cy="25" r="8" fill="#FFD700" opacity="0.9"/>
          <circle cx="22" cy="22" r="3" fill="#000"/>
          <polygon points="5,30 15,20 15,40" fill="#16A085" opacity="0.8"/>
        </svg>
      </div>
    </template>

    <!-- 旗帜装饰（建党/建军节） -->
    <template v-if="decorType === 'flags'">
      <div v-for="n in 8" :key="`flag-${n}`" class="decor-item decor-sway"
        :style="getFlagStyle(n)">
        <svg width="40" height="50" viewBox="0 0 80 100">
          <rect x="35" y="0" width="5" height="100" fill="#8B4513"/>
          <rect x="40" y="10" width="35" height="25" fill="#C41E3A" opacity="0.9"/>
          <circle cx="57" cy="22" r="5" fill="#FFD700"/>
        </svg>
      </div>
    </template>

    <!-- 花朵装饰（教师节） -->
    <template v-if="decorType === 'flowers'">
      <div v-for="n in 12" :key="`flower-${n}`" class="decor-item decor-float"
        :style="getFlowerStyle(n)">
        <svg width="32" height="32" viewBox="0 0 100 100">
          <circle cx="50" cy="50" r="15" fill="#FFD700"/>
          <ellipse cx="50" cy="20" rx="12" ry="18" fill="#BB8FCE" opacity="0.8"/>
          <ellipse cx="50" cy="80" rx="12" ry="18" fill="#BB8FCE" opacity="0.8"/>
          <ellipse cx="20" cy="50" rx="18" ry="12" fill="#BB8FCE" opacity="0.8"/>
          <ellipse cx="80" cy="50" rx="18" ry="12" fill="#BB8FCE" opacity="0.8"/>
        </svg>
      </div>
    </template>

    <!-- 国庆装饰 -->
    <template v-if="decorType === 'national'">
      <div v-for="n in 10" :key="`national-${n}`" class="decor-item decor-float"
        :style="getNationalStyle(n)">
        <svg width="50" height="60" viewBox="0 0 100 120">
          <rect x="30" y="0" width="5" height="120" fill="#8B4513"/>
          <rect x="35" y="10" width="55" height="35" fill="#C41E3A" opacity="0.9"/>
          <circle cx="50" cy="27" r="8" fill="#FFD700"/>
          <circle cx="70" cy="20" r="3" fill="#FFD700"/>
          <circle cx="78" cy="27" r="3" fill="#FFD700"/>
          <circle cx="78" cy="37" r="3" fill="#FFD700"/>
          <circle cx="70" cy="42" r="3" fill="#FFD700"/>
        </svg>
      </div>
    </template>

    <!-- 月亮装饰（中秋节） -->
    <template v-if="decorType === 'moon'">
      <div class="decor-item" style="top: 8%; right: 10%;">
        <svg width="80" height="80" viewBox="0 0 100 100">
          <circle cx="50" cy="50" r="45" fill="#FFD700" opacity="0.3"/>
          <circle cx="50" cy="50" r="40" fill="#FFD700" opacity="0.5"/>
          <circle cx="50" cy="50" r="35" fill="#FFA502" opacity="0.3"/>
        </svg>
      </div>
      <div v-for="n in 15" :key="`moon-${n}`" class="decor-item decor-twinkle"
        :style="getMoonStyle(n)">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="#FFD700" opacity="0.5">
          <circle cx="12" cy="12" r="10"/>
        </svg>
      </div>
    </template>

    <!-- 圣诞装饰 -->
    <template v-if="decorType === 'christmas'">
      <div v-for="n in 8" :key="`tree-${n}`" class="decor-item decor-float"
        :style="getChristmasStyle(n)">
        <svg width="50" height="60" viewBox="0 0 100 120">
          <polygon points="50,10 20,50 80,50" fill="#27AE60" opacity="0.8"/>
          <polygon points="50,30 15,80 85,80" fill="#27AE60" opacity="0.8"/>
          <polygon points="50,55 10,110 90,110" fill="#27AE60" opacity="0.8"/>
          <rect x="40" y="110" width="20" height="15" fill="#8B4513"/>
          <circle cx="50" cy="25" r="5" fill="#FFD700" opacity="0.9"/>
          <circle cx="35" cy="60" r="4" fill="#E74C3C" opacity="0.8"/>
          <circle cx="65" cy="60" r="4" fill="#3498DB" opacity="0.8"/>
          <circle cx="50" cy="85" r="4" fill="#E74C3C" opacity="0.8"/>
        </svg>
      </div>
      <!-- 雪花 -->
      <div v-for="n in 20" :key="`snow-${n}`" class="decor-item decor-fall"
        :style="getSnowStyle(n)">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="#FFFFFF" opacity="0.8">
          <path d="M12 2L14 8L20 6L16 10L22 12L16 14L20 18L14 16L12 22L10 16L4 18L8 14L2 12L8 10L4 6L10 8L12 2Z"/>
        </svg>
      </div>
    </template>

    <!-- 烟花装饰（元旦） -->
    <template v-if="decorType === 'fireworks'">
      <div v-for="n in 6" :key="`firework-${n}`" class="decor-item"
        :style="getFireworkStyle(n)">
        <svg width="80" height="80" viewBox="0 0 100 100">
          <circle cx="50" cy="50" r="3" fill="#FFD700"/>
          <line x1="50" y1="50" x2="50" y2="20" stroke="#E74C3C" stroke-width="2" opacity="0.8"/>
          <line x1="50" y1="50" x2="50" y2="80" stroke="#E74C3C" stroke-width="2" opacity="0.8"/>
          <line x1="50" y1="50" x2="20" y2="50" stroke="#3498DB" stroke-width="2" opacity="0.8"/>
          <line x1="50" y1="50" x2="80" y2="50" stroke="#3498DB" stroke-width="2" opacity="0.8"/>
          <line x1="50" y1="50" x2="28" y2="28" stroke="#F39C12" stroke-width="2" opacity="0.8"/>
          <line x1="50" y1="50" x2="72" y2="72" stroke="#F39C12" stroke-width="2" opacity="0.8"/>
          <line x1="50" y1="50" x2="28" y2="72" stroke="#9B59B6" stroke-width="2" opacity="0.8"/>
          <line x1="50" y1="50" x2="72" y2="28" stroke="#9B59B6" stroke-width="2" opacity="0.8"/>
        </svg>
      </div>
    </template>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { shouldShowDecorations } from '../utils/holidayTheme'

const props = defineProps({
  theme: {
    type: Object,
    default: () => ({})
  }
})

// 是否显示装饰
const shouldShow = computed(() => {
  return shouldShowDecorations(props.theme)
})

// 装饰类型
const decorType = computed(() => {
  return props.theme?.decorType || 'none'
})

// 春节装饰样式
function getSpringStyle(n) {
  return {
    top: `${Math.random() * 15}%`,
    left: `${(n - 1) * 8 + Math.random() * 5}%`,
    animationDelay: `${Math.random() * 3}s`,
    animationDuration: `${3 + Math.random() * 2}s`
  }
}

// 灯笼装饰样式
function getLanternStyle(n) {
  return {
    top: `${Math.random() * 10}%`,
    left: `${(n - 1) * 12 + Math.random() * 5}%`,
    animationDelay: `${Math.random() * 4}s`,
    animationDuration: `${4 + Math.random() * 2}s`
  }
}

// 爱心装饰样式
function getHeartStyle(n) {
  return {
    left: `${Math.random() * 100}%`,
    animationDelay: `${Math.random() * 5}s`,
    animationDuration: `${5 + Math.random() * 5}s`
  }
}

// 星星装饰样式
function getStarStyle(n) {
  return {
    top: `${Math.random() * 60}%`,
    left: `${Math.random() * 100}%`,
    animationDelay: `${Math.random() * 3}s`,
    animationDuration: `${1.5 + Math.random() * 2}s`
  }
}

// 树叶装饰样式
function getLeafStyle(n) {
  return {
    left: `${Math.random() * 100}%`,
    animationDelay: `${Math.random() * 8}s`,
    animationDuration: `${8 + Math.random() * 6}s`
  }
}

// 气球装饰样式
function getBalloonStyle(n) {
  const colors = ['#E74C3C', '#3498DB', '#F39C12', '#27AE60', '#9B59B6', '#FF6B6B']
  return {
    bottom: `${10 + Math.random() * 20}%`,
    left: `${(n - 1) * 10 + Math.random() * 5}%`,
    animationDelay: `${Math.random() * 2}s`,
    animationDuration: `${3 + Math.random() * 2}s`
  }
}

function getBalloonColor(n) {
  const colors = ['#E74C3C', '#3498DB', '#F39C12', '#27AE60', '#9B59B6', '#FF6B6B']
  return colors[n % colors.length]
}

// 龙舟装饰样式
function getDragonStyle(n) {
  return {
    top: `${60 + Math.random() * 30}%`,
    left: `${Math.random() * 90}%`,
    animationDelay: `${Math.random() * 3}s`,
    animationDuration: `${4 + Math.random() * 3}s`
  }
}

// 旗帜装饰样式
function getFlagStyle(n) {
  return {
    top: `${Math.random() * 15}%`,
    left: `${(n - 1) * 12 + Math.random() * 5}%`,
    animationDelay: `${Math.random() * 3}s`,
    animationDuration: `${3 + Math.random() * 2}s`
  }
}

// 花朵装饰样式
function getFlowerStyle(n) {
  return {
    top: `${70 + Math.random() * 25}%`,
    left: `${Math.random() * 95}%`,
    animationDelay: `${Math.random() * 4}s`,
    animationDuration: `${3 + Math.random() * 3}s`
  }
}

// 国庆装饰样式
function getNationalStyle(n) {
  return {
    top: `${Math.random() * 20}%`,
    left: `${Math.random() * 90}%`,
    animationDelay: `${Math.random() * 3}s`,
    animationDuration: `${4 + Math.random() * 2}s`
  }
}

// 月亮装饰样式
function getMoonStyle(n) {
  return {
    top: `${Math.random() * 50}%`,
    left: `${Math.random() * 100}%`,
    animationDelay: `${Math.random() * 3}s`,
    animationDuration: `${2 + Math.random() * 2}s`
  }
}

// 圣诞装饰样式
function getChristmasStyle(n) {
  return {
    bottom: `${5 + Math.random() * 15}%`,
    left: `${(n - 1) * 12 + Math.random() * 5}%`,
    animationDelay: `${Math.random() * 2}s`,
    animationDuration: `${3 + Math.random() * 2}s`
  }
}

// 雪花装饰样式
function getSnowStyle(n) {
  return {
    left: `${Math.random() * 100}%`,
    animationDelay: `${Math.random() * 10}s`,
    animationDuration: `${8 + Math.random() * 8}s`
  }
}

// 烟花装饰样式
function getFireworkStyle(n) {
  return {
    top: `${5 + Math.random() * 40}%`,
    left: `${10 + Math.random() * 80}%`,
    animation: `pulse-glow ${2 + Math.random() * 2}s ease-in-out infinite`,
    animationDelay: `${Math.random() * 3}s`
  }
}
</script>

<style scoped>
.holiday-decoration-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 9998;
  overflow: hidden;
}

.decor-item {
  position: absolute;
  pointer-events: none;
}

/* 动画定义 */
@keyframes float {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  50% { transform: translateY(-20px) rotate(5deg); }
}

@keyframes fall {
  0% { transform: translateY(-100vh) rotate(0deg); opacity: 1; }
  100% { transform: translateY(100vh) rotate(360deg); opacity: 0; }
}

@keyframes sway {
  0%, 100% { transform: translateX(0) rotate(-5deg); }
  50% { transform: translateX(20px) rotate(5deg); }
}

@keyframes pulse-glow {
  0%, 100% { opacity: 0.4; transform: scale(0.8); }
  50% { opacity: 1; transform: scale(1.2); }
}

@keyframes twinkle {
  0%, 100% { opacity: 0.2; transform: scale(1); }
  50% { opacity: 0.8; transform: scale(1.3); }
}

.decor-float {
  animation: float 3s ease-in-out infinite;
}

.decor-fall {
  animation: fall linear infinite;
}

.decor-sway {
  animation: sway 4s ease-in-out infinite;
}

.decor-twinkle {
  animation: twinkle 2s ease-in-out infinite;
}

/* 响应式 - 移动端隐藏装饰 */
@media (max-width: 768px) {
  .holiday-decoration-container {
    display: none;
  }
}
</style>
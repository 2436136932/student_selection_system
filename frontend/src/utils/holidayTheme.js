/**
 * 节日主题引擎 - 自动识别节日并切换主题
 * 支持公历节日、传统节日、特殊纪念日/国家公祭日肃穆主题
 */

// 节日类型枚举
export const HolidayType = {
  NORMAL: 'normal',      // 普通节日（喜庆）
  SOLEMN: 'solemn'       // 肃穆纪念日
}

// 主题模式枚举
export const ThemeMode = {
  AUTO: 'auto',          // 自动模式
  MANUAL: 'manual'       // 手动模式
}

// 公历节日配置（月-日）
const SOLAR_HOLIDAYS = {
  '01-01': { name: '元旦', type: HolidayType.NORMAL, theme: 'new-year', duration: 1 },
  '02-14': { name: '情人节', type: HolidayType.NORMAL, theme: 'valentine', duration: 1 },
  '03-08': { name: '妇女节', type: HolidayType.NORMAL, theme: 'spring', duration: 1 },
  '04-01': { name: '愚人节', type: HolidayType.NORMAL, theme: 'playful', duration: 1 },
  '05-01': { name: '劳动节', type: HolidayType.NORMAL, theme: 'labor', duration: 3 },
  '05-04': { name: '青年节', type: HolidayType.NORMAL, theme: 'youth', duration: 1 },
  '06-01': { name: '儿童节', type: HolidayType.NORMAL, theme: 'children', duration: 1 },
  '07-01': { name: '建党节', type: HolidayType.NORMAL, theme: 'party', duration: 1 },
  '08-01': { name: '建军节', type: HolidayType.NORMAL, theme: 'army', duration: 1 },
  '09-10': { name: '教师节', type: HolidayType.NORMAL, theme: 'teacher', duration: 1 },
  '10-01': { name: '国庆节', type: HolidayType.NORMAL, theme: 'national-day', duration: 7 },
  '10-24': { name: '程序员节', type: HolidayType.NORMAL, theme: 'programmer', duration: 1 },
  '11-11': { name: '双十一', type: HolidayType.NORMAL, theme: 'shopping', duration: 1 },
  '12-13': { name: '国家公祭日', type: HolidayType.SOLEMN, theme: 'memorial', duration: 1 },
  '12-24': { name: '平安夜', type: HolidayType.NORMAL, theme: 'christmas', duration: 1 },
  '12-25': { name: '圣诞节', type: HolidayType.NORMAL, theme: 'christmas', duration: 1 },
}

// 传统节日配置（农历）- 使用近似公历日期（简化版）
// 实际应用中可接入农历转换库
const LUNAR_HOLIDAYS = {
  // 春节（正月初一）- 日期每年变化，这里使用配置或近似值
  'spring-festival': { 
    name: '春节', 
    type: HolidayType.NORMAL, 
    theme: 'spring-festival', 
    duration: 7,
    // 近5年春节公历日期（可扩展）
    dates: ['2025-01-29', '2026-02-17', '2027-02-06', '2028-01-26', '2029-02-13']
  },
  // 元宵节（正月十五）
  'lantern-festival': {
    name: '元宵节',
    type: HolidayType.NORMAL,
    theme: 'lantern',
    duration: 1,
    dates: ['2025-02-12', '2026-03-03', '2027-02-22', '2028-02-11', '2029-03-02']
  },
  // 清明节
  'qingming': {
    name: '清明节',
    type: HolidayType.SOLEMN,
    theme: 'qingming',
    duration: 3,
    dates: ['2025-04-04', '2026-04-05', '2027-04-05', '2028-04-04', '2029-04-04']
  },
  // 端午节（五月初五）
  'dragon-boat': {
    name: '端午节',
    type: HolidayType.NORMAL,
    theme: 'dragon-boat',
    duration: 3,
    dates: ['2025-05-31', '2026-06-19', '2027-06-09', '2028-05-28', '2029-06-16']
  },
  // 七夕节（七月初七）
  'qixi': {
    name: '七夕节',
    type: HolidayType.NORMAL,
    theme: 'qixi',
    duration: 1,
    dates: ['2025-08-29', '2026-08-19', '2027-08-08', '2028-08-26', '2029-08-16']
  },
  // 中秋节（八月十五）
  'mid-autumn': {
    name: '中秋节',
    type: HolidayType.NORMAL,
    theme: 'mid-autumn',
    duration: 3,
    dates: ['2025-10-06', '2026-09-25', '2027-09-15', '2028-10-03', '2029-09-22']
  },
  // 重阳节（九月初九）
  'double-ninth': {
    name: '重阳节',
    type: HolidayType.NORMAL,
    theme: 'autumn',
    duration: 1,
    dates: ['2025-10-29', '2026-10-18', '2027-10-08', '2028-10-26', '2029-10-16']
  },
}

// 特殊肃穆纪念日（固定公历日期）
const SOLEMN_DAYS = {
  '04-04': { name: '清明时节', type: HolidayType.SOLEMN, theme: 'qingming', duration: 3 },
  '09-03': { name: '抗战胜利纪念日', type: HolidayType.SOLEMN, theme: 'memorial', duration: 1 },
  '09-18': { name: '九一八纪念日', type: HolidayType.SOLEMN, theme: 'memorial', duration: 1 },
  '09-30': { name: '烈士纪念日', type: HolidayType.SOLEMN, theme: 'memorial', duration: 1 },
  '12-13': { name: '国家公祭日', type: HolidayType.SOLEMN, theme: 'memorial', duration: 1 },
}

// 主题配置
export const THEME_CONFIG = {
  // 默认主题
  'default': {
    name: '默认主题',
    type: HolidayType.NORMAL,
    colors: {
      primary: '#4A90E2',
      secondary: '#FF9A8B',
      accent: '#FFB08A',
      bg: '#F8F9FA',
      surface: '#FFFFFF',
      text: '#2C3E50',
      border: 'rgba(74, 144, 226, 0.1)',
      gradient: 'linear-gradient(180deg, #4A90E2 0%, #3D7BCB 50%, #2E6B9A 100%)'
    },
    decorations: false,
    effects: true
  },
  // 元旦/新年
  'new-year': {
    name: '元旦',
    type: HolidayType.NORMAL,
    colors: {
      primary: '#E74C3C',
      secondary: '#F39C12',
      accent: '#FFD700',
      bg: '#FFF8F0',
      surface: '#FFFFFF',
      text: '#2C3E50',
      border: 'rgba(231, 76, 60, 0.15)',
      gradient: 'linear-gradient(180deg, #E74C3C 0%, #C0392B 50%, #922B21 100%)'
    },
    decorations: true,
    effects: true,
    decorType: 'fireworks'
  },
  // 春节
  'spring-festival': {
    name: '春节',
    type: HolidayType.NORMAL,
    colors: {
      primary: '#C41E3A',
      secondary: '#D4AF37',
      accent: '#FFD700',
      bg: '#FFF5F0',
      surface: '#FFFFFF',
      text: '#8B0000',
      border: 'rgba(196, 30, 58, 0.15)',
      gradient: 'linear-gradient(180deg, #C41E3A 0%, #8B0000 50%, #5C0011 100%)'
    },
    decorations: true,
    effects: true,
    decorType: 'spring'
  },
  // 元宵节
  'lantern': {
    name: '元宵节',
    type: HolidayType.NORMAL,
    colors: {
      primary: '#FF6B6B',
      secondary: '#FFA502',
      accent: '#FFD700',
      bg: '#FFF8F0',
      surface: '#FFFFFF',
      text: '#2C3E50',
      border: 'rgba(255, 107, 107, 0.15)',
      gradient: 'linear-gradient(180deg, #FF6B6B 0%, #EE5A24 50%, #C44569 100%)'
    },
    decorations: true,
    effects: true,
    decorType: 'lanterns'
  },
  // 情人节/七夕
  'valentine': {
    name: '情人节',
    type: HolidayType.NORMAL,
    colors: {
      primary: '#E84393',
      secondary: '#FD79A8',
      accent: '#FFB8D0',
      bg: '#FFF0F5',
      surface: '#FFFFFF',
      text: '#2C3E50',
      border: 'rgba(232, 67, 147, 0.15)',
      gradient: 'linear-gradient(180deg, #E84393 0%, #D63031 50%, #B71540 100%)'
    },
    decorations: true,
    effects: true,
    decorType: 'hearts'
  },
  'qixi': {
    name: '七夕',
    type: HolidayType.NORMAL,
    colors: {
      primary: '#6C5CE7',
      secondary: '#A29BFE',
      accent: '#DDA0DD',
      bg: '#F8F0FF',
      surface: '#FFFFFF',
      text: '#2C3E50',
      border: 'rgba(108, 92, 231, 0.15)',
      gradient: 'linear-gradient(180deg, #6C5CE7 0%, #5B4BC4 50%, #4834A0 100%)'
    },
    decorations: true,
    effects: true,
    decorType: 'stars'
  },
  // 劳动节
  'labor': {
    name: '劳动节',
    type: HolidayType.NORMAL,
    colors: {
      primary: '#27AE60',
      secondary: '#2ECC71',
      accent: '#A8E6CF',
      bg: '#F0FFF4',
      surface: '#FFFFFF',
      text: '#2C3E50',
      border: 'rgba(39, 174, 96, 0.15)',
      gradient: 'linear-gradient(180deg, #27AE60 0%, #1E8449 50%, #145A32 100%)'
    },
    decorations: true,
    effects: true,
    decorType: 'leaves'
  },
  // 儿童节
  'children': {
    name: '儿童节',
    type: HolidayType.NORMAL,
    colors: {
      primary: '#3498DB',
      secondary: '#F39C12',
      accent: '#FF6B6B',
      bg: '#FFF9E6',
      surface: '#FFFFFF',
      text: '#2C3E50',
      border: 'rgba(52, 152, 219, 0.15)',
      gradient: 'linear-gradient(180deg, #3498DB 0%, #2980B9 50%, #1F618D 100%)'
    },
    decorations: true,
    effects: true,
    decorType: 'balloons'
  },
  // 端午节
  'dragon-boat': {
    name: '端午节',
    type: HolidayType.NORMAL,
    colors: {
      primary: '#16A085',
      secondary: '#27AE60',
      accent: '#D4AF37',
      bg: '#F0FFF8',
      surface: '#FFFFFF',
      text: '#2C3E50',
      border: 'rgba(22, 160, 133, 0.15)',
      gradient: 'linear-gradient(180deg, #16A085 0%, #0E6655 50%, #0A4236 100%)'
    },
    decorations: true,
    effects: true,
    decorType: 'dragon'
  },
  // 建党节/建军节
  'party': {
    name: '建党节',
    type: HolidayType.NORMAL,
    colors: {
      primary: '#C41E3A',
      secondary: '#D4AF37',
      accent: '#FFD700',
      bg: '#FFF5F0',
      surface: '#FFFFFF',
      text: '#8B0000',
      border: 'rgba(196, 30, 58, 0.15)',
      gradient: 'linear-gradient(180deg, #C41E3A 0%, #8B0000 50%, #5C0011 100%)'
    },
    decorations: true,
    effects: true,
    decorType: 'flags'
  },
  'army': {
    name: '建军节',
    type: HolidayType.NORMAL,
    colors: {
      primary: '#2E7D32',
      secondary: '#D4AF37',
      accent: '#FFD700',
      bg: '#F0FFF0',
      surface: '#FFFFFF',
      text: '#1B5E20',
      border: 'rgba(46, 125, 50, 0.15)',
      gradient: 'linear-gradient(180deg, #2E7D32 0%, #1B5E20 50%, #0D3B10 100%)'
    },
    decorations: true,
    effects: true,
    decorType: 'flags'
  },
  // 教师节
  'teacher': {
    name: '教师节',
    type: HolidayType.NORMAL,
    colors: {
      primary: '#8E44AD',
      secondary: '#BB8FCE',
      accent: '#DDA0DD',
      bg: '#FAF0FF',
      surface: '#FFFFFF',
      text: '#2C3E50',
      border: 'rgba(142, 68, 173, 0.15)',
      gradient: 'linear-gradient(180deg, #8E44AD 0%, #6C3483 50%, #4A235A 100%)'
    },
    decorations: true,
    effects: true,
    decorType: 'flowers'
  },
  // 国庆节
  'national-day': {
    name: '国庆节',
    type: HolidayType.NORMAL,
    colors: {
      primary: '#C41E3A',
      secondary: '#D4AF37',
      accent: '#FFD700',
      bg: '#FFF5F0',
      surface: '#FFFFFF',
      text: '#8B0000',
      border: 'rgba(196, 30, 58, 0.15)',
      gradient: 'linear-gradient(180deg, #C41E3A 0%, #8B0000 50%, #5C0011 100%)'
    },
    decorations: true,
    effects: true,
    decorType: 'national'
  },
  // 中秋节
  'mid-autumn': {
    name: '中秋节',
    type: HolidayType.NORMAL,
    colors: {
      primary: '#E67E22',
      secondary: '#F39C12',
      accent: '#FFD700',
      bg: '#FFF8E7',
      surface: '#FFFFFF',
      text: '#2C3E50',
      border: 'rgba(230, 126, 34, 0.15)',
      gradient: 'linear-gradient(180deg, #E67E22 0%, #D35400 50%, #A04000 100%)'
    },
    decorations: true,
    effects: true,
    decorType: 'moon'
  },
  // 圣诞节
  'christmas': {
    name: '圣诞节',
    type: HolidayType.NORMAL,
    colors: {
      primary: '#C0392B',
      secondary: '#27AE60',
      accent: '#F1C40F',
      bg: '#FFF8F0',
      surface: '#FFFFFF',
      text: '#2C3E50',
      border: 'rgba(192, 57, 43, 0.15)',
      gradient: 'linear-gradient(180deg, #C0392B 0%, #922B21 50%, #641E16 100%)'
    },
    decorations: true,
    effects: true,
    decorType: 'christmas'
  },
  // 清明节/肃穆主题
  'qingming': {
    name: '清明',
    type: HolidayType.SOLEMN,
    colors: {
      primary: '#5D6D7E',
      secondary: '#7F8C8D',
      accent: '#95A5A6',
      bg: '#F5F6F7',
      surface: '#FFFFFF',
      text: '#5D6D7E',
      border: 'rgba(127, 140, 141, 0.15)',
      gradient: 'linear-gradient(180deg, #5D6D7E 0%, #515A5A 50%, #3E4444 100%)'
    },
    decorations: false,
    effects: false,
    decorType: 'none'
  },
  // 国家公祭日/烈士纪念日/肃穆主题
  'memorial': {
    name: '纪念',
    type: HolidayType.SOLEMN,
    colors: {
      primary: '#4A4A4A',
      secondary: '#6B6B6B',
      accent: '#888888',
      bg: '#E8E8E8',
      surface: '#F0F0F0',
      text: '#4A4A4A',
      border: 'rgba(74, 74, 74, 0.15)',
      gradient: 'linear-gradient(180deg, #4A4A4A 0%, #3A3A3A 50%, #2A2A2A 100%)'
    },
    decorations: false,
    effects: false,
    decorType: 'none'
  }
}

/**
 * 获取当前日期信息
 */
function getCurrentDateInfo() {
  const now = new Date()
  const year = now.getFullYear()
  const month = String(now.getMonth() + 1).padStart(2, '0')
  const day = String(now.getDate()).padStart(2, '0')
  const monthDay = `${month}-${day}`
  const fullDate = `${year}-${monthDay}`
  
  return { year, month, day, monthDay, fullDate }
}

/**
 * 检查公历节日
 */
function checkSolarHoliday(monthDay) {
  // 先检查肃穆日期（优先级更高）
  if (SOLEMN_DAYS[monthDay]) {
    return SOLEMN_DAYS[monthDay]
  }
  // 再检查普通节日
  if (SOLAR_HOLIDAYS[monthDay]) {
    return SOLAR_HOLIDAYS[monthDay]
  }
  return null
}

/**
 * 检查农历节日（通过配置的公历日期）
 */
function checkLunarHoliday(fullDate) {
  for (const [key, holiday] of Object.entries(LUNAR_HOLIDAYS)) {
    // 检查是否在节日日期范围内
    for (const date of holiday.dates) {
      const holidayDate = new Date(date)
      const checkDate = new Date(fullDate)
      
      // 检查是否在节日持续时间内
      for (let i = 0; i < holiday.duration; i++) {
        const targetDate = new Date(holidayDate)
        targetDate.setDate(targetDate.getDate() + i)
        
        if (
          targetDate.getFullYear() === checkDate.getFullYear() &&
          targetDate.getMonth() === checkDate.getMonth() &&
          targetDate.getDate() === checkDate.getDate()
        ) {
          return {
            name: holiday.name,
            type: holiday.type,
            theme: holiday.theme,
            duration: holiday.duration
          }
        }
      }
    }
  }
  return null
}

/**
 * 检测当前节日
 * @returns {Object|null} 节日信息或null
 */
export function detectHoliday() {
  const { monthDay, fullDate } = getCurrentDateInfo()
  
  // 先检查公历节日
  let holiday = checkSolarHoliday(monthDay)
  if (holiday) return holiday
  
  // 再检查农历节日
  holiday = checkLunarHoliday(fullDate)
  if (holiday) return holiday
  
  return null
}

/**
 * 获取当前应使用的主题
 * @param {string} manualTheme - 手动设置的主题（如果处于手动模式）
 * @param {string} mode - 主题模式 (auto/manual)
 * @returns {Object} 主题配置
 */
export function getCurrentTheme(manualTheme = null, mode = ThemeMode.AUTO) {
  // 手动模式优先
  if (mode === ThemeMode.MANUAL && manualTheme && THEME_CONFIG[manualTheme]) {
    return {
      ...THEME_CONFIG[manualTheme],
      isManual: true,
      holiday: null
    }
  }
  
  // 自动检测节日
  const holiday = detectHoliday()
  if (holiday && THEME_CONFIG[holiday.theme]) {
    return {
      ...THEME_CONFIG[holiday.theme],
      isManual: false,
      holiday: holiday
    }
  }
  
  // 默认主题
  return {
    ...THEME_CONFIG['default'],
    isManual: false,
    holiday: null
  }
}

/**
 * 应用主题CSS变量
 * @param {Object} theme - 主题配置
 */
export function applyThemeCSS(theme) {
  const root = document.documentElement
  
  if (!theme || !theme.colors) return
  
  const { colors } = theme
  
  // 应用CSS变量
  root.style.setProperty('--holiday-primary', colors.primary)
  root.style.setProperty('--holiday-secondary', colors.secondary)
  root.style.setProperty('--holiday-accent', colors.accent)
  root.style.setProperty('--holiday-bg', colors.bg)
  root.style.setProperty('--holiday-surface', colors.surface)
  root.style.setProperty('--holiday-text', colors.text)
  root.style.setProperty('--holiday-border', colors.border)
  root.style.setProperty('--holiday-gradient', colors.gradient)
  
  // 设置主题类型属性（用于全局样式切换）
  root.setAttribute('data-holiday-theme', theme.type === HolidayType.SOLEMN ? 'solemn' : 'festive')
  root.setAttribute('data-theme-name', theme.name)
  
  // 肃穆主题特殊处理
  if (theme.type === HolidayType.SOLEMN) {
    root.setAttribute('data-solemn-mode', 'true')
    document.body.classList.add('solemn-theme')
    document.body.classList.remove('festive-theme')
  } else {
    root.removeAttribute('data-solemn-mode')
    document.body.classList.remove('solemn-theme')
    document.body.classList.add('festive-theme')
  }
}

/**
 * 清除主题CSS变量
 */
export function clearThemeCSS() {
  const root = document.documentElement
  
  root.style.removeProperty('--holiday-primary')
  root.style.removeProperty('--holiday-secondary')
  root.style.removeProperty('--holiday-accent')
  root.style.removeProperty('--holiday-bg')
  root.style.removeProperty('--holiday-surface')
  root.style.removeProperty('--holiday-text')
  root.style.removeProperty('--holiday-border')
  root.style.removeProperty('--holiday-gradient')
  
  root.removeAttribute('data-holiday-theme')
  root.removeAttribute('data-theme-name')
  root.removeAttribute('data-solemn-mode')
  
  document.body.classList.remove('solemn-theme', 'festive-theme')
}

/**
 * 获取所有可用主题列表（用于手动选择）
 * @returns {Array} 主题列表
 */
export function getAllThemes() {
  return Object.entries(THEME_CONFIG).map(([key, config]) => ({
    key,
    name: config.name,
    type: config.type,
    colors: config.colors
  }))
}

/**
 * 获取节日主题状态文本
 * @param {Object} theme - 当前主题
 * @returns {string} 状态描述
 */
export function getThemeStatusText(theme) {
  if (!theme) return ''
  
  if (theme.isManual) {
    return `手动设置：${theme.name}`
  }
  
  if (theme.holiday) {
    return `${theme.holiday.name} - ${theme.name}主题`
  }
  
  return '默认主题'
}

/**
 * 判断当前是否为肃穆主题
 * @param {Object} theme - 当前主题
 * @returns {boolean}
 */
export function isSolemnTheme(theme) {
  return theme && theme.type === HolidayType.SOLEMN
}

/**
 * 判断当前是否显示装饰
 * @param {Object} theme - 当前主题
 * @returns {boolean}
 */
export function shouldShowDecorations(theme) {
  return theme && theme.decorations === true && theme.type !== HolidayType.SOLEMN
}

// 默认导出
export default {
  detectHoliday,
  getCurrentTheme,
  applyThemeCSS,
  clearThemeCSS,
  getAllThemes,
  getThemeStatusText,
  isSolemnTheme,
  shouldShowDecorations,
  HolidayType,
  ThemeMode,
  THEME_CONFIG
}
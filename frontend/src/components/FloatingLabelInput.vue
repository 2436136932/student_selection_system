<template>
  <div class="floating-label-input" :class="{ 'focused': isFocused, 'has-value': hasValue, 'error': error }">
    <label v-if="label" class="floating-label" :for="inputId">
      {{ label }}
      <span v-if="required" class="required-mark">*</span>
    </label>
    <input
      :id="inputId"
      ref="inputRef"
      :type="type"
      :value="modelValue"
      :placeholder="placeholder"
      :disabled="disabled"
      :readonly="readonly"
      :maxlength="maxlength"
      @input="handleInput"
      @focus="handleFocus"
      @blur="handleBlur"
      @keyup.enter="$emit('enter')"
      class="floating-input"
    />
    <div v-if="error" class="error-message">
      {{ error }}
    </div>
    <div v-if="hint && !error" class="hint-message">
      {{ hint }}
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'

const props = defineProps({
  modelValue: {
    type: [String, Number],
    default: ''
  },
  label: {
    type: String,
    default: ''
  },
  type: {
    type: String,
    default: 'text',
    validator: (value) => ['text', 'password', 'email', 'number', 'tel', 'url'].includes(value)
  },
  placeholder: {
    type: String,
    default: ''
  },
  disabled: {
    type: Boolean,
    default: false
  },
  readonly: {
    type: Boolean,
    default: false
  },
  required: {
    type: Boolean,
    default: false
  },
  maxlength: {
    type: Number,
    default: null
  },
  error: {
    type: String,
    default: ''
  },
  hint: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['update:modelValue', 'focus', 'blur', 'enter'])

const inputRef = ref(null)
const isFocused = ref(false)

const hasValue = computed(() => {
  return props.modelValue !== '' && props.modelValue !== null && props.modelValue !== undefined
})

const inputId = computed(() => {
  return `floating-input-${Math.random().toString(36).substr(2, 9)}`
})

const handleInput = (event) => {
  const value = event.target.value
  emit('update:modelValue', value)
}

const handleFocus = (event) => {
  isFocused.value = true
  emit('focus', event)
}

const handleBlur = (event) => {
  isFocused.value = false
  emit('blur', event)
}

const focus = () => {
  if (inputRef.value) {
    inputRef.value.focus()
  }
}

defineExpose({
  focus
})
</script>

<style scoped>
.floating-label-input {
  position: relative;
  margin-bottom: 24px;
}

.floating-label {
  position: absolute;
  left: 16px;
  top: 50%;
  transform: translateY(-50%);
  background: white;
  padding: 0 8px;
  font-size: 16px;
  color: #909399;
  transition: all 0.3s ease;
  pointer-events: none;
  z-index: 1;
}

.floating-label-input.focused .floating-label,
.floating-label-input.has-value .floating-label {
  top: 0;
  transform: translateY(-50%);
  font-size: 12px;
  color: #3498db;
  font-weight: 500;
}

.floating-label-input.error .floating-label {
  color: #e74c3c;
}

.floating-input {
  width: 100%;
  height: 56px;
  padding: 0 16px;
  font-size: 16px;
  color: #2c3e50;
  background: white;
  border: 2px solid #e4e7ed;
  border-radius: 10px;
  transition: all 0.3s ease;
  outline: none;
  font-family: 'Inter', 'Noto Sans SC', 'Microsoft YaHei', sans-serif;
}

.floating-input::placeholder {
  color: #bdc3c7;
  font-size: 16px;
}

.floating-input:hover {
  border-color: #3498db;
  box-shadow: 0 2px 8px rgba(52, 152, 219, 0.1);
}

.floating-input:focus {
  border-color: #3498db;
  box-shadow: 0 0 0 4px rgba(52, 152, 219, 0.1), 0 4px 12px rgba(52, 152, 219, 0.15);
}

.floating-input:disabled {
  background: #f5f7fa;
  border-color: #e4e7ed;
  color: #bdc3c7;
  cursor: not-allowed;
}

.floating-input:readonly {
  background: #f8f9fa;
  border-color: #e4e7ed;
  color: #606266;
}

.floating-label-input.error .floating-input {
  border-color: #e74c3c;
}

.floating-label-input.error .floating-input:focus {
  box-shadow: 0 0 0 4px rgba(231, 76, 60, 0.1), 0 4px 12px rgba(231, 76, 60, 0.15);
}

.required-mark {
  color: #e74c3c;
  margin-left: 2px;
  font-weight: bold;
}

.error-message {
  position: absolute;
  left: 0;
  bottom: -20px;
  font-size: 12px;
  color: #e74c3c;
  animation: slideIn 0.3s ease;
}

.hint-message {
  position: absolute;
  left: 0;
  bottom: -20px;
  font-size: 12px;
  color: #909399;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(-5px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .floating-input {
    height: 48px;
    font-size: 14px;
  }
  
  .floating-label {
    font-size: 14px;
  }
  
  .floating-label-input.focused .floating-label,
  .floating-label-input.has-value .floating-label {
    font-size: 11px;
  }
  
  .floating-label-input {
    margin-bottom: 20px;
  }
}
</style>

<template>
  <div class="filter-tags">
    <!-- 时间范围标签 -->
    <div class="tag-group">
      <span class="group-label">时间：</span>
      <button
        v-for="option in timeOptions"
        :key="option.value"
        class="tag-btn"
        :class="{ active: filters.timeRange === option.value }"
        @click="handleTimeChange(option.value)"
      >
        {{ option.label }}
      </button>
    </div>

    <!-- 行为类型标签 -->
    <div class="tag-group">
      <span class="group-label">类型：</span>
      <button
        v-for="option in typeOptions"
        :key="option.value"
        class="tag-btn"
        :class="{ active: filters.actionType === option.value }"
        @click="handleTypeChange(option.value)"
      >
        <span class="tag-icon">{{ option.icon }}</span>
        {{ option.label }}
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'

interface FilterOptions {
  agentName: string
  timeRange: string
  actionType: string
}

const filters = ref<FilterOptions>({
  agentName: '',
  timeRange: 'today',
  actionType: ''
})

// 时间选项
const timeOptions = [
  { label: '今天', value: 'today' },
  { label: '昨天', value: 'yesterday' },
  { label: '本周', value: 'week' },
  { label: '本月', value: 'month' },
  { label: '全部', value: 'all' }
]

// 类型选项
const typeOptions = [
  { label: '全部', value: '', icon: '🎯' },
  { label: '买酒', value: 'drink', icon: '🍺' },
  { label: '留言', value: 'message', icon: '💬' },
  { label: '涂鸦', value: 'selfie', icon: '🎨' },
  { label: '点赞', value: 'like', icon: '❤️' }
]

// 定义 emits
const emit = defineEmits<{
  'filter-change': [filters: FilterOptions]
}>()

// 处理时间变化
const handleTimeChange = (value: string) => {
  filters.value.timeRange = value
  emit('filter-change', { ...filters.value })
}

// 处理类型变化
const handleTypeChange = (value: string) => {
  filters.value.actionType = value
  emit('filter-change', { ...filters.value })
}

// 暴露方法供父组件调用
defineExpose({
  getFilters: () => ({ ...filters.value }),
  resetFilters: () => {
    filters.value = {
      agentName: '',
      timeRange: 'today',
      actionType: ''
    }
    emit('filter-change', { ...filters.value })
  }
})
</script>

<style scoped>
.filter-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 24px;
  padding: 16px 0;
  align-items: center;
}

.tag-group {
  display: flex;
  align-items: center;
  gap: 8px;
}

.group-label {
  font-size: 0.9rem;
  color: #B29273;
  font-weight: 500;
  white-space: nowrap;
}

.tag-btn {
  padding: 8px 16px;
  background: rgba(30, 20, 16, 0.6);
  border: 1px solid rgba(223, 154, 87, 0.2);
  border-radius: 20px;
  color: #B29273;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.25s ease;
  white-space: nowrap;
  display: flex;
  align-items: center;
  gap: 6px;
}

.tag-icon {
  font-size: 1rem;
}

.tag-btn:hover {
  background: rgba(223, 154, 87, 0.15);
  border-color: rgba(223, 154, 87, 0.4);
  color: #FCE2C1;
  transform: translateY(-2px);
}

.tag-btn.active {
  background: rgba(223, 154, 87, 0.25);
  border-color: #DF9A57;
  color: #DF9A57;
  box-shadow: 0 0 12px rgba(223, 154, 87, 0.3);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .filter-tags {
    gap: 16px;
    padding: 12px 0;
  }

  .tag-group {
    width: 100%;
  }

  .tag-btn {
    padding: 6px 12px;
    font-size: 0.85rem;
  }
}
</style>

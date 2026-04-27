<template>
  <section class="activity-timeline">
    <div class="timeline-container">
      <!-- 筛选标签 -->
      <slot name="filter"></slot>

      <h3 class="timeline-title">
        <span class="timeline-icon">️</span>
        活动流
        <span class="live-badge">
          <span class="live-dot"></span>
          LIVE
        </span>
      </h3>

      <div class="timeline-content" ref="timelineRef">
        <el-skeleton :loading="loading" animated>
          <template #template>
            <div v-for="i in 5" :key="i" class="skeleton-item">
              <el-skeleton-item variant="rect" style="width: 100%; height: 140px; border-radius: 16px;" />
            </div>
          </template>

          <template #default>
            <transition-group name="fade-up" tag="div" class="activity-list">
              <div
                v-for="activity in activities"
                :key="activity.id"
                class="activity-item"
              >
                <div class="activity-icon" :class="`icon-${activity.actionType}`">
                  {{ getActivityIcon(activity.actionType) }}
                </div>

                <div class="activity-card">
                  <div class="activity-header">
                    <div class="agent-info">
                      <span class="agent-name">{{ activity.agentNickname || activity.agentName }}</span>
                      <span class="action-type">{{ getActionTypeText(activity.actionType) }}</span>
                    </div>
                    <span class="activity-time">{{ formatTime(activity.timestamp) }}</span>
                  </div>

                  <div class="activity-body">
                    <p class="activity-text">{{ getActivityText(activity) }}</p>
                    <div v-if="activity.detailJson" class="activity-detail">
                      {{ parseDetail(activity.detailJson, activity.actionType) }}
                    </div>
                  </div>
                </div>
              </div>
            </transition-group>

            <!-- 加载更多 -->
            <div v-if="hasMore && !loading" class="load-more">
              <el-button @click="loadMore" :loading="loadingMore" class="load-more-btn">
                <el-icon v-if="!loadingMore"><MoreFilled /></el-icon>
                {{ loadingMore ? '更多回忆正在浮现…' : '加载更多回忆' }}
              </el-button>
            </div>

            <!-- 空状态 -->
            <div v-if="activities.length === 0 && !loading" class="empty-state">
              <el-icon class="empty-icon"><Clock /></el-icon>
              <p class="empty-text">暂无活动记录</p>
              <p class="empty-hint">Agent 们的活动将会在这里实时展示</p>
            </div>
          </template>
        </el-skeleton>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { Star, MoreFilled, Clock } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import relativeTime from 'dayjs/plugin/relativeTime'
import 'dayjs/locale/zh-cn'
import { getTavernActivityStream, type ActivityStreamVO } from '@/api/aworld/tavern'

dayjs.extend(relativeTime)
dayjs.locale('zh-cn')

const activities = ref<ActivityStreamVO[]>([])
const loading = ref(false)
const loadingMore = ref(false)
const hasMore = ref(true)
const timelineRef = ref<HTMLElement | null>(null)
const page = ref(0)
const pageSize = 20

// 筛选条件（从父组件接收）
const filters = ref({
  timeRange: 'today' as 'today' | 'yesterday' | 'week' | 'month' | 'all',
  actionType: undefined as 'drink' | 'message' | 'selfie' | 'like' | undefined,
  agentName: undefined as string | undefined
})

// 定义 emits
const emit = defineEmits<{
  'activity-click': [activity: ActivityStreamVO]
}>()

// 获取活动图标
const getActivityIcon = (type: string) => {
  const icons: Record<string, string> = {
    drink: '🍺',
    message: '💬',
    selfie: '🎨',
    like: '❤️',
    register: '🎉'
  }
  return icons[type] || '✨'
}

// 获取行为类型文本
const getActionTypeText = (type: string) => {
  const texts: Record<string, string> = {
    drink: '买了杯酒',
    message: '留言',
    selfie: '创作涂鸦',
    like: '点赞',
    register: '加入酒馆'
  }
  return texts[type] || '活动'
}

// 获取活动描述
const getActivityText = (activity: ActivityStreamVO) => {
  const texts: Record<string, string> = {
    drink: '在吧台点了一杯特调',
    message: '在留言板上写下了一段话',
    selfie: '用 AI 创作了一幅涂鸦',
    like: '为精彩内容点赞',
    register: '第一次来到酒馆'
  }
  return texts[activity.actionType] || '进行了某个活动'
}

// 解析详情
const parseDetail = (detailJson: string, actionType: string) => {
  try {
    const detail = JSON.parse(detailJson)
    if (actionType === 'drink') {
      return detail.drink_name ? `🍷 ${detail.drink_name}` : ''
    }
    if (actionType === 'message') {
      return detail.content ? `"${detail.content}"` : ''
    }
    if (actionType === 'selfie') {
      return detail.title ? `🎨 ${detail.title}` : ''
    }
    return ''
  } catch {
    return ''
  }
}

// 格式化时间
const formatTime = (timestamp: string) => {
  const now = dayjs()
  const time = dayjs(timestamp)
  const diff = now.diff(time, 'minute')

  if (diff < 1) return '刚刚'
  if (diff < 60) return `${diff} 分钟前`
  if (diff < 1440) return `${Math.floor(diff / 60)} 小时前`
  
  return time.format('MM-DD HH:mm')
}

// 加载活动数据
const loadActivities = async (isLoadMore = false) => {
  try {
    if (isLoadMore) {
      loadingMore.value = true
    } else {
      loading.value = true
    }

    const params = {
      timeRange: filters.value.timeRange,
      actionType: filters.value.actionType,
      agentName: filters.value.agentName,
      limit: pageSize,
      offset: page.value * pageSize
    }

    const res = await getTavernActivityStream(params)
    const newActivities = res.data.items || []

    if (isLoadMore) {
      activities.value = [...activities.value, ...newActivities]
    } else {
      activities.value = newActivities
    }

    page.value++
    hasMore.value = activities.value.length < res.data.total
  } catch (error) {
    console.error('Failed to load activities:', error)
  } finally {
    loading.value = false
    loadingMore.value = false
  }
}

// 加载更多
const loadMore = () => {
  loadActivities(true)
}

// 刷新数据
const refresh = () => {
  page.value = 0
  hasMore.value = true
  loadActivities(false)
}

// 更新筛选条件
const updateFilters = (newFilters: any) => {
  if (newFilters.timeRange !== undefined) {
    filters.value.timeRange = newFilters.timeRange
  }
  if (newFilters.actionType !== undefined) {
    filters.value.actionType = newFilters.actionType
  }
  if (newFilters.agentName !== undefined) {
    filters.value.agentName = newFilters.agentName
  }
  // 重置分页并重新加载
  refresh()
}

// 暴露方法
defineExpose({
  refresh,
  loadMore,
  updateFilters
})

onMounted(() => {
  loadActivities(false)
})

onUnmounted(() => {
  // 清理工作
})
</script>

<style scoped>
.activity-timeline {
  margin-bottom: 32px;
}

.timeline-container {
  max-width: 1200px;
  margin: 0 auto;
}

.timeline-title {
  font-size: 1.5rem;
  color: #FCE2C1;
  margin: 0 0 24px 0;
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 12px;
}

.timeline-icon {
  font-size: 1.3rem;
}

.live-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 4px 12px;
  background: rgba(200, 76, 61, 0.2);
  border: 1px solid rgba(200, 76, 61, 0.4);
  border-radius: 20px;
  color: #C84C3D;
  font-size: 0.85rem;
  font-weight: 600;
  letter-spacing: 1px;
}

.live-dot {
  width: 8px;
  height: 8px;
  background: #C84C3D;
  border-radius: 50%;
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
    transform: scale(1);
  }
  50% {
    opacity: 0.5;
    transform: scale(1.2);
  }
}

.timeline-content {
  max-height: 600px;
  overflow-y: auto;
  padding-right: 8px;
}

.timeline-content::-webkit-scrollbar {
  width: 4px;
}

.timeline-content::-webkit-scrollbar-track {
  background: rgba(223, 154, 87, 0.1);
  border-radius: 2px;
}

.timeline-content::-webkit-scrollbar-thumb {
  background: rgba(223, 154, 87, 0.3);
  border-radius: 2px;
}

.timeline-content::-webkit-scrollbar-thumb:hover {
  background: rgba(223, 154, 87, 0.5);
}

.skeleton-item {
  margin-bottom: 16px;
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.activity-item {
  display: flex;
  gap: 16px;
  animation: fade-in-up 0.3s ease;
}

@keyframes fade-in-up {
  from {
    opacity: 0;
    transform: translateY(8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.activity-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
  background: rgba(30, 20, 16, 0.8);
  border: 1px solid rgba(223, 154, 87, 0.3);
  flex-shrink: 0;
}

.activity-card {
  flex: 1;
  background: transparent;
  border: none;
  border-radius: 16px;
  padding: 16px 20px;
  transition: all 0.25s ease;
}

.activity-card:hover {
  background: rgba(223, 154, 87, 0.05);
  transform: translateX(4px);
}

.activity-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.agent-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.agent-name {
  color: #FCE2C1;
  font-weight: 600;
  font-size: 1rem;
}

.action-type {
  color: #B29273;
  font-size: 0.85rem;
}

.activity-time {
  color: #B29273;
  font-size: 0.85rem;
  white-space: nowrap;
}

.activity-body {
  margin-bottom: 8px;
}

.activity-text {
  color: #FCE2C1;
  font-size: 0.95rem;
  margin: 0 0 8px 0;
  line-height: 1.6;
}

.activity-detail {
  color: #DF9A57;
  font-size: 0.9rem;
  font-style: italic;
  padding: 8px 12px;
  background: rgba(223, 154, 87, 0.1);
  border-radius: 8px;
  border-left: 3px solid #DF9A57;
}

.activity-footer {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #B29273;
  font-size: 0.85rem;
}

.likes-count {
  display: flex;
  align-items: center;
  gap: 4px;
}

.load-more {
  text-align: center;
  margin-top: 24px;
}

.load-more-btn {
  background: rgba(223, 154, 87, 0.15);
  border: 1px solid rgba(223, 154, 87, 0.3);
  color: #DF9A57;
  border-radius: 24px;
  padding: 12px 32px;
  font-size: 0.95rem;
  transition: all 0.25s ease;

  &:hover {
    background: rgba(223, 154, 87, 0.25);
    border-color: #DF9A57;
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(223, 154, 87, 0.3);
  }
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
}

.empty-icon {
  font-size: 3rem;
  color: #B29273;
  margin-bottom: 16px;
}

.empty-text {
  color: #FCE2C1;
  font-size: 1.1rem;
  margin: 0 0 8px 0;
}

.empty-hint {
  color: #B29273;
  font-size: 0.9rem;
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .activity-timeline {
    padding: 20px;
  }

  .timeline-title {
    font-size: 1.2rem;
  }

  .activity-item {
    gap: 12px;
  }

  .activity-icon {
    width: 40px;
    height: 40px;
    font-size: 1.2rem;
  }

  .activity-card {
    padding: 12px 16px;
  }
}
</style>

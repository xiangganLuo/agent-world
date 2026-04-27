<template>
  <section class="stats-panel">
    <div class="stats-container">
      <h3 class="stats-title">
        <span class="stats-icon">📊</span>
        今日统计
      </h3>

      <div class="stats-grid">
        <!-- 买酒次数 -->
        <div class="stat-card drink-card">
          <div class="stat-icon">🍺</div>
          <div class="stat-content">
            <span class="stat-value">{{ stats.drinkCount }}</span>
            <span class="stat-label">买酒次数</span>
          </div>
          <div class="stat-trend" v-if="stats.drinkTrend">
            <span :class="stats.drinkTrend > 0 ? 'trend-up' : 'trend-down'">
              {{ stats.drinkTrend > 0 ? '↑' : '↓' }} {{ Math.abs(stats.drinkTrend) }}%
            </span>
          </div>
        </div>

        <!-- 留言数量 -->
        <div class="stat-card message-card">
          <div class="stat-icon">💬</div>
          <div class="stat-content">
            <span class="stat-value">{{ stats.messageCount }}</span>
            <span class="stat-label">留言数量</span>
          </div>
          <div class="stat-trend" v-if="stats.messageTrend">
            <span :class="stats.messageTrend > 0 ? 'trend-up' : 'trend-down'">
              {{ stats.messageTrend > 0 ? '↑' : '↓' }} {{ Math.abs(stats.messageTrend) }}%
            </span>
          </div>
        </div>

        <!-- 涂鸦数量 -->
        <div class="stat-card selfie-card">
          <div class="stat-icon">🎨</div>
          <div class="stat-content">
            <span class="stat-value">{{ stats.selfieCount }}</span>
            <span class="stat-label">涂鸦数量</span>
          </div>
          <div class="stat-trend" v-if="stats.selfieTrend">
            <span :class="stats.selfieTrend > 0 ? 'trend-up' : 'trend-down'">
              {{ stats.selfieTrend > 0 ? '↑' : '↓' }} {{ Math.abs(stats.selfieTrend) }}%
            </span>
          </div>
        </div>

        <!-- 活跃 Agent -->
        <div class="stat-card active-card">
          <div class="stat-icon">👥</div>
          <div class="stat-content">
            <span class="stat-value">{{ stats.activeAgents }}</span>
            <span class="stat-label">活跃 Agent</span>
          </div>
          <div class="stat-trend" v-if="stats.activeTrend">
            <span :class="stats.activeTrend > 0 ? 'trend-up' : 'trend-down'">
              {{ stats.activeTrend > 0 ? '↑' : '↓' }} {{ Math.abs(stats.activeTrend) }}%
            </span>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import dayjs from 'dayjs'

interface TavernStats {
  drinkCount: number
  drinkTrend?: number
  messageCount: number
  messageTrend?: number
  selfieCount: number
  selfieTrend?: number
  activeAgents: number
  activeTrend?: number
}

const stats = ref<TavernStats>({
  drinkCount: 0,
  messageCount: 0,
  selfieCount: 0,
  activeAgents: 0
})

const refreshing = ref(false)
let refreshTimer: number

// 加载统计数据
const loadStats = async () => {
  try {
    refreshing.value = true

    // TODO: 调用 API 获取酒馆统计数据
    // const response = await getTavernStats()
    // stats.value = response.data

    // Mock 数据
    stats.value = {
      drinkCount: 156,
      drinkTrend: 12.5,
      messageCount: 89,
      messageTrend: -3.2,
      selfieCount: 34,
      selfieTrend: 25.8,
      activeAgents: 47,
      activeTrend: 8.3
    }
  } catch (error) {
    console.error('Failed to load stats:', error)
  } finally {
    refreshing.value = false
  }
}

// 手动刷新
const handleRefresh = () => {
  loadStats()
}

// 暴露方法
defineExpose({
  refresh: loadStats
})

onMounted(() => {
  loadStats()
  
  // 每小时自动刷新
  refreshTimer = window.setInterval(() => {
    loadStats()
  }, 3600000)
})

onUnmounted(() => {
  if (refreshTimer) {
    clearInterval(refreshTimer)
  }
})
</script>

<style scoped>
.stats-panel {
  margin-bottom: 32px;
}

.stats-container {
  max-width: 1200px;
  margin: 0 auto;
}

.stats-title {
  font-size: 1.5rem;
  color: #FCE2C1;
  margin: 0 0 24px 0;
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 12px;
}

.stats-icon {
  font-size: 1.3rem;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  position: relative;
  background: transparent;
  border: none;
  border-radius: 20px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  transition: all 0.25s ease;
  overflow: hidden;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: var(--accent-color);
  opacity: 0.6;
}

.stat-card:hover {
  transform: translateY(-4px);
  background: rgba(223, 154, 87, 0.05);
}

.drink-card {
  --accent-color: #DF9A57;
}

.message-card {
  --accent-color: #A78BFA;
}

.selfie-card {
  --accent-color: #FFB347;
}

.active-card {
  --accent-color: #60A5FA;
}

.stat-icon {
  font-size: 2.5rem;
  flex-shrink: 0;
}

.stat-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.stat-value {
  font-size: 2rem;
  font-weight: 800;
  color: #FCE2C1;
  line-height: 1;
}

.stat-label {
  font-size: 0.9rem;
  color: #B29273;
  font-weight: 500;
}

.stat-trend {
  position: absolute;
  top: 12px;
  right: 12px;
  font-size: 0.8rem;
  font-weight: 600;
}

.trend-up {
  color: #10B981;
}

.trend-down {
  color: #EF4444;
}

.stats-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 16px;
  border-top: 1px solid rgba(223, 154, 87, 0.15);
  color: #B29273;
  font-size: 0.85rem;
  gap: 12px;
}

.refresh-btn {
  background: rgba(223, 154, 87, 0.15);
  border: 1px solid rgba(223, 154, 87, 0.3);
  color: #DF9A57;
  border-radius: 12px;
  padding: 6px 16px;
  transition: all 0.25s ease;

  &:hover {
    background: rgba(223, 154, 87, 0.25);
    border-color: #DF9A57;
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .stats-panel {
    padding: 20px;
  }

  .stats-title {
    font-size: 1.2rem;
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }

  .stat-card {
    padding: 16px;
  }

  .stat-icon {
    font-size: 2rem;
  }

  .stat-value {
    font-size: 1.5rem;
  }

  .stats-footer {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
}
</style>

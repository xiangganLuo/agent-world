<template>
  <div class="tavern-page">
    <!-- 顶部导航栏 -->
    <nav class="top-nav">
      <div class="nav-container">
        <div class="logo">
          <span class="logo-icon">🍺</span>
          <span class="logo-text">赛博酒馆</span>
        </div>
        <div class="nav-links">
          <a href="https://github.com/your-repo" target="_blank" class="nav-link github-link">
            <el-icon><Platform /></el-icon>
            GitHub
          </a>
        </div>
      </div>
    </nav>
    
    <!-- 酒馆氛围区 -->
    <AtmosphereSection />
    
    <!-- 主内容区 -->
    <div class="tavern-content">
      <!-- 酒单展示 -->
      <DrinkMenu />
      
      <!-- 统计面板 -->
      <StatsPanel ref="statsPanelRef" />
      
      <!-- 活动流时间线（包含筛选标签） -->
      <ActivityTimeline ref="timelineRef">
        <template #filter>
          <FilterPanel ref="filterPanelRef" @filter-change="handleFilterChange" />
        </template>
      </ActivityTimeline>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Platform } from '@element-plus/icons-vue'
import AtmosphereSection from './components/AtmosphereSection.vue'
import DrinkMenu from './components/DrinkMenu.vue'
import FilterPanel from './components/FilterPanel.vue'
import ActivityTimeline from './components/ActivityTimeline.vue'
import StatsPanel from './components/StatsPanel.vue'

const statsPanelRef = ref()
const filterPanelRef = ref()
const timelineRef = ref()

// 处理筛选条件变化
const handleFilterChange = (filters: any) => {
  console.log('筛选条件变化:', filters)
  // 更新活动流的筛选条件并刷新
  timelineRef.value?.updateFilters(filters)
}
</script>

<style scoped>
.tavern-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #1E1410 0%, #2A1E18 100%);
}

/* 顶部导航栏 */
.top-nav {
  background: rgba(30, 20, 16, 0.9);
  backdrop-filter: blur(8px);
  border-bottom: 1px solid rgba(223, 154, 87, 0.2);
  padding: 16px 0;
  position: sticky;
  top: 0;
  z-index: 100;
}

.nav-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 40px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 1.3rem;
  font-weight: 700;
  color: #FCE2C1;
}

.logo-icon {
  font-size: 1.5rem;
}

.logo-text {
  letter-spacing: 1px;
}

.nav-links {
  display: flex;
  gap: 12px;
}

.nav-link {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  color: #B29273;
  text-decoration: none;
  border-radius: 8px;
  transition: all 0.25s ease;
  font-size: 0.95rem;
}

.nav-link:hover {
  color: #DF9A57;
  background: rgba(223, 154, 87, 0.1);
}

.github-link {
  border: 1px solid rgba(223, 154, 87, 0.3);
}

.tavern-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 40px 20px;
}

@media (max-width: 768px) {
  .nav-container {
    padding: 0 20px;
  }

  .logo-text {
    font-size: 1.1rem;
  }
}
</style>

<template>
  <nav class="tavern-nav">
    <div class="nav-container">
      <div class="nav-brand">
        <span class="brand-icon">🍺</span>
        <span class="brand-text">赛博酒馆</span>
      </div>
      
      <ul class="nav-menu">
        <li 
          v-for="item in menuItems" 
          :key="item.path"
          class="nav-item"
          :class="{ active: currentPath === item.path }"
        >
          <router-link :to="item.path" class="nav-link">
            <span class="link-icon">{{ item.icon }}</span>
            <span class="link-text">{{ item.label }}</span>
          </router-link>
        </li>
      </ul>
    </div>
  </nav>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const menuItems = [
  { path: '/', label: '首页', icon: '🏠' },
  { path: '/tavern', label: '酒馆', icon: '🍺' },
  { path: '/guide', label: '加入世界', icon: '✨' }
]

const currentPath = computed(() => route.path)
</script>

<style scoped>
.tavern-nav {
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

.nav-brand {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 1.3rem;
  font-weight: 700;
  color: #FCE2C1;
}

.brand-icon {
  font-size: 1.5rem;
}

.brand-text {
  letter-spacing: 1px;
}

.nav-menu {
  display: flex;
  list-style: none;
  margin: 0;
  padding: 0;
  gap: 8px;
}

.nav-item {
  position: relative;
}

.nav-link {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  color: #B29273;
  text-decoration: none;
  border-radius: 12px;
  transition: all 0.25s ease;
  font-weight: 500;
}

.link-icon {
  font-size: 1.1rem;
}

.link-text {
  white-space: nowrap;
}

.nav-link:hover {
  color: #FCE2C1;
  background: rgba(223, 154, 87, 0.1);
}

.nav-item.active .nav-link {
  color: #DF9A57;
  background: rgba(223, 154, 87, 0.15);
  box-shadow: 0 0 20px rgba(223, 154, 87, 0.2);
}

.nav-item.active .nav-link::after {
  content: '';
  position: absolute;
  bottom: -16px;
  left: 50%;
  transform: translateX(-50%);
  width: 30px;
  height: 3px;
  background: #DF9A57;
  border-radius: 2px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .nav-container {
    padding: 0 20px;
  }

  .nav-brand {
    font-size: 1.1rem;
  }

  .brand-text {
    display: none;
  }

  .nav-menu {
    gap: 4px;
  }

  .nav-link {
    padding: 8px 12px;
    font-size: 0.9rem;
  }

  .link-text {
    display: none;
  }

  .link-icon {
    font-size: 1.3rem;
  }
}
</style>

<template>
  <section class="drink-menu">
    <div class="menu-container">
      <h3 class="menu-title">
        <span class="menu-icon">🍷</span>
        今日酒单
        <span class="menu-subtitle">Today's Special</span>
      </h3>

      <div class="drinks-wrapper">
        <div class="drinks-scroll" ref="scrollRef">
          <div
            v-for="(drink, index) in drinks"
            :key="drink.id"
            class="drink-card"
            :class="`drink-${index + 1}`"
          >
            <div class="drink-badge" v-if="drink.isSpecial">
              <span class="badge-icon">⭐</span>
              <span class="badge-text">特调</span>
            </div>
            
            <div class="drink-icon">{{ drink.icon }}</div>
            
            <div class="drink-info">
              <h4 class="drink-name">{{ drink.name }}</h4>
              <p class="drink-desc">{{ drink.description }}</p>
              <div class="drink-meta">
                <span class="drink-price">💰 {{ drink.price }}</span>
                <span class="drink-abv">🍺 {{ drink.abv }}%</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { getDrinkList, type DrinkVO } from '@/api/aworld/tavern'

const scrollRef = ref<HTMLElement | null>(null)
const showScrollIndicator = ref(false)
const loading = ref(false)

// 计算可见卡片数量（根据容器宽度）
const visibleCount = computed(() => {
  if (typeof window === 'undefined') return 6
  const containerWidth = window.innerWidth - 80 // 减去左右 padding
  const cardWidth = 200 // 卡片宽度 + gap
  return Math.floor(containerWidth / cardWidth)
})

// 酒品图标映射
const drinkIconMap: Record<string, string> = {
  'whiskey': '🥃',
  'vodka': '🍸',
  'beer': '🍺',
  'wine': '🍷',
  'champagne': '🥂',
  'cocktail': '🍹',
  'coffee': '☕',
  'sake': '🍶',
  'default': '🍷'
}

// 获取酒品图标
const getDrinkIcon = (drinkCode: string): string => {
  const code = drinkCode.toLowerCase()
  for (const [key, icon] of Object.entries(drinkIconMap)) {
    if (code.includes(key)) return icon
  }
  return drinkIconMap.default
}

const drinks = ref<DrinkVO[]>([])

// 加载酒单数据
const loadDrinks = async () => {
  try {
    loading.value = true
    const res = await getDrinkList()
    
    // 响应拦截器已返回内层 data 字段（酒品数组）
    drinks.value = (res || []).map(drink => ({
      ...drink,
      price: String(drink.price || 0),
      abv: drink.alcoholPct,
      icon: getDrinkIcon(drink.drinkCode),
      isSpecial: drink.effects?.clarity && drink.effects.clarity > 5
    }))
    
    // 检测是否需要显示滚动指示器
    setTimeout(() => {
      if (scrollRef.value) {
        showScrollIndicator.value = scrollRef.value.scrollWidth > scrollRef.value.clientWidth
      }
    }, 100)
  } catch (error) {
    console.error('Failed to load drinks:', error)
  } finally {
    loading.value = false
  }
}

// 自动滚动
let autoScrollTimer: number
let scrollDirection = 1
const scrollSpeed = 1 // 滚动速度（像素/帧）

const startAutoScroll = () => {
  if (!scrollRef.value) return
  
  autoScrollTimer = window.setInterval(() => {
    if (!scrollRef.value) return
    
    const { scrollLeft, scrollWidth, clientWidth } = scrollRef.value
    const maxScroll = scrollWidth - clientWidth
    
    // 到达边界时反向
    if (scrollLeft >= maxScroll) {
      scrollDirection = -1
    } else if (scrollLeft <= 0) {
      scrollDirection = 1
    }
    
    // 匀速滚动
    scrollRef.value.scrollLeft += scrollDirection * scrollSpeed
  }, 16) // 约 60fps
}

const stopAutoScroll = () => {
  if (autoScrollTimer) {
    clearInterval(autoScrollTimer)
  }
}

onMounted(() => {
  // 加载酒单数据
  loadDrinks()
  
  // 启动自动滚动
  startAutoScroll()
  
  // 鼠标悬停时暂停自动滚动
  if (scrollRef.value) {
    scrollRef.value.addEventListener('mouseenter', stopAutoScroll)
    scrollRef.value.addEventListener('mouseleave', startAutoScroll)
  }
})

onUnmounted(() => {
  stopAutoScroll()
  if (scrollRef.value) {
    scrollRef.value.removeEventListener('mouseenter', stopAutoScroll)
    scrollRef.value.removeEventListener('mouseleave', startAutoScroll)
  }
})
</script>

<style scoped>
.drink-menu {
  margin-bottom: 40px;
}

.menu-container {
  max-width: 1200px;
  margin: 0 auto;
}

.menu-title {
  font-size: 1.5rem;
  color: #FCE2C1;
  margin: 0 0 24px 0;
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 12px;
}

.menu-icon {
  font-size: 1.3rem;
}

.menu-subtitle {
  font-size: 0.9rem;
  color: #B29273;
  font-weight: 400;
  margin-left: 8px;
}

.drinks-wrapper {
  position: relative;
}

.drinks-scroll {
  display: flex;
  gap: 16px;
  overflow-x: auto;
  scrollbar-width: none; /* Firefox */
  -ms-overflow-style: none; /* IE and Edge */
  padding: 8px 4px;
  cursor: grab;
}

.drinks-scroll::-webkit-scrollbar {
  display: none; /* Chrome, Safari, Opera */
}

.drinks-scroll:active {
  cursor: grabbing;
}

.drink-card {
  flex: 0 0 180px;
  position: relative;
  background: rgba(30, 20, 16, 0.4);
  border: 1px solid rgba(223, 154, 87, 0.15);
  border-radius: 16px;
  padding: 20px;
  transition: all 0.25s ease;
  cursor: pointer;
  user-select: none;
}

.drink-card:hover {
  transform: translateY(-4px);
  background: rgba(30, 20, 16, 0.6);
  border-color: rgba(223, 154, 87, 0.3);
  box-shadow: 0 8px 24px rgba(223, 154, 87, 0.15);
}

.drink-badge {
  position: absolute;
  top: -8px;
  right: 12px;
  background: linear-gradient(135deg, #DF9A57 0%, #F0C080 100%);
  color: #1E1410;
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 4px;
  box-shadow: 0 2px 8px rgba(223, 154, 87, 0.4);
}

.badge-icon {
  font-size: 0.8rem;
}

.drink-icon {
  font-size: 2.5rem;
  margin-bottom: 12px;
  display: block;
}

.drink-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.drink-name {
  font-size: 1rem;
  color: #FCE2C1;
  margin: 0;
  font-weight: 600;
  line-height: 1.3;
}

.drink-desc {
  font-size: 0.8rem;
  color: #B29273;
  margin: 0;
  line-height: 1.5;
  flex: 1;
}

.drink-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 8px;
  border-top: 1px solid rgba(223, 154, 87, 0.1);
}

.drink-price {
  font-size: 0.95rem;
  color: #DF9A57;
  font-weight: 700;
}

.drink-abv {
  font-size: 0.8rem;
  color: #B29273;
}

/* 滚动指示器 */
.scroll-indicator {
  display: flex;
  justify-content: center;
  gap: 8px;
  margin-top: 16px;
}

.indicator-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: rgba(223, 154, 87, 0.3);
  transition: all 0.25s ease;
}

.indicator-dot:first-child {
  background: #DF9A57;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .drink-menu {
    margin-bottom: 32px;
  }

  .menu-title {
    font-size: 1.2rem;
  }

  .drinks-scroll {
    gap: 12px;
  }

  .drink-card {
    flex: 0 0 160px;
    padding: 16px;
  }

  .drink-icon {
    font-size: 2rem;
  }

  .drink-name {
    font-size: 0.9rem;
  }

  .drink-desc {
    font-size: 0.75rem;
  }
}
</style>

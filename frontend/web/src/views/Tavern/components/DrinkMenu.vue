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
        
        <!-- 滚动指示器 -->
        <div class="scroll-indicator" v-if="showScrollIndicator">
          <span class="indicator-dot" v-for="i in Math.ceil(drinks.length / visibleCount)" :key="i"></span>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue'

interface Drink {
  id: number
  name: string
  description: string
  price: string
  abv: number
  icon: string
  isSpecial?: boolean
}

const scrollRef = ref<HTMLElement | null>(null)
const showScrollIndicator = ref(false)

// 计算可见卡片数量（根据容器宽度）
const visibleCount = computed(() => {
  if (typeof window === 'undefined') return 6
  const containerWidth = window.innerWidth - 80 // 减去左右 padding
  const cardWidth = 200 // 卡片宽度 + gap
  return Math.floor(containerWidth / cardWidth)
})

const drinks = ref<Drink[]>([
  {
    id: 1,
    name: '赛博朋克特调',
    description: '霓虹蓝与数字橙的完美融合，带着微弱的电流刺激',
    price: '28',
    abv: 12,
    icon: '🍸',
    isSpecial: true
  },
  {
    id: 2,
    name: '量子莫吉托',
    description: '薄荷与青柠在量子态中纠缠，每一口都是惊喜',
    price: '25',
    abv: 8,
    icon: '🍹'
  },
  {
    id: 3,
    name: '数字威士忌',
    description: '经过算法陈酿的琥珀色液体，回味悠长',
    price: '35',
    abv: 40,
    icon: '🥃'
  },
  {
    id: 4,
    name: '虚拟现实IPA',
    description: '啤酒花的香气在虚拟空间中无限放大',
    price: '22',
    abv: 6,
    icon: '🍺'
  },
  {
    id: 5,
    name: '神经网络红酒',
    description: '深度学习酿造的红酒，层次丰富如神经网络',
    price: '42',
    abv: 14,
    icon: '🍷',
    isSpecial: true
  },
  {
    id: 6,
    name: '区块链白兰地',
    description: '不可篡改的经典配方，每一滴都可追溯',
    price: '38',
    abv: 38,
    icon: '🥂'
  },
  {
    id: 7,
    name: '云端拿铁',
    description: '数据流萃取的咖啡，带着云端的清香',
    price: '18',
    abv: 0,
    icon: '☕'
  },
  {
    id: 8,
    name: '二进制鸡尾酒',
    description: '0和1的完美配比，逻辑与感性的平衡',
    price: '30',
    abv: 15,
    icon: '🍸'
  },
  {
    id: 9,
    name: '光纤啤酒',
    description: '光速发酵的金色液体，气泡如数据包般跳跃',
    price: '20',
    abv: 5,
    icon: '🍺'
  },
  {
    id: 10,
    name: 'API 朗姆酒',
    description: '接口调用般的顺滑口感，余韵悠长',
    price: '32',
    abv: 35,
    icon: '🥃'
  },
  {
    id: 11,
    name: '数据库香槟',
    description: '结构化存储的气泡，查询即饮',
    price: '45',
    abv: 12,
    icon: '🥂',
    isSpecial: true
  },
  {
    id: 12,
    name: '递归清酒',
    description: '层层嵌套的米香，回味无穷',
    price: '26',
    abv: 16,
    icon: '🍶'
  }
])

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
  // 检测是否需要显示滚动指示器
  if (scrollRef.value) {
    showScrollIndicator.value = scrollRef.value.scrollWidth > scrollRef.value.clientWidth
  }
  
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

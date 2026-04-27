<template>
  <div class="agent-sphere-container">
    <div class="sphere" ref="sphereRef">
      <div
        v-for="(agent, index) in agents"
        :key="agent.name"
        class="agent-tag"
        :style="getAgentPosition(agent, index)"
      >
        {{ agent.name }}
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'

interface Agent {
  name: string
  x: number
  y: number
  z: number
}

const sphereRef = ref<HTMLElement | null>(null)
const agents = ref<Agent[]>([])
let rotationAngle = 0
let animationFrameId: number

// Mock 数据 - 智能体名字列表
const agentNames = [
  'Nexus', 'CyberPunk', 'NeoMind', 'QuantumX',
  'DataFlow', 'NeuralNet', 'SmartBot', 'AIThinker', 'RoboMind',
  'DeepLearn', 'VisionAI', 'LangChain', 'AutoAgent', 'MindForge',
  'Echo', 'Phoenix', 'Shadow', 'Blaze', 'Frost',
  'Thunder', 'Storm', 'Nova', 'Pulse', 'Vortex',
  'Zenith', 'Apex', 'Flux', 'Orbit', 'Spark',
  'Agent_Alpha', 'Beta_Bot', 'Gamma_AI', 'Delta_Mind', 'Epsilon_X',
  'Zeta_Flow', 'Eta_Core', 'Theta_Net', 'Iota_Sys', 'Kappa_Link',
  'Lambda_Ops', 'Mu_Tech', 'Nu_Data', 'Xi_Cloud', 'Omicron_Lab',
  'Sigma', 'Tau', 'Upsilon', 'Phi', 'Chi',
  'Psi', 'Omega', 'Prime', 'Matrix', 'Vector'
]

// 计算球面上点的坐标（使用 Fibonacci Sphere 算法）
const calculateSpherePositions = (count: number, radius: number): Agent[] => {
  const positions: Agent[] = []
  const goldenAngle = Math.PI * (3 - Math.sqrt(5)) // 黄金角度
  
  for (let i = 0; i < count; i++) {
    const y = 1 - (i / (count - 1)) * 2 // y 从 1 到 -1
    const radiusAtY = Math.sqrt(1 - y * y) // 当前 y 高度的半径
    const theta = goldenAngle * i // 角度
    
    const x = Math.cos(theta) * radiusAtY
    const z = Math.sin(theta) * radiusAtY
    
    positions.push({
      name: agentNames[i % agentNames.length],
      x: x * radius,
      y: y * radius,
      z: z * radius
    })
  }
  
  return positions
}

// 获取 Agent 的位置样式
const getAgentPosition = (agent: Agent, index: number) => {
  const cosAngle = Math.cos(rotationAngle)
  const sinAngle = Math.sin(rotationAngle)
  
  // 绕 Y 轴旋转
  const rotatedX = agent.x * cosAngle - agent.z * sinAngle
  const rotatedZ = agent.x * sinAngle + agent.z * cosAngle
  
  // 透视效果：z 轴影响透明度和缩放
  const scale = 0.6 + (rotatedZ + 200) / 400 * 0.4
  const opacity = 0.3 + (rotatedZ + 200) / 400 * 0.7
  
  return {
    transform: `translate3d(${rotatedX}px, ${agent.y}px, ${rotatedZ}px) scale(${scale})`,
    opacity: Math.max(0.2, Math.min(1, opacity)),
    zIndex: Math.round(rotatedZ + 200)
  }
}

// 动画循环
const animate = () => {
  rotationAngle += 0.005 // 旋转速度
  agents.value = [...agents.value] // 触发更新
  animationFrameId = requestAnimationFrame(animate)
}

onMounted(() => {
  if (sphereRef.value) {
    // 球体半径根据容器大小动态计算
    const containerWidth = sphereRef.value.parentElement?.offsetWidth || 500
    const containerHeight = sphereRef.value.parentElement?.offsetHeight || 500
    const radius = Math.min(containerWidth, containerHeight) / 2 - 60
    
    // 根据球体大小计算 Agent 数量（球越大，Agent 越多）
    const agentCount = Math.max(30, Math.min(60, Math.floor(radius / 8)))
    
    agents.value = calculateSpherePositions(agentCount, radius)
    animate()
  }
})

onUnmounted(() => {
  if (animationFrameId) {
    cancelAnimationFrame(animationFrameId)
  }
})
</script>

<style scoped>
.agent-sphere-container {
  width: 100%;
  height: 100%;
  max-height: 600px;
  position: relative;
  perspective: 1200px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.sphere {
  width: 100%;
  height: 100%;
  position: relative;
  transform-style: preserve-3d;
}

.agent-tag {
  position: absolute;
  left: 50%;
  top: 50%;
  padding: 6px 12px;
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: 16px;
  font-size: 0.85rem;
  color: var(--text-secondary);
  white-space: nowrap;
  transition: all 0.3s ease;
  cursor: default;
  transform-origin: center center;
}

.agent-tag:hover {
  background: var(--border-color);
  border-color: var(--primary-color);
  color: var(--primary-light);
  transform: scale(1.2) !important;
  opacity: 1 !important;
  z-index: 9999 !important;
}

/* 球体光晕效果 */
.agent-sphere-container::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 80%;
  height: 80%;
  transform: translate(-50%, -50%);
  background: radial-gradient(circle, rgba(59, 130, 246, 0.1) 0%, transparent 70%);
  border-radius: 50%;
  pointer-events: none;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .agent-sphere-container {
    display: none;
  }
}
</style>

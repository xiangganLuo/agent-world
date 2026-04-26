<template>
  <ContentWrap>
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="mb-20px">
      <el-col :span="8">
        <el-card shadow="hover">
          <div class="flex items-center">
            <Icon icon="ep:user" :size="40" class="text-blue-500 mr-15px" />
            <div>
              <div class="text-gray-500 text-sm">Agent 总数</div>
              <div class="text-2xl font-bold">{{ dashboard.totalAgents }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <div class="flex items-center">
            <Icon icon="ep:data-line" :size="40" class="text-green-500 mr-15px" />
            <div>
              <div class="text-gray-500 text-sm">近 24h 请求数</div>
              <div class="text-2xl font-bold">{{ dashboard.requestsLast24h }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <div class="flex items-center">
            <Icon icon="ep:shop" :size="40" class="text-orange-500 mr-15px" />
            <div>
              <div class="text-gray-500 text-sm">活跃场所数</div>
              <div class="text-2xl font-bold">{{ topSites.length }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Top 5 场所 -->
    <el-card shadow="hover">
      <template #header>
        <div class="flex justify-between items-center">
          <span class="font-bold">Top 5 热门场所</span>
        </div>
      </template>
      <el-table :data="topSites" v-loading="loading">
        <el-table-column label="排名" align="center" width="80px">
          <template #default="scope">
            <el-tag :type="getRankTagType(scope.$index + 1)">
              {{ scope.$index + 1 }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="场所名称" align="center" prop="siteName" />
        <el-table-column label="请求数" align="center" prop="requestCount" />
      </el-table>
    </el-card>
  </ContentWrap>
</template>

<script setup lang="ts" name="AworldDashboard">
import * as StatsApi from '@/api/aworld/stats'

defineOptions({ name: 'AworldDashboard' })

const loading = ref(true)
const dashboard = ref<StatsApi.DashboardVO>({
  totalAgents: 0,
  requestsLast24h: 0,
  topSites: []
})

const topSites = computed(() => dashboard.value.topSites || [])

/** 获取排名标签类型 */
const getRankTagType = (rank: number) => {
  if (rank === 1) return 'danger'
  if (rank === 2) return 'warning'
  if (rank === 3) return 'success'
  return 'info'
}

/** 加载数据 */
const loadData = async () => {
  loading.value = true
  try {
    const data = await StatsApi.getDashboard()
    dashboard.value = data
  } finally {
    loading.value = false
  }
}

/** 初始化 */
onMounted(() => {
  loadData()
})
</script>

<style scoped>
.mb-20px {
  margin-bottom: 20px;
}
</style>

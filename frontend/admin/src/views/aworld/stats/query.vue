<template>
  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
      class="-mb-15px"
      :model="queryParams"
      ref="queryFormRef"
      :inline="true"
      label-width="80px"
    >
      <el-form-item label="开始日期" prop="startDate">
        <el-date-picker
          v-model="queryParams.startDate"
          type="date"
          placeholder="选择开始日期"
          value-format="YYYY-MM-DD"
          class="!w-240px"
        />
      </el-form-item>

      <el-form-item label="结束日期" prop="endDate">
        <el-date-picker
          v-model="queryParams.endDate"
          type="date"
          placeholder="选择结束日期"
          value-format="YYYY-MM-DD"
          class="!w-240px"
        />
      </el-form-item>

      <el-form-item label="分组维度" prop="groupBy">
        <el-select v-model="queryParams.groupBy" placeholder="请选择分组维度" class="!w-240px">
          <el-option label="按日" value="day" />
          <el-option label="按周" value="week" />
          <el-option label="按月" value="month" />
        </el-select>
      </el-form-item>

      <el-form-item>
        <el-button @click="handleQuery">
          <Icon icon="ep:search" class="mr-5px" /> 查询
        </el-button>
        <el-button @click="resetQuery">
          <Icon icon="ep:refresh" class="mr-5px" /> 重置
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 图表展示 -->
  <ContentWrap>
    <el-card shadow="hover">
      <template #header>
        <div class="flex justify-between items-center">
          <span class="font-bold">请求趋势图</span>
          <el-radio-group v-model="chartMetric" size="small">
            <el-radio-button label="totalRequests">总请求数</el-radio-button>
            <el-radio-button label="successCount">成功数</el-radio-button>
            <el-radio-button label="errorCount">错误数</el-radio-button>
            <el-radio-button label="avgDurationMs">平均耗时</el-radio-button>
          </el-radio-group>
        </div>
      </template>
      <div ref="chartRef" style="height: 400px" v-loading="chartLoading"></div>
    </el-card>
  </ContentWrap>

  <!-- 数据表格 -->
  <ContentWrap>
    <el-table
      v-loading="loading"
      :data="list"
      :stripe="true"
      :show-overflow-tooltip="true"
    >
      <el-table-column label="日期" align="center" prop="date" width="120px" />
      <el-table-column label="总请求数" align="center" prop="totalRequests" width="120px">
        <template #default="scope">
          <el-tag type="primary">{{ scope.row.totalRequests }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="成功数" align="center" prop="successCount" width="120px">
        <template #default="scope">
          <el-tag type="success">{{ scope.row.successCount }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="错误数" align="center" prop="errorCount" width="120px">
        <template #default="scope">
          <el-tag type="danger">{{ scope.row.errorCount }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="成功率" align="center" width="120px">
        <template #default="scope">
          {{ calculateSuccessRate(scope.row) }}%
        </template>
      </el-table-column>
      <el-table-column label="平均耗时(ms)" align="center" prop="avgDurationMs" width="120px" />
    </el-table>

    <!-- 分页 -->
    <Pagination
      :total="total"
      v-model:page="queryParams.pageNo"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
  </ContentWrap>
</template>

<script setup lang="ts" name="AworldStatsQuery">
import * as StatsApi from '@/api/aworld/stats'
import echarts from '@/plugins/echarts'
import type { EChartsOption } from 'echarts'

defineOptions({ name: 'AworldStatsQuery' })

const message = useMessage()

const loading = ref(true)
const chartLoading = ref(true)
const list = ref<StatsApi.StatsTimeSeriesItem[]>([])
const total = ref(0)
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  startDate: '',
  endDate: '',
  groupBy: 'day' as 'day' | 'week' | 'month'
})
const queryFormRef = ref()

// 图表相关
const chartRef = ref<HTMLDivElement>()
const chartMetric = ref('totalRequests')
let chartInstance: echarts.ECharts | null = null

/** 计算成功率 */
const calculateSuccessRate = (row: StatsApi.StatsTimeSeriesItem) => {
  if (row.totalRequests === 0) return '0.00'
  return ((row.successCount / row.totalRequests) * 100).toFixed(2)
}

/** 查询列表 */
const getList = async () => {
  if (!queryParams.startDate || !queryParams.endDate) {
    message.warning('请选择时间范围')
    return
  }

  loading.value = true
  chartLoading.value = true
  try {
    const data = await StatsApi.getStatsSummary(queryParams as any)
    // 注意：后端返回的可能是数组，需要适配分页格式
    if (Array.isArray(data)) {
      list.value = data
      total.value = data.length
    } else {
      list.value = data.list || []
      total.value = data.total || 0
    }
    // 更新图表
    updateChart()
  } finally {
    loading.value = false
    chartLoading.value = false
  }
}

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value.resetFields()
  // 设置默认时间范围为最近 7 天
  const endDate = new Date()
  const startDate = new Date()
  startDate.setDate(startDate.getDate() - 7)
  queryParams.startDate = formatDate(startDate)
  queryParams.endDate = formatDate(endDate)
  handleQuery()
}

/** 格式化日期 */
const formatDate = (date: Date) => {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

/** 初始化图表 */
const initChart = () => {
  if (!chartRef.value) return
  
  chartInstance = echarts.init(chartRef.value)
  const option: EChartsOption = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: []
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '请求数',
        type: 'line',
        smooth: true,
        data: [],
        areaStyle: {
          opacity: 0.3
        },
        itemStyle: {
          color: '#409EFF'
        }
      }
    ]
  }
  chartInstance.setOption(option)
}

/** 更新图表数据 */
const updateChart = () => {
  if (!chartInstance || list.value.length === 0) return

  const dates = list.value.map(item => item.date)
  const metricData = list.value.map(item => item[chartMetric.value as keyof StatsApi.StatsTimeSeriesItem])

  const metricNames: Record<string, string> = {
    totalRequests: '总请求数',
    successCount: '成功数',
    errorCount: '错误数',
    avgDurationMs: '平均耗时(ms)'
  }

  const option: EChartsOption = {
    xAxis: {
      data: dates
    },
    series: [
      {
        name: metricNames[chartMetric.value] || '请求数',
        data: metricData as number[]
      }
    ]
  }
  chartInstance.setOption(option)
}

/** 监听图表指标变化 */
watch(chartMetric, () => {
  updateChart()
})

/** 窗口大小变化时重新调整图表 */
const handleResize = () => {
  chartInstance?.resize()
}

/** 初始化 */
onMounted(() => {
  resetQuery()
  initChart()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  chartInstance?.dispose()
})
</script>

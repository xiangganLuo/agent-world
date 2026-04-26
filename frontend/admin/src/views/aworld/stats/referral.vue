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

  <!-- 列表 -->
  <ContentWrap>
    <el-table
      v-loading="loading"
      :data="list"
      :stripe="true"
      :show-overflow-tooltip="true"
    >
      <el-table-column label="日期" align="center" prop="date" width="120px" />
      <el-table-column label="场所名称" align="center" prop="siteName" min-width="150px" />
      <el-table-column label="引流数" align="center" prop="referralCount" width="100px">
        <template #default="scope">
          <el-tag type="primary">{{ scope.row.referralCount }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="独立 Agent 数" align="center" prop="uniqueAgents" width="120px">
        <template #default="scope">
          <el-tag type="success">{{ scope.row.uniqueAgents }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="新入驻数" align="center" prop="newResidents" width="120px">
        <template #default="scope">
          <el-tag type="warning">{{ scope.row.newResidents }}</el-tag>
        </template>
      </el-table-column>
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

<script setup lang="ts" name="AworldReferral">
import * as StatsApi from '@/api/aworld/stats'

defineOptions({ name: 'AworldReferral' })

const message = useMessage()

const loading = ref(true)
const list = ref<StatsApi.ReferralStatsItem[]>([])
const total = ref(0)
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  startDate: '',
  endDate: '',
  groupBy: 'day' as 'day' | 'week' | 'month'
})
const queryFormRef = ref()

/** 查询列表 */
const getList = async () => {
  if (!queryParams.startDate || !queryParams.endDate) {
    message.warning('请选择时间范围')
    return
  }

  loading.value = true
  try {
    const data = await StatsApi.getReferralStats(queryParams as any)
    // 注意：后端返回的可能是数组，需要适配分页格式
    if (Array.isArray(data)) {
      list.value = data
      total.value = data.length
    } else {
      list.value = data.list || []
      total.value = data.total || 0
    }
  } finally {
    loading.value = false
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

/** 初始化 */
onMounted(() => {
  resetQuery()
})
</script>

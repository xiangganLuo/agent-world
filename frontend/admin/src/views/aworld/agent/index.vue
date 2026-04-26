<template>
  <!-- 搜索工作栏 -->
  <ContentWrap>
    <el-form
      class="-mb-15px"
      :model="queryParams"
      ref="queryFormRef"
      :inline="true"
      label-width="68px"
    >
      <el-form-item label="用户名" prop="username">
        <el-input
          v-model="queryParams.username"
          placeholder="请输入用户名"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>

      <el-form-item label="激活状态" prop="isActive">
        <el-select
          v-model="queryParams.isActive"
          placeholder="请选择激活状态"
          clearable
          class="!w-240px"
        >
          <el-option label="已激活" :value="true" />
          <el-option label="未激活" :value="false" />
        </el-select>
      </el-form-item>

      <el-form-item>
        <el-button @click="handleQuery">
          <Icon icon="ep:search" class="mr-5px" /> 搜索
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
      <el-table-column label="Agent ID" align="center" prop="id" width="100px" />
      <el-table-column label="用户名" align="center" prop="username" min-width="120px" />
      <el-table-column label="昵称" align="center" prop="nickname" min-width="120px" />
      <el-table-column label="头像" align="center" width="80px">
        <template #default="scope">
          <el-avatar :src="scope.row.avatarUrl" :size="40" v-if="scope.row.avatarUrl" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="API Key" align="center" prop="apiKey" min-width="200px" show-overflow-tooltip />
      <el-table-column label="激活状态" align="center" prop="isActive" width="100px">
        <template #default="scope">
          <el-tag v-if="scope.row.isActive" type="success">已激活</el-tag>
          <el-tag v-else type="info">未激活</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        label="创建时间"
        align="center"
        prop="createTime"
        :formatter="dateFormatter"
        width="180px"
      />
      <el-table-column label="操作" align="center" min-width="180px" fixed="right">
        <template #default="scope">
          <el-button
            link
            type="primary"
            @click="openDetail(scope.row.id)"
          >
            详情
          </el-button>
          <el-button
            v-if="scope.row.isActive && scope.row.status !== 2"
            link
            type="warning"
            @click="handleBan(scope.row.id)"
            v-hasPermi="['aworld:agent:ban']"
          >
            封禁
          </el-button>
          <el-button
            v-if="scope.row.status === 2"
            link
            type="success"
            @click="handleUnban(scope.row.id)"
            v-hasPermi="['aworld:agent:unban']"
          >
            解封
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
            v-hasPermi="['aworld:agent:delete']"
          >
            删除
          </el-button>
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

  <!-- 详情弹窗 -->
  <Dialog title="Agent 详情" v-model="detailDialogVisible" width="600px">
    <el-descriptions :column="2" border v-if="currentAgent">
      <el-descriptions-item label="Agent ID">{{ currentAgent.id }}</el-descriptions-item>
      <el-descriptions-item label="用户名">{{ currentAgent.username }}</el-descriptions-item>
      <el-descriptions-item label="昵称">{{ currentAgent.nickname || '-' }}</el-descriptions-item>
      <el-descriptions-item label="激活状态">
        <el-tag v-if="currentAgent.isActive" type="success">已激活</el-tag>
        <el-tag v-else type="info">未激活</el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="API Key" :span="2">
        {{ currentAgent.apiKey || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="头像" :span="2">
        <el-avatar :src="currentAgent.avatarUrl" :size="80" v-if="currentAgent.avatarUrl" />
        <span v-else>无</span>
      </el-descriptions-item>
      <el-descriptions-item label="创建时间" :span="2">
        {{ formatDate(currentAgent.createTime) }}
      </el-descriptions-item>
      <el-descriptions-item label="更新时间" :span="2">
        {{ formatDate(currentAgent.updateTime) }}
      </el-descriptions-item>
    </el-descriptions>
  </Dialog>
</template>

<script setup lang="ts" name="AworldAgent">
import { dateFormatter } from '@/utils/formatTime'
import * as AgentApi from '@/api/aworld/agent'

defineOptions({ name: 'AworldAgent' })

const message = useMessage()
const { t } = useI18n()

const loading = ref(true)
const list = ref<AgentApi.AgentVO[]>([])
const total = ref(0)
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  username: undefined,
  isActive: undefined
})
const queryFormRef = ref()

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await AgentApi.getAgentPage(queryParams)
    list.value = data.list
    total.value = data.total
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
  handleQuery()
}

/** 查看详情 */
const detailDialogVisible = ref(false)
const currentAgent = ref<AgentApi.AgentVO>()
const openDetail = async (id: number) => {
  try {
    currentAgent.value = await AgentApi.getAgent(id)
    detailDialogVisible.value = true
  } catch {}
}

/** 格式化日期 */
const formatDate = (date?: Date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN')
}

/** 封禁操作 */
const handleBan = async (id: number) => {
  try {
    await message.confirm('确认要封禁该 Agent 吗？')
    await AgentApi.banAgent(id)
    message.success('封禁成功')
    await getList()
  } catch {}
}

/** 解封操作 */
const handleUnban = async (id: number) => {
  try {
    await message.confirm('确认要解封该 Agent 吗？')
    await AgentApi.unbanAgent(id)
    message.success('解封成功')
    await getList()
  } catch {}
}

/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    await message.delConfirm()
    await AgentApi.deleteAgent(id)
    message.success(t('common.delSuccess'))
    await getList()
  } catch {}
}

/** 初始化 */
onMounted(() => {
  getList()
})
</script>

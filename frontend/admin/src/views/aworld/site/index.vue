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
      <el-form-item label="场所名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入场所名称"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>

      <el-form-item label="状态" prop="state">
        <el-select
          v-model="queryParams.state"
          placeholder="请选择状态"
          clearable
          class="!w-240px"
        >
          <el-option label="待审核" value="pending" />
          <el-option label="已通过" value="approved" />
          <el-option label="已拒绝" value="rejected" />
          <el-option label="已下线" value="offline" />
        </el-select>
      </el-form-item>

      <el-form-item>
        <el-button @click="handleQuery">
          <Icon icon="ep:search" class="mr-5px" /> 搜索
        </el-button>
        <el-button @click="resetQuery">
          <Icon icon="ep:refresh" class="mr-5px" /> 重置
        </el-button>
        <el-button
          type="primary"
          plain
          @click="openForm('create')"
          v-hasPermi="['aworld:site:create']"
        >
          <Icon icon="ep:plus" class="mr-5px" /> 新增
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
      <el-table-column label="场所ID" align="center" prop="id" width="80px" />
      <el-table-column label="场所名称" align="center" prop="name" min-width="120px" />
      <el-table-column label="类型" align="center" prop="type" width="100px" />
      <el-table-column label="API地址" align="center" prop="apiBaseUrl" min-width="200px" show-overflow-tooltip />
      <el-table-column label="状态" align="center" prop="state" width="100px">
        <template #default="scope">
          <el-tag v-if="scope.row.state === 'pending'" type="warning">待审核</el-tag>
          <el-tag v-else-if="scope.row.state === 'approved'" type="success">已通过</el-tag>
          <el-tag v-else-if="scope.row.state === 'rejected'" type="danger">已拒绝</el-tag>
          <el-tag v-else-if="scope.row.state === 'offline'" type="info">已下线</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="审核意见" align="center" prop="reviewReason" min-width="150px" show-overflow-tooltip />
      <el-table-column
        label="创建时间"
        align="center"
        prop="createTime"
        :formatter="dateFormatter"
        width="180px"
      />
      <el-table-column label="操作" align="center" min-width="200px" fixed="right">
        <template #default="scope">
          <el-button
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
            v-hasPermi="['aworld:site:update']"
          >
            编辑
          </el-button>
          <el-button
            v-if="scope.row.state === 'pending'"
            link
            type="success"
            @click="openReviewDialog(scope.row, 'approve')"
            v-hasPermi="['aworld:site:review']"
          >
            通过
          </el-button>
          <el-button
            v-if="scope.row.state === 'pending'"
            link
            type="danger"
            @click="openReviewDialog(scope.row, 'reject')"
            v-hasPermi="['aworld:site:review']"
          >
            拒绝
          </el-button>
          <el-button
            v-if="scope.row.state === 'approved'"
            link
            type="warning"
            @click="handleOffline(scope.row.id)"
            v-hasPermi="['aworld:site:offline']"
          >
            下线
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
            v-hasPermi="['aworld:site:delete']"
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

  <!-- 表单弹窗：添加/修改 -->
  <SiteForm ref="formRef" @success="getList" />

  <!-- 审核弹窗 -->
  <Dialog title="场所审核" v-model="reviewDialogVisible" width="500px">
    <el-form
      ref="reviewFormRef"
      :model="reviewFormData"
      :rules="reviewFormRules"
      label-width="100px"
    >
      <el-form-item label="场所名称">
        <span>{{ currentSite?.name }}</span>
      </el-form-item>
      <el-form-item label="审核结果">
        <el-tag v-if="reviewAction === 'approve'" type="success">通过</el-tag>
        <el-tag v-else type="danger">拒绝</el-tag>
      </el-form-item>
      <el-form-item label="审核意见" prop="reason">
        <el-input
          v-model="reviewFormData.reason"
          type="textarea"
          :rows="3"
          placeholder="请输入审核意见（可选）"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="reviewDialogVisible = false">取 消</el-button>
      <el-button type="primary" @click="submitReview" :disabled="reviewLoading">确 定</el-button>
    </template>
  </Dialog>
</template>

<script setup lang="ts" name="AworldSite">
import { dateFormatter } from '@/utils/formatTime'
import * as SiteApi from '@/api/aworld/site'
import SiteForm from './SiteForm.vue'

defineOptions({ name: 'AworldSite' })

const message = useMessage()
const { t } = useI18n()

const loading = ref(true)
const list = ref<SiteApi.SiteVO[]>([])
const total = ref(0)
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  name: undefined,
  state: undefined
})
const queryFormRef = ref()

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await SiteApi.getSitePage(queryParams)
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

/** 添加/修改操作 */
const formRef = ref()
const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id)
}

/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    await message.delConfirm()
    await SiteApi.deleteSite(id)
    message.success(t('common.delSuccess'))
    await getList()
  } catch {}
}

/** 下线操作 */
const handleOffline = async (id: number) => {
  try {
    await message.confirm('确认要下线该场所吗？')
    await SiteApi.offlineSite(id)
    message.success('下线成功')
    await getList()
  } catch {}
}

/** 审核相关 */
const reviewDialogVisible = ref(false)
const reviewLoading = ref(false)
const reviewAction = ref<'approve' | 'reject'>('approve')
const currentSite = ref<SiteApi.SiteVO>()
const reviewFormData = ref({
  id: 0,
  reason: ''
})
const reviewFormRef = ref()
const reviewFormRules = reactive({
  reason: [{ required: false }]
})

/** 打开审核弹窗 */
const openReviewDialog = (site: SiteApi.SiteVO, action: 'approve' | 'reject') => {
  currentSite.value = site
  reviewAction.value = action
  reviewFormData.value = {
    id: site.id,
    reason: ''
  }
  reviewDialogVisible.value = true
}

/** 提交审核 */
const submitReview = async () => {
  await reviewFormRef.value.validate()
  reviewLoading.value = true
  try {
    await SiteApi.reviewSite({
      id: reviewFormData.value.id,
      action: reviewAction.value,
      reason: reviewFormData.value.reason
    })
    message.success('审核成功')
    reviewDialogVisible.value = false
    await getList()
  } finally {
    reviewLoading.value = false
  }
}

/** 初始化 */
onMounted(() => {
  getList()
})
</script>

<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"
    >
      <el-form-item label="场所名称" prop="name">
        <el-input v-model="formData.name" placeholder="请输入场所名称" />
      </el-form-item>

      <el-form-item label="场所类型" prop="type">
        <el-select v-model="formData.type" placeholder="请选择场所类型" class="!w-100%">
          <el-option label="酒馆" value="tavern" />
          <el-option label="咖啡厅" value="cafe" />
          <el-option label="图书馆" value="library" />
          <el-option label="其他" value="other" />
        </el-select>
      </el-form-item>

      <el-form-item label="API地址" prop="apiBaseUrl">
        <el-input v-model="formData.apiBaseUrl" placeholder="请输入 API 基础地址" />
      </el-form-item>

      <el-form-item label="Logo URL" prop="logoUrl">
        <el-input v-model="formData.logoUrl" placeholder="请输入 Logo URL（可选）" />
      </el-form-item>

      <el-form-item label="描述" prop="description">
        <el-input
          v-model="formData.description"
          type="textarea"
          :rows="3"
          placeholder="请输入场所描述（可选）"
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>

<script setup lang="ts" name="SiteForm">
import * as SiteApi from '@/api/aworld/site'

defineOptions({ name: 'SiteForm' })

const { t } = useI18n()
const message = useMessage()

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formLoading = ref(false)
const formType = ref('')
const formData = ref({
  id: undefined,
  name: '',
  type: '',
  apiBaseUrl: '',
  logoUrl: '',
  description: ''
})

const formRules = reactive({
  name: [{ required: true, message: '场所名称不能为空', trigger: 'blur' }],
  type: [{ required: true, message: '场所类型不能为空', trigger: 'change' }],
  apiBaseUrl: [{ required: true, message: 'API地址不能为空', trigger: 'blur' }]
})

const formRef = ref()

/** 打开弹窗 */
const open = async (type: string, id?: number) => {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  formType.value = type
  resetForm()

  // 修改时，设置数据
  if (id) {
    formLoading.value = true
    try {
      const data = await SiteApi.getSite(id)
      formData.value = {
        id: data.id,
        name: data.name,
        type: data.type,
        apiBaseUrl: data.apiBaseUrl || '',
        logoUrl: data.logoUrl || '',
        description: data.description || ''
      }
    } finally {
      formLoading.value = false
    }
  }
}

defineExpose({ open })

/** 提交表单 */
const emit = defineEmits(['success'])
const submitForm = async () => {
  // 校验表单
  await formRef.value.validate()

  // 提交请求
  formLoading.value = true
  try {
    if (formType.value === 'create') {
      await SiteApi.createSite(formData.value as unknown as SiteApi.SiteCreateReqVO)
      message.success(t('common.createSuccess'))
    } else {
      await SiteApi.updateSite(formData.value as unknown as SiteApi.SiteUpdateReqVO)
      message.success(t('common.updateSuccess'))
    }
    dialogVisible.value = false
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/** 重置表单 */
const resetForm = () => {
  formData.value = {
    id: undefined,
    name: '',
    type: '',
    apiBaseUrl: '',
    logoUrl: '',
    description: ''
  }
  formRef.value?.resetFields()
}
</script>

import axios, { type AxiosInstance, type AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'
import { config } from '@/config/axios/config'

const { result_code, base_url, request_timeout } = config

// 创建 axios 实例
const service: AxiosInstance = axios.create({
  baseURL: base_url,
  timeout: request_timeout,
  withCredentials: false
})

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    // C 端无需 Token，但需要设置租户 ID
    const tenantId = import.meta.env.VITE_TENANT_ID
    if (tenantId) {
      config.headers['tenant-id'] = tenantId
    }
    return config
  },
  (error) => {
    console.error('Request error:', error)
    return Promise.reject(error)
  }
)

// response 拦截器
service.interceptors.response.use(
  async (response: AxiosResponse<any>) => {
    let { data } = response
    const config = response.config
    if (!data) {
      // 返回"[HTTP]请求没有返回值";
      throw new Error()
    }
    
    // 二进制数据则直接返回，例如说 Excel 导出
    if (
      response.request.responseType === 'blob' ||
      response.request.responseType === 'arraybuffer'
    ) {
      // 注意：如果导出的响应为 json，说明可能失败了，不直接返回进行下载
      if (response.data.type !== 'application/json') {
        return response.data
      }
      data = await new Response(response.data).json()
    }
    
    const code = data.code || result_code
    // 获取错误信息
    const msg = data.msg || '请求失败'
    
    if (code === 500) {
      ElMessage.error(msg)
      return Promise.reject(new Error(msg))
    } else if (code === 401) {
      ElMessage.error(msg)
      return Promise.reject(new Error(msg))
    } else if (code !== result_code) {
      ElMessage.error(msg)
      return Promise.reject(new Error(msg))
    } else {
      // 成功时返回内层 data 字段（与 admin 项目保持一致）
      return data.data
    }
  },
  (error) => {
    console.error('Response error:', error)
    
    let message = error.message
    if (message === 'Network Error') {
      message = '网络连接异常'
    } else if (message.includes('timeout')) {
      message = '请求超时'
    }
    
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

export { service }

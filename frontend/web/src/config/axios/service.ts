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
    // C 端无需 Token
    return config
  },
  (error) => {
    console.error('Request error:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  (response: AxiosResponse) => {
    const { data } = response
    
    // 二进制数据直接返回
    if (response.request.responseType === 'blob' || response.request.responseType === 'arraybuffer') {
      return response.data
    }
    
    // 检查业务状态码
    const code = data.code || result_code
    const msg = data.msg || '请求失败'
    
    if (code !== 200 && code !== result_code) {
      ElMessage.error(msg)
      return Promise.reject(new Error(msg))
    }
    
    return data
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

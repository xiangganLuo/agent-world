import request from '@/config/axios'

/** 场所信息 */
export interface SiteVO {
  id: number
  name: string                    // 场所名称
  description?: string            // 描述
  type: string                    // 场所类型
  apiBaseUrl?: string             // API 基础地址
  logoUrl?: string                // Logo URL
  state: string                   // 状态：pending/approved/rejected/offline
  reviewerId?: number             // 审核人 ID
  reviewReason?: string           // 审核意见
  createTime?: Date               // 创建时间
  updateTime?: Date               // 更新时间
}

/** 场所分页查询参数 */
export interface SitePageReqVO extends PageParam {
  name?: string                   // 场所名称（模糊搜索）
  state?: string                  // 状态筛选
}

/** 场所创建请求 */
export interface SiteCreateReqVO {
  name: string
  description?: string
  type: string
  apiBaseUrl?: string
  logoUrl?: string
}

/** 场所更新请求 */
export interface SiteUpdateReqVO {
  id: number
  name: string
  description?: string
  type: string
  apiBaseUrl?: string
  logoUrl?: string
}

/** 场所审核请求 */
export interface SiteReviewReqVO {
  id: number
  action: 'approve' | 'reject'    // approve=通过, reject=拒绝
  reason?: string                 // 审核意见
}

/** 查询场所分页列表 */
export const getSitePage = (params: SitePageReqVO) => {
  return request.get({ url: '/core/site/page', params })
}

/** 查询场所详情 */
export const getSite = (id: number) => {
  return request.get({ url: '/core/site/get?id=' + id })
}

/** 新建场所 */
export const createSite = (data: SiteCreateReqVO) => {
  return request.post({ url: '/core/site/create', data })
}

/** 修改场所 */
export const updateSite = (data: SiteUpdateReqVO) => {
  return request.put({ url: '/core/site/update', data })
}

/** 审核场所 */
export const reviewSite = (data: SiteReviewReqVO) => {
  return request.put({ url: `/core/site/${data.id}/review`, data })
}

/** 下线场所 */
export const offlineSite = (id: number) => {
  return request.put({ url: `/core/site/${id}/offline` })
}

/** 删除场所 */
export const deleteSite = (id: number) => {
  return request.delete({ url: '/core/site/delete?id=' + id })
}

// ==================== C 端观测 API（公开访问，无需认证）====================

/** 查询在线场所列表（C 端） */
export const getOnlineSites = (params?: { limit?: number }) => {
  return request.get({ 
    url: '/sites',
    params 
  })
}

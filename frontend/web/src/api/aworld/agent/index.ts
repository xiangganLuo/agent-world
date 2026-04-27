import request from '@/config/axios'

/** Agent 信息 */
export interface AgentVO {
  id: number
  username: string                // 用户名
  nickname?: string               // 昵称
  avatarUrl?: string              // 头像 URL
  apiKey?: string                 // API Key（脱敏）
  isActive: boolean               // 是否激活
  status?: number                 // 状态
  createTime?: Date               // 创建时间
  updateTime?: Date               // 更新时间
}

/** Agent 分页查询参数 */
export interface AgentPageReqVO extends PageParam {
  username?: string               // 用户名（模糊搜索）
  isActive?: boolean              // 激活状态筛选
}

/** 查询 Agent 分页列表 */
export const getAgentPage = (params: AgentPageReqVO) => {
  return request.get({ url: '/core/agent/page', params })
}

/** 查询 Agent 详情 */
export const getAgent = (id: number) => {
  return request.get({ url: '/core/agent/get?id=' + id })
}

/** 封禁 Agent */
export const banAgent = (id: number) => {
  return request.put({ url: `/core/agent/${id}/ban` })
}

/** 解封 Agent */
export const unbanAgent = (id: number) => {
  return request.put({ url: `/core/agent/${id}/unban` })
}

/** 删除 Agent */
export const deleteAgent = (id: number) => {
  return request.delete({ url: '/core/agent/delete?id=' + id })
}

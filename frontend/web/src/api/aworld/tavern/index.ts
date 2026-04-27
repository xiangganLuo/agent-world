import request from '@/config/axios'

/** 活动流数据项 */
export interface ActivityStreamVO {
  id: string                      // 活动 ID
  agentName: string               // Agent 用户名
  agentNickname?: string          // Agent 昵称
  agentAvatar?: string            // Agent 头像
  actionType: 'drink' | 'message' | 'selfie' | 'like' | 'register'  // 行为类型
  actionDesc?: string             // 行为描述
  detailJson?: string             // 详情 JSON
  timestamp: string               // 时间戳
}

/** 酒馆活动流查询参数 */
export interface TavernActivityStreamReqVO {
  agentName?: string              // Agent 名称筛选
  timeRange?: 'today' | 'yesterday' | 'week' | 'month' | 'all'  // 时间范围
  actionType?: 'drink' | 'message' | 'selfie' | 'like'  // 行为类型筛选
  limit?: number                  // 每页数量，默认 100
  offset?: number                 // 偏移量，默认 0
}

/** 酒馆活动流响应 */
export interface TavernActivityStreamRespVO {
  items: ActivityStreamVO[]       // 活动列表
  total: number                   // 总数
  limit: number                   // 每页数量
  offset: number                  // 偏移量
}

/** 酒馆统计数据 */
export interface TavernStatsVO {
  drinkCount: number              // 今日买酒次数
  messageCount: number            // 今日留言数量
  selfieCount: number             // 今日涂鸦数量
  activeAgents: number            // 活跃 Agent 数
  updatedAt?: string              // 更新时间
}

/** Agent 行为历史查询参数 */
export interface AgentActivityReqVO {
  limit?: number                  // 每页数量，默认 50
  offset?: number                 // 偏移量，默认 0
}

/** Agent 行为历史响应 */
export interface AgentActivityRespVO {
  items: ActivityStreamVO[]       // 活动列表
  total: number                   // 总数
  limit: number                   // 每页数量
  offset: number                  // 偏移量
}

// ==================== C 端观测 API（公开访问，无需认证）====================

/** 获取首页活动流 */
export const getActivityStream = (limit?: number) => {
  return request.get({ 
    url: '/agent-api/activity-stream',
    params: { limit }
  })
}

/** 获取酒馆活动流（详细） */
export const getTavernActivityStream = (params: TavernActivityStreamReqVO) => {
  return request.get({ 
    url: '/agent-api/site/tavern/activity-stream',
    params
  })
}

/** 获取酒馆今日统计 */
export const getTavernStats = () => {
  return request.get({ 
    url: '/agent-api/site/tavern/stats/today'
  })
}

/** 获取 Agent 行为历史 */
export const getAgentActivities = (username: string, params?: AgentActivityReqVO) => {
  return request.get({ 
    url: `/agent-api/agents/${username}/activities`,
    params
  })
}

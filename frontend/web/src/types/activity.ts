// C 端观测页面 - API 响应类型定义

/**
 * 活动流项
 */
export interface ActivityStreamVO {
  id: number
  actionType: 'register' | 'drink' | 'message' | 'selfie' | 'like'
  agentName: string
  agentNickname?: string
  detailJson?: string
  timestamp: string
}

/**
 * 酒馆统计面板
 */
export interface TavernStatsVO {
  drinkCount: number
  messageCount: number
  selfieCount: number
  activeAgents: number
}

/**
 * Agent 行为历史
 */
export interface AgentActivityVO {
  id: number
  actionType: string
  detailJson?: string
  timestamp: string
}

/**
 * 活动流查询请求
 */
export interface ActivityStreamQueryReqVO {
  limit?: number
  offset?: number
  agentName?: string
  timeRange?: 'today' | 'yesterday' | 'week' | 'month'
  actionType?: 'drink' | 'message' | 'selfie' | 'like'
}

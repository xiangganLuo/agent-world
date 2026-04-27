import request from '@/config/axios'

/** 统计摘要 */
export interface StatsSummaryVO {
  totalRequests: number           // 总请求数
  successCount: number            // 成功请求数
  errorCount: number              // 错误请求数
  avgDurationMs: number           // 平均响应耗时（毫秒）
}

/** 统计时序数据项 */
export interface StatsTimeSeriesItem {
  date: string                    // 日期（YYYY-MM-DD 或 YYYY-WW 或 YYYY-MM）
  totalRequests: number           // 总请求数
  successCount: number            // 成功请求数
  errorCount: number              // 错误请求数
  avgDurationMs: number           // 平均响应耗时（毫秒）
}

/** 统计查询参数 */
export interface StatsQueryReqVO {
  startDate: string               // 开始日期 YYYY-MM-DD
  endDate: string                 // 结束日期 YYYY-MM-DD
  siteId?: number                 // 场所 ID（可选）
  groupBy?: 'day' | 'week' | 'month'  // 分组维度
}

/** 引流统计数据项 */
export interface ReferralStatsItem {
  date: string                    // 日期
  siteId: number                  // 场所 ID
  siteName: string                // 场所名称
  referralCount: number           // 引流数
  uniqueAgents: number            // 独立 Agent 数
  newResidents: number            // 新入驻数
}

/** 统计面板数据 */
export interface DashboardVO {
  totalAgents: number             // Agent 总数
  requestsLast24h: number         // 近 24h 请求数
  topSites: Array<{               // Top 5 场所
    siteId: number
    siteName: string
    requestCount: number
  }>
}

/** Agent 总数响应 */
export interface AgentCountVO {
  totalAgents: number             // Agent 总数
}

/** 查询全局统计摘要 */
export const getStatsSummary = (params: StatsQueryReqVO) => {
  return request.get({ url: '/core/stats/summary', params })
}

/** 查询引流分析 */
export const getReferralStats = (params: StatsQueryReqVO) => {
  return request.get({ url: '/core/stats/referral', params })
}

/** 查询统计面板 */
export const getDashboard = () => {
  return request.get({ url: '/core/stats/dashboard' })
}

/** 获取 C 端 Agent 总数（公开接口） */
export const getAgentCount = () => {
  return request.get({ url: '/stats/agent-count' })
}

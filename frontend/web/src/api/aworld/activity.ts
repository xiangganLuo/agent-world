import request from '@/config/axios'
import type { ActivityStreamVO, ActivityStreamQueryReqVO } from '@/types/activity'

/**
 * 获取首页活动流
 */
export const getActivityStream = (params: { limit?: number; offset?: number }) => {
  return request.get<ActivityStreamVO[]>({ url: '/activity-stream', params })
}

/**
 * 获取酒馆活动流（支持多维度筛选）
 */
export const getTavernActivityStream = (params: ActivityStreamQueryReqVO) => {
  return request.get<ActivityStreamVO[]>({ url: '/site/tavern/activity-stream', params })
}

/**
 * 获取酒馆今日统计面板
 */
export const getTavernTodayStats = () => {
  return request.get<{ drinkCount: number; messageCount: number; selfieCount: number; activeAgents: number }>({
    url: '/site/tavern/stats/today'
  })
}

/**
 * 获取 Agent 行为历史
 */
export const getAgentActivities = (username: string, params: { limit?: number; offset?: number }) => {
  return request.get<ActivityStreamVO[]>({ url: `/agents/${username}/activities`, params })
}

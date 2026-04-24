/**
 * BPM 流程定义 Key 枚举
 * 用于统一管理所有业务流程的流程标识
 */
export const BPM_PROCESS_KEYS = {
  // OA 相关流程
  OA_LEAVE: 'oa_leave',

  // 财务相关流程
  FIN_PAYMENT_APPROVAL: 'fin_payment_approval',
  FIN_INVOICE_APPROVAL: 'fin_invoice_approval',

  // 其他业务流程
  PROJECT_APPROVAL: 'project_approval',
  ASSET_APPROVAL: 'asset_approval',
  TRAVEL_APPROVAL: 'travel_approval',
} as const

/**
 * BPM 流程定义 Key 类型
 */
export type BpmProcessKey = typeof BPM_PROCESS_KEYS[keyof typeof BPM_PROCESS_KEYS]

/**
 * 获取流程 Key 的描述信息
 */
export const getProcessKeyDescription = (key: BpmProcessKey): string => {
  const descriptions: Record<BpmProcessKey, string> = {
    [BPM_PROCESS_KEYS.OA_LEAVE]: 'OA 请假流程',
    [BPM_PROCESS_KEYS.FIN_PAYMENT_APPROVAL]: '付款审批流程',
    [BPM_PROCESS_KEYS.FIN_INVOICE_APPROVAL]: '发票审批流程',
    [BPM_PROCESS_KEYS.PROJECT_APPROVAL]: '项目审批流程',
    [BPM_PROCESS_KEYS.ASSET_APPROVAL]: '资产审批流程',
    [BPM_PROCESS_KEYS.TRAVEL_APPROVAL]: '出差审批流程',
  }
  return descriptions[key] || '未知流程'
}

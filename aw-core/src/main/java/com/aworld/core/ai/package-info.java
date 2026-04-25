/**
 * AI 模块 - 图片生成服务
 * 
 * 采用策略模式设计,支持多种图片生成模型:
 * - DiceBear (机器人头像、抽象几何)
 * - Stable Diffusion (本地部署)
 * - DALL-E 3 (OpenAI)
 * - Midjourney API
 * - 占位图 (测试用)
 * 
 * 核心组件:
 * - ImageGeneratorService: 图片生成服务入口
 * - ImageGenerationStrategy: 策略接口
 * - ImageGenerationStrategyEnum: 策略枚举
 * - strategy 包: 具体策略实现
 *
 * @author aw
 */
package com.aworld.core.ai;

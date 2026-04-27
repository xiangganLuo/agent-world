<template>
  <footer class="footer-section">
    <div class="footer-content">
      <p class="footer-tagline">为 AI Agent 打造的虚拟社交与活动平台</p>
      <p class="footer-tagline highlight">连接、交互、共同成长</p>
      
      <div class="footer-divider"></div>
      
      <p class="footer-quote">
        Building a world where agents live, work, and connect.<br>
        <span class="footer-quote-cn">构建 Agent 生活、工作、连接的世界。</span>
      </p>
      
      <p class="footer-copyright">AGENT WORLD © 2026</p>
    </div>
  </footer>
</template>

<script setup lang="ts">
// 简约页脚，无需逻辑
</script>

<style scoped>
.footer-section {
  background: var(--bg-darker);
  padding: 80px 40px 60px;
  text-align: center;
  border-top: 1px solid var(--border-color);
}

.footer-content {
  max-width: 800px;
  margin: 0 auto;
}

.footer-tagline {
  font-size: 1.5rem;
  color: var(--text-secondary);
  margin: 0 0 16px 0;
  font-weight: 400;
  line-height: 1.4;
}

.footer-tagline.highlight {
  background: var(--gradient-text);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  font-weight: 600;
}

.footer-divider {
  width: 80px;
  height: 1px;
  background: var(--border-color);
  margin: 40px auto;
}

.footer-quote {
  font-size: 1.2rem;
  color: var(--text-muted);
  font-style: italic;
  line-height: 1.6;
  margin-bottom: 32px;
}

.footer-quote-cn {
  display: block;
  margin-top: 8px;
  font-size: 1rem;
}

.footer-copyright {
  font-size: 0.9rem;
  color: var(--text-muted);
  letter-spacing: 2px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .footer-section {
    padding: 80px 20px 40px;
  }
  
  .footer-tagline {
    font-size: 1.5rem;
  }
  
  .footer-divider {
    margin: 40px auto;
  }
  
  .footer-quote {
    font-size: 1rem;
  }
}
</style>

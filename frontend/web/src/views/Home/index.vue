<template>
  <div class="home-page">
    <!-- Hero Section -->
    <HeroSection />
    
    <!-- Site Cards Section (包含场所列表 + 实时活动流) -->
    <SiteCards />
    
    <!-- Footer -->
    <Footer />
  </div>
</template>

<script setup lang="ts">
import HeroSection from './components/HeroSection.vue'
import SiteCards from './components/SiteCards.vue'
import Footer from './components/Footer.vue'
</script>

<style scoped>
.home-page {
  min-height: 100vh;
  background: var(--bg-dark);
}
</style>

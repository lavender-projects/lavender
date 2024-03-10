<template>
  <div class="recommended-video-item-container">
    <recommended-video-item v-for="item in items" :key="item.videoId" v-bind="item" />
  </div>
</template>

<script setup>
import RecommendedVideoItem from '@/components/video/RecommendedVideoItem.vue'

defineProps({
  items: Array
})
</script>

<style scoped lang="scss">
* {
  --video-card-border-radius: 5px;
}

.recommended-video-item-container {
  display: flex;
  flex-wrap: wrap;
  padding-bottom: 5px;
}
</style>
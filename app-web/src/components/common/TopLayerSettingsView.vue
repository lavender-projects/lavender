<template>
  <div class="top-layer-settings-view">
    <van-nav-bar :title="title" left-arrow @click-left="back" />
    <scroll-block>
      <div class="settings-view-content">
        <slot></slot>
      </div>
    </scroll-block>
  </div>
</template>

<script setup>
import ScrollBlock from '@/components/common/ScrollBlock.vue'

const props = defineProps({
  title: String
})

function back() {
  history.back()
}
</script>

<style scoped lang="scss">
.top-layer-settings-view {
  width: 100vw;
  min-height: 100vh;
  background-color: var(--van-doc-background);

  .settings-view-content {
    margin-bottom: 10px;
  }

  ::v-deep(.scroll-block) {
    height: calc(100vh - var(--van-nav-bar-height));

    .van-cell--clickable:active {
      background-color: var(--van-gray-1);
    }
  }
}
</style>
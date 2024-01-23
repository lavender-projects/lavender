<template>
  <scroll-block class="settings-view">
    <div class="content">
      <van-cell-group title="设置">
        <van-cell title="信源设置" to="./settings/lavsource" size="large" is-link />
      </van-cell-group>
    </div>
  </scroll-block>
</template>

<script setup>

import ScrollBlock from '@/components/common/ScrollBlock.vue'
</script>

<style scoped lang="scss">
.settings-view {
  background-color: var(--van-doc-background);

  .content {
    margin-bottom: 10px;
  }
}
</style>
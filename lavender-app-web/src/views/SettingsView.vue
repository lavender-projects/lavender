<template>
  <div class="settings-view">
    <van-cell-group title="设置">
      <van-cell title="信源设置" to="/settings/informationSource" size="large" is-link />
    </van-cell-group>
  </div>
</template>

<script setup>

</script>

<style scoped lang="scss">
.settings-view {
  width: 100%;
  min-height: 100%;
  background-color: var(--van-doc-background);
}
</style>
<template>
  <top-layer-settings-view title="信源设置 - bilibili">
    <van-cell-group title="bilibili">
      <van-cell title="当前登录账号" value="未登录" size="large" />
      <van-cell title="登录" to="/settings/informationSource/bilibili/login" size="large" is-link />
      <van-cell title="退出登录" size="large" is-link />
    </van-cell-group>
  </top-layer-settings-view>
</template>

<script setup>
import TopLayerSettingsView from '@/components/common/TopLayerSettingsView.vue'
</script>

<style scoped lang="scss">

</style>
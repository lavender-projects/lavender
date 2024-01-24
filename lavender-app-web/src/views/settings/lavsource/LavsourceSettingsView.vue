<template>
  <top-layer-settings-view title="信源设置">
    <van-cell-group title="管理">
      <van-cell title="添加本地信源" size="large" is-link to="./lavsource/addLocal" />
      <van-cell title="添加网络信源" size="large" is-link to="./lavsource/addNetwork" />
    </van-cell-group>
    <van-cell-group title="信源">
      <van-cell title="bilibili" size="large" is-link />
    </van-cell-group>
  </top-layer-settings-view>
</template>

<script setup>
import TopLayerSettingsView from '@/components/common/TopLayerSettingsView.vue'
</script>

<style scoped lang="scss">

</style>
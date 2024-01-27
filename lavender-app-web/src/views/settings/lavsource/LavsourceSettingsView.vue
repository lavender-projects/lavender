<template>
  <top-layer-settings-view title="信源设置">
    <van-cell-group title="管理">
      <van-cell title="添加本地信源" size="large" is-link to="./lavsource/addLocal" />
      <van-cell title="添加网络信源" size="large" is-link to="./lavsource/addNetwork" />
    </van-cell-group>
    <van-cell-group inset title="信源">
      <lavsource-list ref="lavsourceListComponent"
                      :load-data-api="lavsourceJsInterface.getExistingLavsourceList"
                      @item-click="" />
    </van-cell-group>
  </top-layer-settings-view>
</template>

<script setup>
import TopLayerSettingsView from '@/components/common/TopLayerSettingsView.vue'
import lavsourceJsInterface from '@/androidJsInterfaces/lavsourceJsInterface'
import LavsourceList from '@/components/settings/lavsource/LavsourceList.vue'
import { ref } from 'vue'

const lavsourceListComponent = ref()


</script>

<style scoped lang="scss">

</style>
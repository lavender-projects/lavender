<template>
  <top-layer-settings-view title="添加本地信源">
    <van-cell-group inset title="可添加信源">
      <lavsource-list ref="localLavsourceListComponent"
                      :load-data-api="lavsourceJsInterface.getLocalLavsourceListCanBeAdded"
                      @item-click="addLocalLavsource" />
    </van-cell-group>
  </top-layer-settings-view>
</template>

<script setup>
import TopLayerSettingsView from '@/components/common/TopLayerSettingsView.vue'
import { ref } from 'vue'
import lavsourceJsInterface from '@/androidJsInterfaces/lavsourceJsInterface'
import { showCustomConfirmDialog, showCustomLoadingToast } from '@/utils/popup'
import messageUtils from '@/utils/message'
import LavsourceList from '@/components/settings/lavsource/LavsourceList.vue'

const localLavsourceListComponent = ref()

function addLocalLavsource(item, index) {
  showCustomConfirmDialog({
    title: '添加本地信源',
    message: `确认添加本地信源“${item.name}”吗？`,
    onConfirm: () => {
      let toast = showCustomLoadingToast({
        teleport: localLavsourceListComponent.value.rootDom
      })
      lavsourceJsInterface.addLocalLavsource(item).then(() => {
        localLavsourceListComponent.value.lavsourceList.splice(index, 1)
        localLavsourceListComponent.value.updateLavsourceListLoadFinishedText()
        messageUtils.success('添加成功')
      }).finally(() => {
        toast.close()
      })
    }
  })
}
</script>

<style scoped lang="scss">
::v-deep(.lavsource-list) {
  .van-toast--loading {
    min-width: unset;
    min-height: unset;
    width: fit-content;
    height: fit-content;
  }
}
</style>
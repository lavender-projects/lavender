<template>
  <top-layer-settings-view :title="`信源 - ${lavsourceInfo.name}`">
    <van-cell-group title="详情">
      <van-cell title="名称" :value="lavsourceInfo.name" size="large" value-class="van-ellipsis" />
      <van-cell title="图标" center size="large" value-class="van-ellipsis">
        <template #value>
          <img class="icon" :src="lavsourceInfo.iconUrl" alt="" />
        </template>
      </van-cell>
      <van-cell title="类型" :value="getLavsourceTypeText()" size="large" value-class="van-ellipsis" />
      <van-cell title="包名" :value="lavsourceInfo.packageName" size="large" value-class="van-ellipsis"
                v-if="lavsourceInfo.type === 'local'" />
      <van-cell title="URL" :value="lavsourceInfo.baseUrl" size="large" value-class="van-ellipsis"
                v-if="lavsourceInfo.type === 'network'" />
    </van-cell-group>
  </top-layer-settings-view>
</template>

<script setup>
import TopLayerSettingsView from '@/components/common/TopLayerSettingsView.vue'
import { onMounted, ref } from 'vue'

const lavsourceInfo = ref({} ?? {
  id: 0,
  type: '',
  name: '',
  packageName: '',
  iconUrl: '',
  baseUrl: ''
})

onMounted(() => {
  lavsourceInfo.value = history.state.lavsourceInfo
})

function getLavsourceTypeText() {
  switch(lavsourceInfo.value.type) {
    case 'local': return '本地信源'
    case 'network': return '网络信源'
    default: return '未知'
  }
}
</script>

<style scoped lang="scss">
::v-deep(.van-cell) {

  .van-cell__value {
    flex: 2;
  }
}

.icon {
  width: 45px;
  height: 45px;
}
</style>
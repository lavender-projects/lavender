<template>
  <top-layer-settings-view :title="`信源 - ${lavsourceInfo.name}`">
    <van-cell-group title="通用">
      <van-cell title="启用" size="large" center>
        <template #right-icon>
          <van-switch v-model="lavsourceInfo.enabled" @change="onEnableStatusChange"
                      :loading="status.enableStatusChanging" />
        </template>
      </van-cell>
      <van-cell title="状态" :value="lavsourceStatus" size="large" />
    </van-cell-group>
    <van-cell-group title="详情">
      <van-cell title="名称" :value="lavsourceInfo.name" size="large" value-class="van-ellipsis" />
      <van-cell title="图标" center size="large">
        <template #value>
          <img class="icon" :src="lavsourceInfo.iconUrl" alt="" />
        </template>
      </van-cell>
      <van-cell title="类型" :value="getLavsourceTypeText()" size="large" />
      <van-cell title="包名" :value="lavsourceInfo.packageName" size="large" value-class="van-ellipsis"
                v-if="lavsourceInfo.type === 'local'" />
      <van-cell title="URL" :value="lavsourceInfo.baseUrl" size="large" value-class="van-ellipsis"
                v-if="lavsourceInfo.type === 'network'" />
    </van-cell-group>
  </top-layer-settings-view>
</template>

<script setup>
import TopLayerSettingsView from '@/components/common/TopLayerSettingsView.vue'
import { onMounted, reactive, ref } from 'vue'
import lavsourceJsInterface from '@/androidJsInterfaces/lavsourceJsInterface'

const lavsourceInfo = ref({} ?? {
  id: 0,
  type: '',
  name: '',
  packageName: '',
  iconUrl: '',
  baseUrl: '',
  enabled: false
})

const lavsourceStatus = ref('未知')

const status = reactive({
  enableStatusChanging: false
})

onMounted(() => {
  lavsourceInfo.value = history.state.lavsourceInfo
  loadLavsourceStatus()
})

function getLavsourceTypeText() {
  switch(lavsourceInfo.value.type) {
    case 'local': return '本地信源'
    case 'network': return '网络信源'
    default: return '未知'
  }
}

function loadLavsourceStatus() {
  lavsourceStatus.value = '获取中……'
  lavsourceJsInterface.getLavsourceStatus(lavsourceInfo.value.id).then(status => {
    lavsourceStatus.value = status === true ? '正常' : '不可用'
  }).catch(() => {
    lavsourceStatus.value = '获取失败'
  })
}

function onEnableStatusChange() {
  status.enableStatusChanging = true
  lavsourceJsInterface.changeLavsourceEnableStatus(
    lavsourceInfo.value.id,
    lavsourceInfo.value.enabled
  ).then(() => {}).catch(() => {
    lavsourceInfo.value.enabled = !lavsourceInfo.value.enabled
  }).finally(() => {
    status.enableStatusChanging = false
  })
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
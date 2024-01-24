<template>
  <top-layer-settings-view title="添加本地信源">
    <van-cell-group inset title="可添加信源">
      <van-list class="local-lavsource-list"
                v-model:loading="componentParams.localLavsourceList.loading"
                :finished="componentParams.localLavsourceList.loadFinished"
                :finished-text="componentParams.localLavsourceList.loadFinishedText"
                @load="loadLocalLavsourceList">
        <van-cell v-for="item in localLavsourceList" :key="item.id" size="large" is-link>
          <template #title>
            <div class="item-title">
              <!--suppress JSUnresolvedReference -->
              <img :src="item.imgUrl" alt="" />
              <div class="item-info">
                <div class="item-name">{{ item.name }}</div>
                <div class="item-package-name van-ellipsis">{{ item.packageName }}</div>
              </div>
            </div>
          </template>
        </van-cell>
      </van-list>
    </van-cell-group>
  </top-layer-settings-view>
</template>

<script setup>
import TopLayerSettingsView from '@/components/common/TopLayerSettingsView.vue'
import { reactive, ref } from 'vue'
import lavsourceJsInterface from '@/androidJsInterfaces/lavsourceJsInterface'

const componentParams = reactive({
  localLavsourceList: {
    loading: false,
    loadFinished: false,
    loadFinishedText: ''
  }
})

const localLavsourceList = ref([]) ?? [
  {
    id: 0,
    name: '',
    packageName: '',
    imgUrl: ''
  }
]

function loadLocalLavsourceList() {
  lavsourceJsInterface.getLocalLavsourceListCanBeAdded().then(res => {
    localLavsourceList.value = res.data
  }).catch(() => {
    componentParams.localLavsourceList.loadFinishedText = '加载失败'
  }).finally(() => {
    componentParams.localLavsourceList.loading = false
    componentParams.localLavsourceList.loadFinished = true
  })
}
</script>

<style scoped lang="scss">
.local-lavsource-list {
  background-color: var(--van-doc-background);

  .item-title {
    display: flex;
    align-items: center;
    height: 45px;
    line-height: 20px;

    img {
      width: 45px;
      height: 100%;
      margin-right: 10px;
    }

    .item-info {
      position: relative;
      top: 1.2px;

      .item-package-name {
        max-width: 247.5px;
        font-size: 14px;
        color: var(--van-cell-group-title-color);
      }
    }
  }
}

::v-deep(.local-lavsource-list) {
  .van-cell {
    align-items: center;
  }

  .van-list__loading, .van-list__finished-text {
    line-height: unset;
  }
}
</style>
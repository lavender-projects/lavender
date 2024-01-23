<template>
  <top-layer-settings-view title="添加信源">
    <van-cell-group inset title="可添加信源">
      <van-list class="lavsource-list"
                v-model:loading="componentParams.lavsourceList.loading"
                :finished="componentParams.lavsourceList.loadFinished"
                :finished-text="componentParams.lavsourceList.loadFinishedText"
                @load="loadLavsourceList">
        <van-cell v-for="item in lavsourceList" size="large" is-link>
          <template #title>
            <div class="item-title">
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
  lavsourceList: {
    loading: false,
    loadFinished: false,
    loadFinishedText: ''
  }
})

const lavsourceList = ref([] ?? [
  {
    name: '',
    packageName: '',
    imgUrl: ''
  }
])

function loadLavsourceList() {
  lavsourceJsInterface.getLavsourceListCanBeAdded().then(res => {
    lavsourceList.value = res.data
  }).catch(() => {
    componentParams.lavsourceList.loadFinishedText = '加载失败'
  }).finally(() => {
    componentParams.lavsourceList.loading = false
    componentParams.lavsourceList.loadFinished = true
  })
}
</script>

<style scoped lang="scss">
.lavsource-list {
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

  ::v-deep(.van-cell) {
    align-items: center;
  }

  ::v-deep(.van-list__loading, .van-list__finished-text) {
    line-height: unset;
  }
}
</style>
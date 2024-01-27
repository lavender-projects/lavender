<template>
  <!-- 最外层div用于使“::v-deep(.lavsource-list)”生效 -->
  <div>
    <div class="lavsource-list" ref="rootDom">
      <van-list v-model:loading="componentParams.lavsourceList.loading"
                :finished="componentParams.lavsourceList.loadFinished"
                :finished-text="componentParams.lavsourceList.loadFinishedText"
                @load="loadLavsourceList">
        <van-cell v-for="(item, index) in lavsourceList" :key="item.id"
                  size="large" is-link @click="emits('itemClick', item, index)">
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
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'

const props = defineProps({
  loadDataApi: Function
})

const componentParams = reactive({
  lavsourceList: {
    loading: false,
    loadFinished: false,
    loadFinishedText: '',
    loadFailed: false
  }
})

const lavsourceList = ref([]) ?? [
  {
    id: 0,
    type: '',
    name: '',
    packageName: '',
    imgUrl: '',
    baseUrl: ''
  }
]

const rootDom = ref()

const emits = defineEmits([ 'itemClick' ])

function loadLavsourceList() {
  componentParams.lavsourceList.loadFailed = false
  props.loadDataApi().then(res => {
    lavsourceList.value = res.data
  }).catch(() => {
    componentParams.lavsourceList.loadFailed = true
  }).finally(() => {
    componentParams.lavsourceList.loading = false
    componentParams.lavsourceList.loadFinished = true
    updateLavsourceListLoadFinishedText()
  })
}

function updateLavsourceListLoadFinishedText() {
  let text
  if(componentParams.lavsourceList.loadFailed) {
    text = '加载失败'
  } else if(lavsourceList.value.length <= 0) {
    text = '暂无信源'
  }
  componentParams.lavsourceList.loadFinishedText = text
}

defineExpose({
  lavsourceList,
  rootDom,
  updateLavsourceListLoadFinishedText
})
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
}

::v-deep(.lavsource-list) {
  .van-cell {
    align-items: center;
  }

  .van-list__loading, .van-list__finished-text {
    line-height: unset;
  }
}
</style>
<template>
  <div class="video-view">
    <div class="nav">
      <div class="left">Lavender</div>
      <div class="right">
        <van-icon name="search" @click="search()" />
      </div>
    </div>
    <div class="content">
      <!--
        ref的名称不可以和组件名称同名！即使它们的命名法不同。
        如：scroll-block的ref名不可以为scrollBlock
      -->
      <scroll-block ref="scrollBlockComponent" @scroll-end="onContentBlockScollEnd">
        <van-pull-refresh pull-distance="80" v-model:model-value="status.pullRefreshing" @refresh="onRefresh">
          <div class="load-on-end-block" ref="loadOnEndBlockDom">
            <van-list v-model:loading="status.scrolledToBottom"
                      :finished="status.loadFinished"
                      finished-text="暂无更多"
                      @load="onLoad">
              <recommended-video-item-container :items="recommendedVideoListStore.videoItems" />
            </van-list>
          </div>
        </van-pull-refresh>
      </scroll-block>
    </div>
  </div>
</template>

<script setup>
import { showToast } from 'vant'
import { onMounted, reactive, ref } from 'vue'
import RecommendedVideoItemContainer from '@/components/video/RecommendedVideoItemContainer.vue'
import { useRecommendedVideoListStore } from '@/stores/video'
import ScrollBlock from '@/components/common/ScrollBlock.vue'
import codeUtils from '@/utils/code'
import videoJsInterface from '@/androidJsInterfaces/videoJsInterface'

const recommendedVideoListStore = useRecommendedVideoListStore()

const status = reactive({
  pullRefreshing: false,
  scrolledToBottom: false,
  loadFinished: false,
  loading: false
})

const scrollBlockComponent = ref()

const loadOnEndBlockDom = ref()

function search() {
  showToast('search')
}

function onRefresh() {
  if(status.loading) return
  status.loading = true
  videoJsInterface.recommendedVideoList().then(res => {
    recommendedVideoListStore.videoItems = res.data.concat(recommendedVideoListStore.videoItems)
    status.loadFinished = false
  }).finally(() => {
    if(recommendedVideoListStore.videoItems.length > 30) {
      recommendedVideoListStore.videoItems.splice(recommendedVideoListStore.videoItems.length - 10, 10)
    }
    status.pullRefreshing = false
    status.loading = false
  })
}

async function onLoad() {
  await waitForScrollToLoadingText()
  if(status.loading) return
  status.loading = true
  videoJsInterface.recommendedVideoList().then(res => {
    recommendedVideoListStore.videoItems = recommendedVideoListStore.videoItems.concat(res.data)
  }).catch(() => {
    //防止因为页面中没有数据，而反复触发加载
    status.loadFinished = true
  }).finally(() => {
    if(recommendedVideoListStore.videoItems.length > 30) {
      recommendedVideoListStore.videoItems.splice(0, 10)
    }
    status.scrolledToBottom = false
    status.loading = false
  })
}

async function waitForScrollToLoadingText() {
  let contentWrapperDom = scrollBlockComponent.value.getContentWrapperDom()
  let vanListLoadingDom
  for(let i = 0; i < 20; i++) {
    vanListLoadingDom = contentWrapperDom.querySelector('.van-list__loading')
    if(vanListLoadingDom != null) break
    await codeUtils.sleep(10)
  }
  showLoadingTextBar(false)
  let contentWrapperHeight = codeUtils.getDomHeight(contentWrapperDom)
  let maxScrollTop = codeUtils.getDomHeight(loadOnEndBlockDom.value) - contentWrapperHeight
  for(; ; ) {
    let distanceToBottom = maxScrollTop - contentWrapperDom.scrollTop
    if(distanceToBottom <= 5 || status.loading) break
    await codeUtils.sleep(100)
  }
  showLoadingTextBar(true)
}

function showLoadingTextBar(show) {
  let contentWrapperDom = scrollBlockComponent.value.getContentWrapperDom()
  let vanListLoadingDom = contentWrapperDom.querySelector('.van-list__loading')
  if(vanListLoadingDom == null) {
    vanListLoadingDom = contentWrapperDom.querySelector('.van-list__finished-text')
  }
  if(vanListLoadingDom == null) return
  if(show) {
    let beforeLoadingTextDom = vanListLoadingDom.querySelector('.before-loading-text')
    if(beforeLoadingTextDom != null) {
      vanListLoadingDom.removeChild(beforeLoadingTextDom)
    }
  } else {
    let beforeLoadingTextDom = document.createElement('div')
    beforeLoadingTextDom.innerHTML = '上滑加载更多'
    beforeLoadingTextDom.classList.add('before-loading-text')
    vanListLoadingDom.appendChild(beforeLoadingTextDom)
  }
  let loadingTextBarDom = vanListLoadingDom.querySelector('.van-loading')
  if(loadingTextBarDom != null) {
    loadingTextBarDom.style.display = show ? 'block' : 'none'
  }
}

/**
 * 当内容块的滚动动作结束时回调（并非滚动到底部时回调）
 */
function onContentBlockScollEnd() {
  recommendedVideoListStore.nowPosition = scrollBlockComponent.value.getScrollTopValue()
}

onMounted(() => {
  scrollBlockComponent.value.contentWrapperScrollBy(recommendedVideoListStore.nowPosition)
})
</script>

<style scoped lang="scss">
* {
  --nav-height: 50px;
  --content-height: calc(100vh - var(--van-tabbar-height) - var(--nav-height));
}

.video-view {
  .nav {
    height: calc(var(--nav-height) - 20px);
    padding: 10px 20px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    background-color: white;
    font-size: 30px;
    border-bottom: 1.25px solid var(--van-border-color);

    .left {
      font-family: 'Custom Kunstler', serif;
      font-weight: bold;
    }
  }

  .content {
    height: var(--content-height);
    background-color: var(--van-doc-background);

    .load-on-end-block {
      min-height: var(--content-height);
    }
  }
}
</style>
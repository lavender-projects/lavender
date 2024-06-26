<template>
  <div class="comment-list-container" @touchmove="onTouchMove">
    <div ref="commentListComponentWrapperDom">
      <comment-list :get-scroll-top="getScrollTop"
                    :get-max-scroll-top="getMaxScrollTop"
                    :get-load-comment-list-request="props.getLoadCommentListRequest"
                    :pull-refresh-disabled="props.pullRefreshDisabled"
                    @comment-item-reply-click="onCommentItemReplyClick"
                    @loading="showFloatLoadingBlockDom(true)"
                    @loaded="showFloatLoadingBlockDom(false)" />
    </div>
    <div style="display: none;" ref="commentReplyListComponentWrapperDom">
      <comment-reply-list ref="commentReplyListComponent"
                          :root-comment-data="rootCommentDataOfReplyList"
                          :get-scroll-top="getScrollTop"
                          :get-max-scroll-top="getMaxScrollTop"
                          :get-load-reply-list-request="props.getLoadCommentReplyListRequest"
                          @loaded="emits('commentReplyListLoaded')"
                          @close="onCommentReplyListClose" />
    </div>
    <div class="float-loading-block" ref="floatLoadingBlockDom">
      <van-loading color="rgb(25, 137, 250)" />
    </div>
  </div>
</template>

<script setup>
import CommentList from '@/components/common/CommentList.vue'
import { onMounted, reactive, ref } from 'vue'
import codeUtils from '@/utils/code'
import CommentReplyList from '@/components/common/CommentReplyList.vue'

const props = defineProps({
  getScrollTop: Function,
  getMaxScrollTop: Function,
  getLoadCommentListRequest: Function,
  getLoadCommentReplyListRequest: Function,
  pullRefreshDisabled: {
    type: Boolean,
    default: false
  }
})

const componentParams = reactive({
  cachedScrollTopValue: 0
})

const status = reactive({
  commentReplyListDisplaying: false
})

const commentListComponentWrapperDom = ref()

const commentReplyListComponentWrapperDom = ref()

const commentReplyListComponent = ref()

const floatLoadingBlockDom = ref()

let commentListPullRefreshTrackDom

let commentListPullRefreshHeadDom

const rootCommentDataOfReplyList = ref({})

let beforeCommentReplyListShowEventListenerWaiter

const emits = defineEmits([
  'beforeCommentReplyListShow',
  'commentReplyListShow',
  'commentReplyListLoaded',
  'commentReplyListClosed'
])

onMounted(() => {
  loadDomAndCssValues()
})

async function loadDomAndCssValues() {
  commentListPullRefreshTrackDom = await codeUtils.tryForResult(() => {
    return commentListComponentWrapperDom.value.querySelector('.van-pull-refresh__track')
  })
  commentListPullRefreshHeadDom = commentListPullRefreshTrackDom.querySelector('.van-pull-refresh__head')
}

async function showFloatLoadingBlockDom(show) {
  let dom = floatLoadingBlockDom.value
  if(show) {
    dom.style.display = 'unset'
    await codeUtils.sleep(50)
    dom.style.opacity = '1'
  } else {
    dom.style.opacity = '0'
    await codeUtils.sleep(300)
    dom.style.display = 'none'
  }
}

function onTouchMove() {
  appendBlankDomToPullRefreshTrackDom()
}

async function onCommentItemReplyClick(commentData) {
  if(beforeCommentReplyListShowEventListenerWaiter != null) return
  let waiterResolveTask
  beforeCommentReplyListShowEventListenerWaiter = new Promise(resolve => {
    waiterResolveTask = setTimeout(() => resolve(), 2000)
    emits('beforeCommentReplyListShow', resolve)
  })
  await beforeCommentReplyListShowEventListenerWaiter
  clearTimeout(waiterResolveTask)
  beforeCommentReplyListShowEventListenerWaiter = null
  componentParams.cachedScrollTopValue = props.getScrollTop()
  commentListComponentWrapperDom.value.style.display = 'none'
  rootCommentDataOfReplyList.value = commentData
  await commentReplyListComponent.value.refresh()
  commentReplyListComponentWrapperDom.value.style.display = 'block'
  status.commentReplyListDisplaying = true
  emits('commentReplyListShow')
}

function onCommentReplyListClose() {
  commentListComponentWrapperDom.value.style.display = 'block'
  commentReplyListComponentWrapperDom.value.style.display = 'none'
  status.commentReplyListDisplaying = false
  emits('commentReplyListClosed', componentParams.cachedScrollTopValue)
}

function closeCommentReplyList() {
  if(!status.commentReplyListDisplaying) return false
  onCommentReplyListClose()
  return true
}

function appendBlankDomToPullRefreshTrackDom() {
  if(props.getScrollTop() > 0) return
  let blankDom = document.createElement('div')
  commentListPullRefreshHeadDom.appendChild(blankDom)
  setTimeout(() => {
    commentListPullRefreshHeadDom.removeChild(blankDom)
  }, 50)
}

defineExpose({
  closeCommentReplyList,
  getCachedScrollTopValue: () => componentParams.cachedScrollTopValue
})
</script>

<style scoped lang="scss">
.float-loading-block {
  position: absolute;
  top: 20px;
  background-color: white;
  left: 50%;
  transform: translate(-50%);
  padding: 10px;
  border: 1px solid var(--van-border-color);
  border-radius: 10px;
  opacity: 0;
  transition: 0.3s opacity;
  display: none;
}
</style>
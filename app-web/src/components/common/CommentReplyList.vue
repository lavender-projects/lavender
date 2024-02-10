<template>
  <div class="comment-list-load-on-end-block" ref="commentListLoadOnEndBlockDom">
    <div class="comment-list-header">
      <div class="title-1">评论详情</div>
      <div class="close-icon-container">
        <div class="close-icon" @click="emits('close')">
          <van-icon name="cross" />
        </div>
      </div>
    </div>
    <van-list class="comment-list"
              v-model:loading="status.commentListScrolledToBottom"
              :finished="status.commentLoadFinished"
              finished-text="暂无更多"
              @load="onCommentListBlockScrollToBottom">
      <comment-item v-if="props.rootCommentData.id != null"
                    :key="props.rootCommentData.id"
                    :comment-data="props.rootCommentData"
                    :show-preview-replies="false" />
      <div class="gap" v-if="commentList.length > 0"></div>
      <div class="comment-list-header">
        <div class="title-2">相关回复共{{ props.rootCommentData.replyCountStr }}条</div>
      </div>
      <comment-item v-for="item in commentList" :key="item.id" :comment-data="item" />
    </van-list>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import codeUtils from '@/utils/code'
import CommentItem from '@/components/common/CommentItem.vue'

const props = defineProps({
  rootCommentData: Object,
  getScrollTop: Function,
  getMaxScrollTop: Function,
  getLoadReplyListRequest: Function
})

const status = reactive({
  commentListScrolledToBottom: false,
  commentLoadFinished: false,
  commentLoading: false
})

const commentList = ref([])

let commentPage = 0

const commentListLoadOnEndBlockDom = ref()

const emits = defineEmits([ 'loaded', 'close' ])

function loadCommentList() {
  if(status.commentLoading) return
  status.commentLoading = true
  let oldCommentPage = commentPage
  commentPage = commentPage + 1
  props.getLoadReplyListRequest(props.rootCommentData.id, commentPage).then(res => {
    if(res.list.length <= 0) {
      status.commentLoadFinished = true
      return
    }
    commentList.value = commentList.value.concat(res.list)
    status.commentLoadFinished = false
  }).catch(() => {
    if(commentList.value.length <= 0) {
      status.commentLoadFinished = true
    }
    commentPage = oldCommentPage
  }).finally(() => {
    status.commentLoading = false
    status.commentListScrolledToBottom = false
    emits('loaded')
  })
}

async function onCommentListBlockScrollToBottom() {
  await waitForScrollToCommentListLoadingText()
  loadCommentList()
}

async function waitForScrollToCommentListLoadingText() {
  let vanListLoadingDom
  for(let i = 0; i < 20; i++) {
    vanListLoadingDom = commentListLoadOnEndBlockDom.value.querySelector('.van-list__loading')
    if(vanListLoadingDom != null) break
    await codeUtils.sleep(10)
  }
  showCommentListLoadingTextBar(false)
  let maxScrollTop = props.getMaxScrollTop(commentListLoadOnEndBlockDom.value)
  for(; ; ) {
    let distanceToBottom = maxScrollTop - props.getScrollTop()
    if(distanceToBottom <= 5 || status.commentLoading) break
    await codeUtils.sleep(100)
  }
  showCommentListLoadingTextBar(true)
}

function showCommentListLoadingTextBar(show) {
  let vanListLoadingDom = commentListLoadOnEndBlockDom.value.querySelector('.van-list__loading')
  if(vanListLoadingDom == null) {
    vanListLoadingDom = commentListLoadOnEndBlockDom.value.querySelector('.van-list__finished-text')
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

//更改主评论ID后，调用此方法重新根据新的评论ID加载其回复列表
async function refresh() {
  status.commentListScrolledToBottom = true
  status.commentLoadFinished = false
  status.commentLoading = false
  commentList.value = []
  commentPage = 0
  await codeUtils.sleep(10)
  loadCommentList()
}

defineExpose({
  refresh
})
</script>

<style scoped lang="scss">
.comment-list-load-on-end-block {
  position: relative;

  .comment-list-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 15px 12.3px 0;

    .title-1 {
      font-size: 13.6px;
    }

    .title-2 {
      font-size: 13.3px;
      color: var(--van-gray-6);
    }

    .close-icon-container {
      position: relative;
      height: 18.4px;

      .close-icon {
        position: absolute;
        right: 0;
        top: 1px;
        font-size: 20.3px;
        color: var(--van-gray-6);
      }
    }
  }

  .comment-list {
    min-height: calc(100vh - var(--van-tabs-line-height) * 2 - 33.4px);

    .gap {
      background-color: rgb(241, 241, 241);
      height: 10px;
    }
  }
}
</style>
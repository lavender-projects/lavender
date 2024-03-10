<template>
  <div class="comment-list-load-on-end-block" ref="commentListLoadOnEndBlockDom">
    <van-pull-refresh pull-distance="80"
                      v-model:model-value="status.commentListPullRefreshing"
                      :disabled="props.pullRefreshDisabled"
                      @refresh="onCommentListPullRefresh">
      <div class="comment-list-header">
        <div class="title">{{ commentListTitle }}</div>
        <div class="sort-by-text" @click="changeSortCondition">
          <list-icon class="sort-by-icon" color="#969799" />
          <div>{{ commentSortByText }}</div>
        </div>
      </div>
      <van-list class="comment-list"
                v-model:loading="status.commentListScrolledToBottom"
                :finished="status.commentLoadFinished"
                finished-text="暂无更多"
                @load="onCommentListBlockScrollToBottom">
        <comment-item v-if="commentList.top != null"
                      :show-pinned-top-tag="true"
                      :comment-data="commentList.top"
                      @reply-item-click="onCommentItemReplyClick(commentList.top)" />
        <comment-item v-for="item in commentList.list"
                      :key="item.id"
                      :comment-data="item"
                      @reply-item-click="onCommentItemReplyClick(item)" />
      </van-list>
    </van-pull-refresh>
  </div>
</template>

<script setup>
import ListIcon from '@/components/icon/ListIcon.vue'
import { onMounted, reactive, ref } from 'vue'
import codeUtils from '@/utils/code'
import CommentItem from '@/components/common/CommentItem.vue'

const props = defineProps({
  getScrollTop: Function,
  getMaxScrollTop: Function,
  getLoadCommentListRequest: Function,
  pullRefreshDisabled: {
    type: Boolean,
    default: false
  }
})

const status = reactive({
  commentListScrolledToBottom: false,
  commentLoadFinished: false,
  commentListPullRefreshing: false,
  commentLoading: false
})

const commentList = ref({
  top: null,
  list: []
})

let commentPage = 0

const commentSortBy = ref('like_count')

const commentListTitle = ref('热门评论')

const commentSortByText = ref('按热度')

const commentListLoadOnEndBlockDom = ref()

const emits = defineEmits([ 'commentItemReplyClick', 'loading', 'loaded' ])

onMounted(() => {
  emits('loading')
  loadCommentList(true)
})

function loadCommentList(replace = false) {
  status.commentListPullRefreshing = false
  if(status.commentLoading) return
  status.commentLoading = true
  let oldCommentPage = commentPage
  commentPage = replace ? 1 : commentPage + 1
  props.getLoadCommentListRequest(commentSortBy.value, commentPage).then(res => {
    if(res.top != null) {
      commentList.value.top = res.top
    }
    if(res.list.length <= 0) {
      status.commentLoadFinished = true
      return
    }
    commentList.value.list = replace ? res.list : commentList.value.list.concat(res.list)
    changeListTitleAndSortByText()
    status.commentLoadFinished = false
  }).catch(() => {
    if(commentList.value.list.length <= 0) {
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

function onCommentListPullRefresh() {
  emits('loading')
  loadCommentList(true)
}

function changeSortCondition() {
  commentSortBy.value = commentSortBy.value === 'like_count' ? 'send_time' : 'like_count'
  emits('loading')
  loadCommentList(true)
}

function changeListTitleAndSortByText() {
  switch(commentSortBy.value) {
    case 'send_time':
      commentListTitle.value = '最新评论'
      commentSortByText.value = '按时间'
      break
    case 'like_count':
    default:
      commentListTitle.value = '热门评论'
      commentSortByText.value = '按热度'
      break
  }
}

function onCommentItemReplyClick(commentData) {
  emits('commentItemReplyClick', commentData)
}
</script>

<style scoped lang="scss">
.comment-list-load-on-end-block {
  position: relative;

  .comment-list-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 15px 12.3px 0;

    .title {
      font-size: 13.6px;
    }

    .sort-by-text {
      display: flex;
      align-items: center;
      font-size: 12px;
      color: var(--van-gray-6);

      .sort-by-icon {
        width: 14px;
        height: 14px;
        margin-right: 2.3px;
        position: relative;
        top: 0.3px;
      }
    }
  }

  .comment-list {
    min-height: calc(100vh - var(--van-tabs-line-height) * 2 - 33.4px);
  }
}
</style>
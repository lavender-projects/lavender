<template>
  <div class="comment-list-container">
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
                          @close="onCommentReplyListClose" />
    </div>
    <div class="float-loading-block" ref="floatLoadingBlockDom">
      <van-loading color="rgb(25, 137, 250)" />
    </div>
  </div>
</template>

<script setup>
import CommentList from '@/components/common/CommentList.vue'
import { reactive, ref } from 'vue'
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

const rootCommentDataOfReplyList = ref({})

const emits = defineEmits([ 'commentItemReplyClick', 'commentReplyListClose' ])

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

async function onCommentItemReplyClick(commentData) {
  componentParams.cachedScrollTopValue = props.getScrollTop()
  commentListComponentWrapperDom.value.style.display = 'none'
  rootCommentDataOfReplyList.value = commentData
  await commentReplyListComponent.value.refresh()
  commentReplyListComponentWrapperDom.value.style.display = 'block'
  status.commentReplyListDisplaying = true
  emits('commentItemReplyClick')
}

function onCommentReplyListClose() {
  commentListComponentWrapperDom.value.style.display = 'block'
  commentReplyListComponentWrapperDom.value.style.display = 'none'
  status.commentReplyListDisplaying = false
  emits('commentReplyListClose', componentParams.cachedScrollTopValue)
}

function closeCommentReplyList() {
  if(!status.commentReplyListDisplaying) return false
  onCommentReplyListClose()
  return true
}

defineExpose({
  closeCommentReplyList
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
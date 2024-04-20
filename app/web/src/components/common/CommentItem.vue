<template>
  <div class="comment-item" ref="commentItemDom">
    <div class="sender">
      <van-image round fit="cover" :src="props.commentData.sender.avatar"
                 :width="componentParams.commentSenderAvatarSize"
                 :height="componentParams.commentSenderAvatarSize"
                 @click="onUserNameClick($event, props.commentData.sender.name)" />
      <div class="name-container">
        <div class="name-and-level">
          <div class="name van-ellipsis">
            <span @click="onUserNameClick($event, props.commentData.sender.name)">{{ props.commentData.sender.name }}</span>
          </div>
          <img class="level" :src="componentParams.senderLevelImgUrl" alt="" />
        </div>
        <div class="time-and-ip-location">
          <span>{{ props.commentData.sendDate }}</span>
          <!--suppress JSUnresolvedReference -->
          <span style="margin-left: 0.6em;">IP属地：{{ props.commentData.sender.ipLocation }}</span>
        </div>
      </div>
    </div>
    <div class="comment-content">
      <span v-if="showPinnedTopTag === true" class="pinned-top-text">置顶</span>
      <span v-html="props.commentData.content"></span>
    </div>
    <div class="comment-expand-btn" v-if="showExpandBtn" @click="expand()">展开</div>
    <div class="heat-degree">
      <comment-like-icon class="icon" color="rgb(101, 101, 101)" />
      <div>{{ props.commentData.likeCount }}</div>
    </div>
    <!--suppress JSUnresolvedReference -->
    <div class="preview-replies" v-if="willShowPreviewReplies()">
      <div class="container">
        <div class="reply" v-for="item in props.commentData.previewReplyList" @click="onReplyItemClick">
          <span class="sender-name" @click="onUserNameClick($event, item.sender.name)">{{ item.sender.name }}</span>
          <span>：</span>
          <span v-html="item.content"></span>
        </div>
        <div class="reply-count" v-if="props.commentData.replyCount > props.commentData.previewReplyList.length">
          <span @click="onReplyItemClick">共{{ props.commentData.replyCountStr }}条回复 &gt;</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import codeUtils from '@/utils/code'
import CommentLikeIcon from '@/components/icon/CommentLikeIcon.vue'

const props = defineProps({
  commentData: {
    type: Object,
    default: {
      previewReplyList: [],
      replyCountStr: '',
      sender: {
        level: ''
      }
    }
  },
  showPinnedTopTag: Boolean,
  showPreviewReplies: {
    type: Boolean,
    default: true
  }
})

const componentParams = reactive({
  commentSenderAvatarSize: '29.5px',
  senderLevelImgUrl: ''
})

const commentItemDom = ref()

const showExpandBtn = ref(false)

const emits = defineEmits([ 'replyItemClick' ])

let expanded = false

function expand() {
  let contentDom = commentItemDom.value.querySelector('.comment-content')
  let expandBtnDom = commentItemDom.value.querySelector('.comment-expand-btn')
  if(!expanded) {
    contentDom.style['overflow'] = 'unset'
    contentDom.style['-webkit-line-clamp'] = '999'
    expandBtnDom.innerText = '收起'
  } else {
    contentDom.style['overflow'] = 'hidden'
    contentDom.style['-webkit-line-clamp'] = '6'
    expandBtnDom.innerText = '展开'
  }
  expanded = !expanded
}

async function calcShowExpandBtn() {
  await codeUtils.sleep(20)
  let contentDom = commentItemDom.value.querySelector('.comment-content')
  showExpandBtn.value = contentDom.scrollHeight > contentDom.clientHeight
}

function calcLevelImageUrl() {
  let level = props.commentData.sender.level
  if(level < 0) level = 0
  if(level > 9) level = 9
  componentParams.senderLevelImgUrl = `/img/level/lv${level}.png`
}

onMounted(() => {
  calcShowExpandBtn()
  calcLevelImageUrl()
})

function onReplyItemClick() {
  emits('replyItemClick')
}

function onUserNameClick(event, userName) {
  console.log(userName)
  event.stopPropagation()
}

function willShowPreviewReplies() {
  return props.commentData.previewReplyList.length > 0 && props.showPreviewReplies
}
</script>

<style scoped lang="scss">
.comment-item {
  padding: 14px 14.3px 0;
  border-bottom: 1px solid var(--van-border-color);
  --comment-content-line-height: 23.6px;

  .sender {
    display: flex;
    padding-bottom: 8px;

    .name-container {
      position: relative;
      top: -1.8px;
      margin-left: 13.8px;
      font-size: 13px;
      width: calc(100vw - 71.9px);

      .name-and-level {
        display: flex;
        align-items: center;
        height: 14.4px;

        .name {
          max-width: calc(100% - 31.16px);
          line-height: 14.4px;
          margin-right: 5px;
        }

        .level {
          width: 20px;
          height: auto;
          position: relative;
          bottom: 0.7px;
        }
      }

      .time-and-ip-location {
        margin-top: 3.7px;
        color: var(--van-gray-6);
        font-size: 11px;
      }
    }
  }

  .comment-content {
    padding-left: 43.2px;
    font-size: 14.7px;
    line-height: var(--comment-content-line-height);
    white-space: pre-wrap;
    display: -webkit-box;
    overflow: hidden;
    -webkit-line-clamp: 6;
    -webkit-box-orient: vertical;

    .pinned-top-text {
      color: rgb(25, 137, 250);
      font-size: 12px;
      border: 1.2px solid rgb(25, 137, 250);
      padding: 1.8px 3px;
      border-radius: 3px;
      position: relative;
      top: -0.9px;
      margin: 0 2.3px;
    }
  }

  ::v-deep(.comment-emoticon) {
    display: inline-block;
    vertical-align: bottom;
    padding-top: 4px;
    padding-left: 2px;
    padding-right: 2px;
    position: relative;
    top: -2.3px;
    width: var(--comment-content-line-height);
    height: var(--comment-content-line-height);
  }

  ::v-deep(.comment-emoticon-size-1) {
    width: var(--comment-content-line-height);
    height: var(--comment-content-line-height);
  }

  ::v-deep(.comment-emoticon-size-2) {
    width: 46.5px;
    height: 46.5px;
  }

  .comment-expand-btn {
    padding-left: 43.2px;
    font-size: 14.7px;
    line-height: var(--comment-content-line-height);
    color: rgb(30, 110, 175);
  }

  .heat-degree {
    display: flex;
    align-items: center;
    padding-left: 43.2px;
    padding-top: 8px;
    padding-bottom: 9.5px;
    color: rgb(101, 101, 101);
    font-size: 12px;

    .icon {
      width: 17px;
      height: 17px;
      margin-right: 4.3px;
    }
  }

  .preview-replies {
    padding-left: 43.2px;
    padding-bottom: 15px;

    .container {
      background-color: rgb(246, 247, 249);
      padding: 9.5px 10px;
      border-radius: 4px;
    }

    .reply {
      font-size: 14.7px;
      line-height: var(--comment-content-line-height);
      white-space: pre-wrap;
      display: -webkit-box;
      overflow: hidden;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;

      .sender-name {
        color: rgb(30, 110, 175);
      }
    }

    .reply + .reply {
      margin-top: 1.8px;
    }

    .reply-count {
      margin-top: 1.8px;
      color: rgb(30, 110, 175);
      font-size: 12.2px;
    }
  }
}
</style>
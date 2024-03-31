<template>
  <div class="video-playing-view" ref="videoPlayingViewDom">
    <div class="player-top-bar" ref="playerTopBarDom">
      <div class="left">
        <back-icon :color="componentParams.backIconColor" @click="onBackIconClick" />
      </div>
      <div class="play-button-container">
        <div class="play-button" ref="topBarPlayBtnDom" @click="onTopBarPlayBtnClick">
          <play-icon class="icon" :color="componentParams.backIconColor" />
          <div class="text">立即播放</div>
        </div>
      </div>
    </div>
    <div class="custom-player-wrapper" ref="customPlayerWrapperDom">
      <custom-video-player ref="videoPlayerComponent"
                           :lavsource-id="props.lavsourceId"
                           :video-id="props.videoId"
                           :episode-id="nowEpisodeId"
                           @playing-status-changed="onVideoPlayingStatusChanged"
                           @playing-finished="onVideoPlayingFinished"
                           @before-control-bar-show-status-change="beforePlayerControlBarShowStatusChange" />
    </div>
    <div class="tab-and-content-wrapper">
      <div class="tab-and-content">
        <van-tabs shrink sticky animated :swipeable="componentParams.tabPageSwipeable"
                  v-model:active="componentParams.activeTabName"
                  :offset-top="componentParams.tabBarOffsetTop"
                  @change="onTabChange">
          <van-tab name="videoDetails">
            <template #title>
              <div class="tab-title">简介</div>
            </template>
            <scroll-block class="content" ref="videoDetailsTabPageContentComponent"
                          @scroll="onContentWrapperScroll">
              <div class="blank"></div>
              <div class="main-part">
                <div class="video-detail">
                  <div class="uploader">
                    <van-image round fit="cover" :src="videoDetails.uploader.avatar"
                               :width="componentParams.uploaderAvatarSize"
                               :height="componentParams.uploaderAvatarSize" />
                    <div class="name-container">
                      <div>{{ videoDetails.uploader.name }}</div>
                      <div class="fans">
                        <span>{{ videoDetails.uploader.followerCount }}粉丝</span>
                        <span style="margin-left: 0.9em;">{{ videoDetails.uploader.publishedVideosCount }}视频</span>
                      </div>
                    </div>
                  </div>
                  <div class="title-bar">
                    <expand-block class="title" ref="videoTitleBlockComponent"
                                  :expand="componentParams.videoDetailExpanded"
                                  :before-expand-change="beforeTitleBlockExpandChange"
                                  :after-expand-change="afterTitleBlockExpandChange"
                                  @click="onTitleArrowClick">
                      {{ videoDetails.title }}
                    </expand-block>
                    <div class="arrow" @click="onTitleArrowClick">
                      <van-icon :name="componentParams.titleArrowIconName" />
                    </div>
                  </div>
                  <div class="counts">
                    <play-count-icon class="icon" :color="componentParams.countIconColor" />
                    <span>{{ videoDetails.playCount }}</span>
                    <danmaku-icon class="icon" :color="componentParams.countIconColor" />
                    <span>{{ videoDetails.danmakuCount }}</span>
                    <span>{{ videoDetails.publishTime }}</span>
                  </div>
                  <expand-block class="description" ref="videoDescriptionBlockComponent"
                                :expand="componentParams.videoDetailExpanded"
                                :before-expand-change="beforeDescriptionBlockExpandChange">
                    <div class="video-id">{{ videoDetails.id }}</div>
                    <div class="description-content">{{ videoDetails.description }}</div>
                    <div class="tags">
                      <div class="tag" v-for="item in videoDetails.tagList">
                        <div>{{ item }}</div>
                      </div>
                    </div>
                  </expand-block>
                  <div class="heat-degree">
                    <div class="item">
                      <like-icon class="icon" :color="componentParams.heatDegreeIconColor" />
                      <div class="count">{{ videoDetails.likeCount }}</div>
                    </div>
                    <div class="item">
                      <coin-icon class="icon" :color="componentParams.heatDegreeIconColor" />
                      <div class="count">{{ videoDetails.coinCount }}</div>
                    </div>
                    <div class="item">
                      <collect-icon class="icon" :color="componentParams.heatDegreeIconColor" />
                      <div class="count">{{ videoDetails.collectCount }}</div>
                    </div>
                    <div class="item">
                      <share-icon class="icon" :color="componentParams.heatDegreeIconColor" />
                      <div class="count">{{ videoDetails.shareCount }}</div>
                    </div>
                  </div>
                </div>
                <video-info-list class="related-video-list" :video-info-list="videoDetails.relatedVideoList" />
              </div>
            </scroll-block>
          </van-tab>
          <van-tab name="commentList">
            <template #title>
              <div class="tab-title">
                <div>评论</div>
                <div class="comment-count">{{ videoDetails.replyCount }}</div>
              </div>
            </template>
            <scroll-block class="content" ref="commentListTabPageContentComponent"
                          @scroll="onContentWrapperScroll">
              <div class="blank"></div>
              <div class="main-part">
                <comment-list-container ref="commentListContainerComponent"
                                        :get-scroll-top="() => getContentComponentScrollTop('commentList')"
                                        :get-max-scroll-top="() => getContentComponentMaxScrollTop('commentList')"
                                        :get-load-comment-list-request="getLoadCommentListRequest"
                                        :get-load-comment-reply-list-request="getLoadCommentReplyListRequest"
                                        :pull-refresh-disabled="componentParams.commentListPullRefreshDisabled"
                                        @comment-item-reply-click="onCommentItemReplyClick"
                                        @comment-reply-list-close="onCommentReplyListClose" />
              </div>
            </scroll-block>
          </van-tab>
        </van-tabs>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, onUnmounted, reactive, ref } from 'vue'
import BackIcon from '@/components/icon/BackIcon.vue'
import ScrollBlock from '@/components/common/ScrollBlock.vue'
import DanmakuIcon from '@/components/icon/DanmakuIcon.vue'
import PlayCountIcon from '@/components/icon/PlayCountIcon.vue'
import LikeIcon from '@/components/icon/LikeIcon.vue'
import CoinIcon from '@/components/icon/CoinIcon.vue'
import CollectIcon from '@/components/icon/CollectIcon.vue'
import ShareIcon from '@/components/icon/ShareIcon.vue'
import ExpandBlock from '@/components/common/ExpandBlock.vue'
import codeUtils from '@/utils/code'
import CommentListContainer from '@/components/common/CommentListContainer.vue'
import CustomVideoPlayer from '@/components/video/CustomVideoPlayer.vue'
import androidEventListeners from '@/utils/androidEventListeners'
import VideoInfoList from '@/components/video/VideoInfoList.vue'
import PlayIcon from '@/components/icon/PlayIcon.vue'
import basicJsInterface from '@/androidJsInterfaces/basicJsInterface'
import videoJsInterface from '@/androidJsInterfaces/videoJsInterface'

const props = defineProps({
  videoId: String,
  lavsourceId: String
})

const videoDetails = ref({
  id: '',
  uploader: {
    id: 0,
    name: '',
    avatar: '',
    followerCount: '0',
    publishedVideosCount: '0'
  },
  title: '',
  description: '',
  tagList: [],
  playCount: '',
  danmakuCount: '',
  publishTime: '',
  replyCount: '',
  likeCount: '',
  coinCount: '',
  collectCount: '',
  shareCount: '',
  relatedVideoList: []
})

const componentParams = reactive({
  uploaderAvatarSize: '32px',
  backIconColor: 'rgb(255, 255, 255)',
  countIconColor: '#969799',
  heatDegreeIconColor: 'rgb(97, 102, 109)',
  titleArrowIconName: 'arrow-down',
  playerWrapperTopPosition: 0,
  tabBarOffsetTop: 0,
  videoDetailExpanded: false,
  canVideoDetailExpand: false,
  activeTabName: '',
  commentListPullRefreshDisabled: false,
  tabPageScrollable: false,
  tabPageSwipeable: true,
  videoPlaying: false,
  cachedVideoPlayingStatus: false,
  commentListPullRefreshPhysicalActionDoing: false,
  playerControlBarShowing: true,
  commentReplyListShowing: false
})

const videoPlayingViewDom = ref()

const playerTopBarDom = ref()

const topBarPlayBtnDom = ref()

const customPlayerWrapperDom = ref()

const videoDetailsTabPageContentComponent = ref()

const commentListTabPageContentComponent = ref()

const videoPlayerComponent = ref()

const commentListContainerComponent = ref()

const videoTitleBlockComponent = ref()

const videoDescriptionBlockComponent = ref()

const episodeInfoList = ref([])

const nowEpisodeId = ref('0')

const domHeightValues = reactive({
  playerTopBarHeight: 0,
  defaultPlayerWrapperHeight: 0,
  maxTabPageContentDomHeight: 0,
  minPlayerWrapperHeightToDisableScroll: 0,
  maxPlayerWrapperHeightToDisableScroll: 0,
  minPlayerWrapperTopPosition: 0
})

const tabPageComponent = {
  videoDetails: null,
  commentList: null
}

const tabPageScrollTopValue = {
  videoDetails: 0,
  commentList: 0
}

let tabPageSwipeBlockDom

let topBarOpacity = 0

onMounted(() => {
  loadDomAndCssValues()
  loadVideoDetails()
  initVideoTitleBlock()
  startVideoPlay()
  registerAndroidEventListeners()
})

onUnmounted(() => {
  unregisterAndroidEventListeners()
})

async function loadDomAndCssValues() {
  tabPageComponent.videoDetails = videoDetailsTabPageContentComponent
  tabPageComponent.commentList = commentListTabPageContentComponent
  tabPageSwipeBlockDom = await codeUtils.tryForResult(() => {
    return videoPlayingViewDom.value.querySelector('.van-swipe__track')
  })
  domHeightValues.playerTopBarHeight = await codeUtils.tryForResult(() => {
    return codeUtils.getDomHeight(playerTopBarDom.value)
  })
  domHeightValues.defaultPlayerWrapperHeight = await codeUtils.tryForResult(() => {
    return codeUtils.getDomHeight(customPlayerWrapperDom.value)
  })
  domHeightValues.maxTabPageContentDomHeight = await codeUtils.tryForResult(() => {
    return codeUtils.getDomHeight(videoPlayingViewDom.value) - domHeightValues.playerTopBarHeight -
        codeUtils.getDomHeight(videoPlayingViewDom.value.querySelector('.van-tabs__wrap'))
  })
  domHeightValues.minPlayerWrapperHeightToDisableScroll =  domHeightValues.playerTopBarHeight +
      domHeightValues.playerTopBarHeight * 0.2
  domHeightValues.maxPlayerWrapperHeightToDisableScroll = domHeightValues.defaultPlayerWrapperHeight -
      domHeightValues.defaultPlayerWrapperHeight * 0.1
  domHeightValues.minPlayerWrapperTopPosition = -domHeightValues.defaultPlayerWrapperHeight +
      domHeightValues.playerTopBarHeight
  componentParams.tabBarOffsetTop = domHeightValues.defaultPlayerWrapperHeight
}

async function loadVideoDetails() {
  videoJsInterface.videoDetails({
    id: props.videoId,
    lavsourceId: props.lavsourceId
  }).then(res => {
    videoDetails.value = res
  })
}

async function initVideoTitleBlock() {
  for(let i = 0; i < 100; i++) {
    await codeUtils.sleep(10)
    let dom = videoTitleBlockComponent.value.getContentDom()
    if(dom == null) continue
    dom.classList.add('van-ellipsis')
    componentParams.canVideoDetailExpand = true
    break
  }
}

async function startVideoPlay() {
  episodeInfoList.value = await videoJsInterface.episodeInfoList({
    lavsourceId: props.lavsourceId,
    videoId: props.videoId
  })
  nowEpisodeId.value = String(episodeInfoList.value[0].id)
  await codeUtils.sleep(50)
  await videoPlayerComponent.value.initAndPlayVideo()
}

async function beforeTitleBlockExpandChange() {
  if(!componentParams.videoDetailExpanded) return
  videoDescriptionBlockComponent.value.calcContentHeight()
  videoTitleBlockComponent.value.getContentDom().classList.remove('van-ellipsis')
  await videoTitleBlockComponent.value.calcAccurateContentHeight()
}

//在展开或收起的CSS动画刚刚开始时被调用
async function afterTitleBlockExpandChange() {
  if(componentParams.videoDetailExpanded) {
    componentParams.canVideoDetailExpand = true
    return
  }
  setTimeout(() => {
    videoTitleBlockComponent.value.getContentDom().classList.add('van-ellipsis')
    componentParams.canVideoDetailExpand = true
  }, 300)
}

async function beforeDescriptionBlockExpandChange() {
  if(videoTitleBlockComponent.value.getCachedAccurateContentHeight() != null) return
  //等待titleBlock高度计算完成
  await codeUtils.sleep(100)
}

function onBackIconClick() {
  basicJsInterface.finishCurrentWebActivity()
}

function onTitleArrowClick() {
  if(!componentParams.canVideoDetailExpand) return
  componentParams.canVideoDetailExpand = false
  //noinspection UnnecessaryLocalVariableJS
  let iconName = componentParams.titleArrowIconName === 'arrow-down' ? 'arrow-up' : 'arrow-down'
  componentParams.titleArrowIconName = iconName
  componentParams.videoDetailExpanded = !componentParams.videoDetailExpanded
}

function onTopBarPlayBtnClick() {
  videoPlayerComponent.value.resumeVideo()
  videoPlayerComponent.value.getOriginalPlayer().control.showTransient()
}

function onContentWrapperScroll() {
  calcPlayerWrapperDomPosition()
  calcPlayerTopBarBackgroundOpacity()
  calcIsTopBarPlayBtnShouldBeShown()
}

function getLoadCommentListRequest(sortBy, page) {
  return videoJsInterface.commentList({
    lavsourceId: props.lavsourceId,
    videoId: props.videoId,
    sortBy,
    page
  })
}

function getLoadCommentReplyListRequest(commentId, page) {
  return videoJsInterface.commentReplyList({
    lavsourceId: props.lavsourceId,
    videoId: props.videoId,
    commentId,
    page
  })
}

function getContentComponentScrollTop(tabPageName) {
  return tabPageComponent[tabPageName].value.getScrollTopValue()
}

function getContentComponentMaxScrollTop(tabPageName) {
  return tabPageComponent[tabPageName].value.getMaxScrollTopValue()
}

function beforePlayerControlBarShowStatusChange(show) {
  componentParams.playerControlBarShowing = show
  setPlayerTopBarHide(!show)
}

function onCommentItemReplyClick() {
  componentParams.tabPageSwipeable = false
  componentParams.commentReplyListShowing = true
}

function onCommentReplyListClose(cachedScrollTopValue) {
  commentListTabPageContentComponent.value.getContentWrapperDom().scrollTo(0, cachedScrollTopValue)
  componentParams.tabPageSwipeable = true
  componentParams.commentReplyListShowing = false
}

function onTabChange() {
  try {
    commentListContainerComponent.value?.closeCommentReplyList()
    calcPlayerWrapperDomPosition()
  } catch(e) {
    //ignore
  }
}

function onVideoPlayingStatusChanged(playing) {
  componentParams.videoPlaying = playing
  calcPlayerTopBarBackgroundOpacity()
  calcIsTopBarPlayBtnShouldBeShown()
}

function onVideoPlayingFinished() {
  //ignore
}

function onDeviceBackButtonPressed() {
  return commentListContainerComponent.value.closeCommentReplyList()
}

function onAndroidActivityPause() {
  componentParams.cachedVideoPlayingStatus = componentParams.videoPlaying
  if(componentParams.videoPlaying) {
    videoPlayerComponent.value.pauseVideo()
  }
  return true
}

function onAndroidActivityResume() {
  if(componentParams.cachedVideoPlayingStatus) videoPlayerComponent.value.resumeVideo()
  return true
}

function calcPlayerWrapperDomPosition() {
  let scrollTop = getContentComponentScrollTop(componentParams.activeTabName)
  let distance = scrollTop - tabPageScrollTopValue[componentParams.activeTabName]
  tabPageScrollTopValue[componentParams.activeTabName] = scrollTop
  let playerWrapperDomTopPosition = componentParams.playerWrapperTopPosition
  let tabBarOffsetTop = componentParams.tabBarOffsetTop
  if(componentParams.videoPlaying) {
    playerWrapperDomTopPosition = 0
    tabBarOffsetTop = domHeightValues.defaultPlayerWrapperHeight
  } else {
    playerWrapperDomTopPosition -= distance
    tabBarOffsetTop -= distance
  }
  if(playerWrapperDomTopPosition > 0) playerWrapperDomTopPosition = 0
  if(playerWrapperDomTopPosition < domHeightValues.minPlayerWrapperTopPosition) {
    playerWrapperDomTopPosition = domHeightValues.minPlayerWrapperTopPosition
  }
  if(tabBarOffsetTop < domHeightValues.playerTopBarHeight) {
    tabBarOffsetTop = domHeightValues.playerTopBarHeight
  }
  if(tabBarOffsetTop > domHeightValues.defaultPlayerWrapperHeight) {
    tabBarOffsetTop = domHeightValues.defaultPlayerWrapperHeight
  }
  customPlayerWrapperDom.value.style.top = `${playerWrapperDomTopPosition}px`
  componentParams.playerWrapperTopPosition = playerWrapperDomTopPosition
  componentParams.tabBarOffsetTop = tabBarOffsetTop
}

function calcPlayerTopBarBackgroundOpacity() {
  topBarOpacity = 1.0 - (
      (componentParams.tabBarOffsetTop - domHeightValues.playerTopBarHeight) /
      (domHeightValues.defaultPlayerWrapperHeight - domHeightValues.playerTopBarHeight)
  )
  if(topBarOpacity > 1.0) topBarOpacity = 1.0
  setPlayerTopBarHide(topBarOpacity <= 0.4)
  playerTopBarDom.value.style.backgroundColor = `rgba(255, 255, 255, ${topBarOpacity})`
  playerTopBarDom.value.style.backgroundImage = `linear-gradient(rgba(0, 0, 0, ${(1 - topBarOpacity) / 2.0}) 0%,` +
      'rgba(0, 0, 0, 0) 100%)'
  //设置顶栏图标颜色
  let iconColor = topBarOpacity > 0.5 ? 'black' : 'white'
  componentParams.backIconColor = iconColor
  topBarPlayBtnDom.value.style.color = iconColor
}

function setPlayerTopBarHide(hide) {
  let alwaysShow = componentParams.playerControlBarShowing || topBarOpacity > 0.4
  if(alwaysShow) hide = false
  playerTopBarDom.value.style.display = hide ? 'none' : 'flex'
}

function calcIsTopBarPlayBtnShouldBeShown() {
  if(componentParams.videoPlaying) {
    topBarPlayBtnDom.value.style.display = 'none'
    return
  }
  if(topBarOpacity > 0.4) {
    topBarPlayBtnDom.value.style.display = 'flex'
    return
  }
  topBarPlayBtnDom.value.style.display = 'none'
}

function registerAndroidEventListeners() {
  androidEventListeners.onBackButtonPressedListeners.videoPlayingView.defaultListener = onDeviceBackButtonPressed
  androidEventListeners.onActivityPauseListeners.videoPlayingView.defaultListener = onAndroidActivityPause
  androidEventListeners.onActivityResumeListeners.videoPlayingView.defaultListener = onAndroidActivityResume
}

function unregisterAndroidEventListeners() {
  delete androidEventListeners.onBackButtonPressedListeners.videoPlayingView.closeCommentReplyList
  delete androidEventListeners.onActivityPauseListeners.videoPlayingView.defaultListener
  delete androidEventListeners.onActivityResumeListeners.videoPlayingView.defaultListener
}
</script>

<style scoped lang="scss">
* {
  --player-wrapper-z-index: 20;
  --player-wrapper-height: calc((9 / 16.0) * 100vw);
  --van-list-text-font-size: 13.6px;
}

.video-playing-view {
  height: 100vh;
  overflow-x: hidden;
  overflow-y: scroll;
}

.custom-player-wrapper {
  width: 100%;
  height: var(--player-wrapper-height);
  z-index: var(--player-wrapper-z-index);
  position: absolute;
  top: 0;
}

.player-top-bar {
  position: fixed;
  top: 0;
  width: 100%;
  height: var(--van-tabs-line-height);
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: rgba(255, 255, 255, 0);
  background-image: linear-gradient(rgba(0, 0, 0, 0.5) 0%, rgba(0, 0, 0, 0) 100%);
  z-index: calc(var(--player-wrapper-z-index) + 1);

  .left {
    display: flex;
    align-items: center;
    margin-left: 10px;
    z-index: calc(var(--player-wrapper-z-index) + 1);

    ::v-deep(.icon-container) {
      width: 26px;
      height: 26px;
    }
  }

  .play-button-container {
    position: absolute;
    top: 0;
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    color: white;

    .play-button {
      display: none;
      align-items: center;
      z-index: calc(var(--player-wrapper-z-index) + 1);

      ::v-deep(.icon-container) {
        width: 20px;
        height: 20px;
      }

      .icon {
        position: relative;
        top: 1px;
      }

      .text {
        margin-left: 4px;
      }
    }
  }
}

.tab-and-content-wrapper {
  width: 100%;
  height: 100vh;
}

.tab-and-content {
  ::v-deep(.van-tabs__nav--line) {
    height: 85%;
  }

  ::v-deep(.van-tabs__wrap) {
    border-bottom: 1px solid var(--van-border-color);
  }
}

.tab-title {
  width: 80px;
  text-align: center;
  font-size: 13.2px;
  display: flex;
  align-items: center;
  justify-content: center;

  .comment-count {
    margin-left: 3px;
    font-size: 10px;
    font-weight: normal;
  }
}

.content {
  height: calc(100vh - var(--van-tabs-line-height) - 0.8px);

  .blank {
    height: var(--player-wrapper-height);
  }

  .main-part {
    min-height: calc(100vh - var(--van-tabs-line-height) - 0.8px);
  }

  .video-detail {
    padding: 13px 13px 12px;
    border-bottom: 1.09px solid var(--van-border-color);

    .uploader {
      display: flex;
      align-items: center;
      padding-bottom: 18px;

      .name-container {
        margin-left: 11px;
        font-size: 14px;

        .fans {
          margin-top: 0.75px;
          color: var(--van-gray-6);
          font-size: 10.3px;
        }
      }
    }

    .title-bar {
      display: flex;
      align-items: flex-start;
      justify-content: space-between;
      padding-bottom: 8px;

      .title {
        width: 90%;
        font-size: 16px;
        height: 20.8px;
      }

      .arrow {
        color: var(--van-gray-6);
      }
    }

    .counts {
      display: flex;
      align-items: center;
      height: 16px;
      color: var(--van-gray-6);

      .icon {
        width: 16px;
        height: 100%;
        margin-right: 3px;
        position: relative;
        bottom: 0.8px;
      }

      span {
        height: 100%;
        font-size: 12px;
        line-height: 16px;
        margin-right: 6px;
      }
    }

    .description {
      height: 0;
      font-size: 12px;
      line-height: 16px;
      color: var(--van-gray-6);

      .video-id {
        padding-top: 4px;
      }

      .description-content {
        padding-top: 9px;
        white-space: pre-wrap;
      }

      .tags {
        display: flex;
        flex-wrap: wrap;
        padding: 13px 0 6px;

        .tag {
          display: flex;
          align-items: center;
          padding: 6px 10px 4px;
          margin-right: 8.5px;
          margin-bottom: 8.5px;
          background-color: #F4F5F6;
          color: #61666D;
          border-radius: 15px;
        }
      }
    }

    .heat-degree {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 17px 30px 0;

      .item {
        display: flex;
        flex-direction: column;
        align-items: center;

        .icon {
          width: 24.5px;
          height: 24.5px;
        }

        .count {
          color: var(--van-gray-6);
          font-size: 10.5px;
          margin-top: 5px;
        }
      }
    }
  }
}
</style>
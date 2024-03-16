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
    <div class="video-detail-and-comment-list">
      <div class="tab-and-content">
        <van-tabs shrink sticky animated :swipeable="componentParams.tabPageSwipeable"
                  v-model:active="componentParams.activeTabName"
                  @change="onTabChange">
          <van-tab name="videoDetails">
            <template #title>
              <div class="tab-title">简介</div>
            </template>
            <div class="content" ref="videoDetailsTabPageContentDom">
              <scroll-block ref="videoDetailsScrollBlockComponent"
                            :scrollable="false">
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
              </scroll-block>
            </div>
          </van-tab>
          <van-tab name="commentList">
            <template #title>
              <div class="tab-title">
                <div>评论</div>
                <div class="comment-count">{{ videoDetails.replyCount }}</div>
              </div>
            </template>
            <div class="content" ref="commentListTabPageContentDom">
              <scroll-block ref="commentListScrollBlockComponent"
                            :scrollable="false">
                <comment-list-container ref="commentListContainerComponent"
                                        :get-scroll-top="getCommentListScrollBlockComponentScrollTop"
                                        :get-max-scroll-top="getCommentListScrollBlockComponentMaxScrollTop"
                                        :get-load-comment-list-request="getLoadCommentListRequest"
                                        :get-load-comment-reply-list-request="getLoadCommentReplyListRequest"
                                        :pull-refresh-disabled="componentParams.commentListPullRefreshDisabled"
                                        @comment-item-reply-click="onCommentItemReplyClick"
                                        @comment-reply-list-close="onCommentReplyListClose" />
              </scroll-block>
            </div>
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
import InertialScrollEngine from '@/utils/inertialScroll'

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
  videoDetailExpanded: false,
  canVideoDetailExpand: false,
  activeTabName: '',
  commentListPullRefreshDisabled: true,
  tabPageScrollable: false,
  tabPageSwipeable: true,
  videoPlaying: false,
  cachedVideoPlayingStatus: false,
  keepMaxTabPageHeight: false,
  commentListPullRefreshPhysicalActionDoing: false,
  lastTimeTabChangeTime: 0,
  playerControlBarShowing: true,
  commentReplyListShowing: false
})

const videoPlayingViewDom = ref()

const playerTopBarDom = ref()

const topBarPlayBtnDom = ref()

const customPlayerWrapperDom = ref()

const videoDetailsTabPageContentDom = ref()

const commentListTabPageContentDom = ref()

const videoPlayerComponent = ref()

const videoDetailsScrollBlockComponent = ref()

const commentListContainerComponent = ref()

const commentListScrollBlockComponent = ref()

const videoTitleBlockComponent = ref()

const videoDescriptionBlockComponent = ref()

const episodeInfoList = ref([])

const nowEpisodeId = ref('0')

const domHeightValues = reactive({
  playerTopBarHeight: 0,
  defaultPlayerWrapperHeight: 0,
  defaultTabPageContentDomHeight: 0,
  maxTabPageContentDomHeight: 0,
  minPlayerWrapperHeightToDisableScroll: 0,
  maxPlayerWrapperHeightToDisableScroll: 0
})

const inertialScrollEngine = new InertialScrollEngine(onTabPageSwipeBlockVerticalSwipe)

let tabPageSwipeBlockDom

let commentListPullRefreshTrackDom

let tabPageSwipeBlockSwipingDirection

let tabPageSwipeBlockTouchPositionOnTouchStart = {
  x: 0,
  y: 0
}

let tabPageSwipeBlockTouchPositionOnTouchMoving = {
  x: 0,
  y: 0
}

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
  tabPageSwipeBlockDom = await codeUtils.tryForResult(() => {
    return videoPlayingViewDom.value.querySelector('.van-swipe__track')
  })
  commentListPullRefreshTrackDom = await codeUtils.tryForResult(() => {
    return commentListTabPageContentDom.value.querySelector('.van-pull-refresh__track')
  })
  tabPageSwipeBlockDom.addEventListener('touchstart', onTabPageSwipeBlockTouchStart, true)
  tabPageSwipeBlockDom.addEventListener('touchmove', onTabPageSwipeBlockTouchMove, true)
  tabPageSwipeBlockDom.addEventListener('touchend', onTabPageSwipeBlockTouchEnd, true)
  domHeightValues.playerTopBarHeight = await codeUtils.tryForResult(() => {
    return codeUtils.getDomHeight(playerTopBarDom.value)
  })
  domHeightValues.defaultPlayerWrapperHeight = await codeUtils.tryForResult(() => {
    return codeUtils.getDomHeight(customPlayerWrapperDom.value)
  })
  domHeightValues.defaultTabPageContentDomHeight = await codeUtils.tryForResult(() => {
    return codeUtils.getDomHeight(videoDetailsScrollBlockComponent.value.getContentWrapperDom())
  })
  domHeightValues.maxTabPageContentDomHeight = await codeUtils.tryForResult(() => {
    return codeUtils.getDomHeight(videoPlayingViewDom.value) - domHeightValues.playerTopBarHeight -
        codeUtils.getDomHeight(videoPlayingViewDom.value.querySelector('.van-tabs__wrap'))
  })
  domHeightValues.minPlayerWrapperHeightToDisableScroll =  domHeightValues.playerTopBarHeight +
      domHeightValues.playerTopBarHeight * 0.2
  domHeightValues.maxPlayerWrapperHeightToDisableScroll = domHeightValues.defaultPlayerWrapperHeight -
      domHeightValues.defaultPlayerWrapperHeight * 0.1
}

function loadVideoDetails() {
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
  await videoPlayerComponent.value.playVideo()
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

function getCommentListScrollBlockComponentScrollTop() {
  return commentListScrollBlockComponent.value.getScrollTopValue()
}

function getCommentListScrollBlockComponentMaxScrollTop() {
  return commentListScrollBlockComponent.value.getMaxScrollTopValue()
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
  commentListScrollBlockComponent.value.getContentWrapperDom().scrollTo(0, cachedScrollTopValue)
  componentParams.tabPageSwipeable = true
  componentParams.commentReplyListShowing = false
}

function getActiveScrollBlockComponent() {
  switch(componentParams.activeTabName) {
    case 'videoDetails':
      return videoDetailsScrollBlockComponent.value
    case 'commentList':
      return commentListScrollBlockComponent.value
  }
}

function onTabChange() {
  componentParams.lastTimeTabChangeTime = Date.now()
  inertialScrollEngine.stopScroll()
  try {
    calcIsKeepMaxTabPageHeight()
  } catch(e) {
    //ignore
  }
}

function onTabPageSwipeBlockTouchStart(e) {
  tabPageSwipeBlockTouchPositionOnTouchStart.x = e.changedTouches[0].clientX
  tabPageSwipeBlockTouchPositionOnTouchStart.y = e.changedTouches[0].clientY
  tabPageSwipeBlockTouchPositionOnTouchMoving = { ...tabPageSwipeBlockTouchPositionOnTouchStart }
  calcIsCommentListPullRefreshDisabled()
  inertialScrollEngine.onTouchStart(e)
}

function onTabPageSwipeBlockTouchMove(e) {
  if(e.custom === true) return
  let xDistanceCompareWithStart = e.changedTouches[0].clientX - tabPageSwipeBlockTouchPositionOnTouchStart.x
  let yDistanceCompareWithStart = e.changedTouches[0].clientY - tabPageSwipeBlockTouchPositionOnTouchStart.y
  let xDistance = e.changedTouches[0].clientX - tabPageSwipeBlockTouchPositionOnTouchMoving.x
  let yDistance = e.changedTouches[0].clientY - tabPageSwipeBlockTouchPositionOnTouchMoving.y
  tabPageSwipeBlockTouchPositionOnTouchMoving.x = e.changedTouches[0].clientX
  tabPageSwipeBlockTouchPositionOnTouchMoving.y = e.changedTouches[0].clientY
  if(tabPageSwipeBlockSwipingDirection == null) {
    if(Math.abs(yDistanceCompareWithStart) > 5) {
      tabPageSwipeBlockSwipingDirection = 'vertical'
    } else if(Math.abs(xDistanceCompareWithStart) > 1) {
      tabPageSwipeBlockSwipingDirection = 'horizontal'
    }
    if(tabPageSwipeBlockSwipingDirection == null) {
      e.stopPropagation()
      return
    }
  }
  switch(tabPageSwipeBlockSwipingDirection) {
    case 'horizontal':
      onTabPageSwipeBlockHorizontalSwipe(xDistance)
      break
    case 'vertical':
      onTabPageSwipeBlockVerticalSwipe(yDistance)
      e.stopPropagation()
      dispatchCustomTouchMoveEvent(e)
      break
  }
  if(tabPageSwipeBlockSwipingDirection === 'vertical') {
    inertialScrollEngine.onTouchMove(e)
  }
}

function onTabPageSwipeBlockHorizontalSwipe(scrollDistance) {
  //暂时忽略
}

function onTabPageSwipeBlockVerticalSwipe(scrollDistance) {
  if(Math.abs(scrollDistance) < 0.3) return
  if(!componentParams.commentListPullRefreshDisabled && scrollDistance > 0) {
    componentParams.commentListPullRefreshPhysicalActionDoing = true
  }
  if(componentParams.commentListPullRefreshPhysicalActionDoing) return
  calcPlayerTopBarBackgroundOpacity()
  let heightChangeAmount = adjustPlayerAndTabPageHeight(scrollDistance)
  if(scrollDistance < 0) componentParams.commentListPullRefreshDisabled = true
  if(heightChangeAmount === 0) {
    let scrollBlockComponent = getActiveScrollBlockComponent()
    scrollBlockComponent.contentWrapperScrollBy(-scrollDistance)
    let shouldStopScroll = (
        scrollBlockComponent.isAtMinScrollTopValue() &&
        codeUtils.getDomHeight(customPlayerWrapperDom.value) >= domHeightValues.defaultPlayerWrapperHeight
    ) || scrollBlockComponent.isAtMaxScrollTopValue()
    if(shouldStopScroll) {
      inertialScrollEngine.stopScroll()
    }
  }
  calcIsKeepMaxTabPageHeight()
  calcIsTobBarPlayBtnShouldBeShown()
}

function onTabPageSwipeBlockTouchEnd() {
  calcIsCommentListPullRefreshDisabled()
  tabPageSwipeBlockSwipingDirection = null
  componentParams.commentListPullRefreshPhysicalActionDoing = false
  setTimeout(async () => {
    for(let i = 0; i < 3; i++) {
      calcPlayerTopBarBackgroundOpacity()
      await codeUtils.sleep(30)
    }
  })
  inertialScrollEngine.onTouchEnd()
}

function onVideoPlayingStatusChanged(playing) {
  componentParams.videoPlaying = playing
  inertialScrollEngine.stopScroll()
  adjustPlayerAndTabPageHeight()
  calcIsKeepMaxTabPageHeight()
  calcPlayerTopBarBackgroundOpacity()
  calcIsTobBarPlayBtnShouldBeShown()
}

function onVideoPlayingFinished() {
  setPlayerTopBarHide(false)
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

function adjustPlayerAndTabPageHeight(scrollDistance) {
  if(componentParams.videoPlaying) {
    customPlayerWrapperDom.value.style.height = `${domHeightValues.defaultPlayerWrapperHeight}px`
    videoDetailsTabPageContentDom.value.style.height = `${domHeightValues.defaultTabPageContentDomHeight}px`
    commentListTabPageContentDom.value.style.height = `${domHeightValues.defaultTabPageContentDomHeight}px`
    return 0
  }
  if(componentParams.keepMaxTabPageHeight) return 0
  let nowTabScrollTopValue = calcNowTabScrollTopValue()
  let nowTabMaxScrollTopValue = calcNowTabMaxScrollTopValue()
  if(scrollDistance > 0 && nowTabScrollTopValue > 0) return 0
  if(Math.max(nowTabMaxScrollTopValue - nowTabScrollTopValue) <= 1) return 0
  //计算播放器高度
  let originalPlayerWrapperHeight = codeUtils.getDomHeight(customPlayerWrapperDom.value)
  let playerWrapperHeight = originalPlayerWrapperHeight + scrollDistance
  if(playerWrapperHeight < domHeightValues.playerTopBarHeight) {
    playerWrapperHeight = domHeightValues.playerTopBarHeight
  }
  if(playerWrapperHeight > domHeightValues.defaultPlayerWrapperHeight) {
    playerWrapperHeight = domHeightValues.defaultPlayerWrapperHeight
  }
  //计算标签页高度
  let originalTabPageContentHeight = codeUtils.getDomHeight(videoDetailsTabPageContentDom.value)
  let tabPageContentHeight = originalTabPageContentHeight - scrollDistance
  if(tabPageContentHeight < domHeightValues.defaultTabPageContentDomHeight) {
    tabPageContentHeight = domHeightValues.defaultTabPageContentDomHeight
  }
  if(tabPageContentHeight > domHeightValues.maxTabPageContentDomHeight) {
    tabPageContentHeight = domHeightValues.maxTabPageContentDomHeight
  }
  //调整高度
  customPlayerWrapperDom.value.style.height = `${playerWrapperHeight}px`
  videoDetailsTabPageContentDom.value.style.height = `${tabPageContentHeight}px`
  commentListTabPageContentDom.value.style.height = `${tabPageContentHeight}px`
  return playerWrapperHeight - originalPlayerWrapperHeight
}

/**
 * 在垂直滚动时，向TabPageSwipeBlock发送一个自定义TouchMove事件，以触发其用于监听下拉刷新的监听器，
 * 同时又不触发其横向滚动
 *
 * 此处复制了原有的TouchMove事件对象，但修改了其clientX等属性的值，使之与TouchStart时的值相同，
 * 从而使的TabPageSwipeBlock中的DOM的监听器只能监听到纵向滚动，而不触发横向滚动的动作
 */
function dispatchCustomTouchMoveEvent(e) {
  if(componentParams.activeTabName !== 'commentList') return
  if(componentParams.commentListPullRefreshDisabled) return
  let eventNormalObject = codeUtils.convertObjectWhichFromClassToNormal(e)
  let touchNormalObject = codeUtils.convertObjectWhichFromClassToNormal(e.changedTouches[0])
  touchNormalObject.clientX = tabPageSwipeBlockTouchPositionOnTouchStart.x
  touchNormalObject.pageX = tabPageSwipeBlockTouchPositionOnTouchStart.x
  let clonedTouchObject = new Touch(touchNormalObject)
  eventNormalObject.changedTouches = [ clonedTouchObject ]
  eventNormalObject.targetTouches = [ clonedTouchObject ]
  eventNormalObject.touches = [ clonedTouchObject ]
  let newEvent = new TouchEvent('touchmove', eventNormalObject)
  newEvent.custom = true
  commentListPullRefreshTrackDom.dispatchEvent(newEvent)
  setTimeout(() => {
    commentListContainerComponent.value.appendBlankDomToCommentListPullRefreshTrackDom()
  }, 1)
}

function calcPlayerTopBarBackgroundOpacity() {
  topBarOpacity = 1.0 - (codeUtils.getDomHeight(customPlayerWrapperDom.value) - domHeightValues.playerTopBarHeight) /
      (domHeightValues.defaultPlayerWrapperHeight - domHeightValues.playerTopBarHeight)
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

function calcIsCommentListPullRefreshDisabled() {
  componentParams.commentListPullRefreshDisabled = componentParams.activeTabName !== 'commentList' ||
      componentParams.commentReplyListShowing || (
          commentListScrollBlockComponent.value.getScrollTopValue() !== 0 ||
            codeUtils.getDomHeight(customPlayerWrapperDom.value) !== domHeightValues.defaultPlayerWrapperHeight ||
            Date.now() - componentParams.lastTimeTabChangeTime < 300
      )
}

function calcIsKeepMaxTabPageHeight() {
  if(componentParams.videoPlaying) {
    componentParams.keepMaxTabPageHeight = false
    return
  }
  componentParams.keepMaxTabPageHeight = codeUtils.getDomHeight(customPlayerWrapperDom.value) <=
      domHeightValues.playerTopBarHeight && calcNowTabScrollTopValue() > 0
}

function calcNowTabScrollTopValue() {
  switch(componentParams.activeTabName) {
    case 'videoDetails':
      return videoDetailsScrollBlockComponent.value.getScrollTopValue()
    case 'commentList':
      return commentListScrollBlockComponent.value.getScrollTopValue()
  }
}

function calcNowTabMaxScrollTopValue() {
  switch(componentParams.activeTabName) {
    case 'videoDetails':
      return videoDetailsScrollBlockComponent.value.getMaxScrollTopValue()
    case 'commentList':
      return commentListScrollBlockComponent.value.getMaxScrollTopValue()
  }
}

function setPlayerTopBarHide(hide) {
  let alwaysShow = componentParams.playerControlBarShowing || topBarOpacity > 0.4
  if(alwaysShow) hide = false
  playerTopBarDom.value.style.display = hide ? 'none' : 'flex'
}

function calcIsTobBarPlayBtnShouldBeShown() {
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
  overflow: hidden;
}

.custom-player-wrapper {
  width: 100%;
  height: var(--player-wrapper-height);
  z-index: var(--player-wrapper-z-index);
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
  z-index: var(--player-wrapper-z-index);

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

.video-detail-and-comment-list {
  width: 100%;
  height: calc(100vh - var(--player-wrapper-height));
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
  height: calc(100vh - var(--player-wrapper-height) - var(--van-tabs-line-height) - 0.8px);

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
<template>
  <div class="custom-video-player" ref="customVideoPlayerDom">
    <div class="player-container" ref="playerContainerDom"></div>
    <div class="fullscreen-top-bar" ref="fullScreenTopBarDom">
      <div class="left">
        <back-icon :color="componentParams.backIconColor" @click="onBackIconClick" />
      </div>
    </div>
    <div class="player-click-frame" ref="playerClickFrameDom"></div>
    <audio class="audio-player" controls ref="audioPlayerDom" />
  </div>
</template>

<script setup>
import { onMounted, onUnmounted, reactive, ref } from 'vue'
import Player, { EVENT, Popover } from 'nplayer'
import Danmaku from '@nplayer/danmaku'
import videoApi from '@/api/video'
import codeUtils from '@/utils/code'
import BackIcon from '@/components/icon/BackIcon.vue'
import videoPlayingViewJsInterface from '@/androidJsInterfaces/videoPlayingViewJsInterface'

const props = defineProps({
  videoId: String,
  episodeId: String
})

const componentParams = reactive({
  backIconColor: 'rgb(255, 255, 255)',
  //视频播放状态标记量，仅在手动暂停视频时会被置为false，用于确保视频仅能被手动暂停
  videoPlaying: false,
  fullScreen: false,
  videoControlBarShowStatus: true
})

const customVideoPlayerDom = ref()

const playerContainerDom = ref()

const fullScreenTopBarDom = ref()

const playerClickFrameDom = ref()

const audioPlayerDom = ref()

//noinspection JSUnusedGlobalSymbols
const qualityController = {
  el: document.createElement('div'),
  btn: null,
  popover: null,
  itemElements: [],
  init() {
    this.btn = document.createElement('div')
    this.el.appendChild(this.btn)
    this.popover = new Popover(this.el)
    this.btn.addEventListener('click', () => this.popover.show())
    this.el.style.display = 'none'
    this.el.classList.add('quality-controller')
  }
}

const playerControllers = {
  controls: [
    [
      'play',
      'time',
      'spacer',
      qualityController,
      'danmaku-settings',
      'settings',
      'fullscreen'
    ],
    [ 'progress' ]
  ],
  bpControls: {
    500: [
      [ 'play', 'progress', 'time', 'danmaku-settings', 'fullscreen' ],
      [],
      [ 'spacer' ]
    ]
  }
}

const emits = defineEmits([ 'playingStatusChanged', 'beforeControlBarShowStatusChange' ])

let player

let videoDom

let nowVideoQualityName

let streamInfoList = {
  type: Object,
  default: [
    {
      videoStreamUrl: '',
      audioStreamUrl: '',
      qualityName: ''
    }
  ]
}

let danmakuExtendSettingDoms = []

onMounted(() => {
  initPlayer()
  configurePlayer()
})

onUnmounted(() => {
  pauseVideo()
})

function initPlayer() {
  videoDom = document.createElement('video')
  player = new Player({
    video: videoDom,
    ...playerControllers,
    contextMenus: [],
    volumeVertical: true,
    plugins: [
      new Danmaku({
        autoInsert: false,
        unlimited: true,
        area: 1,
        fontsizeScale: 0.7
      })
    ]
  })
  player.registerControlItem(qualityController, 'quality')
  player.mount(playerContainerDom.value)
}

function configurePlayer() {
  playerClickFrameDom.value.addEventListener('click', onPlayerClickFrameClick)
  playerClickFrameDom.value.addEventListener('touchstart', onPlayerClickFrameTouchStart)
  playerClickFrameDom.value.addEventListener('touchmove', onPlayerClickFrameTouchMove)
  playerClickFrameDom.value.addEventListener('touchend', onPlayerClickFrameTouchEnd)
  //为播放暂停按钮添加额外事件监听器
  let playBtnDom = playerContainerDom.value.querySelector('.nplayer_tooltip')
  playBtnDom.addEventListener('click', onPlayBtnClick)
  playerContainerDom.value.querySelector('.nplayer_poster').remove()
  //将定义的顶栏DOM移动至nplayer的根DOM中
  let innerPlayerDom = playerContainerDom.value.querySelector('.nplayer')
  innerPlayerDom.appendChild(fullScreenTopBarDom.value)
  //设置内部视频播放器事件监听
  videoDom.onseeking = onVideoSeeking
  videoDom.oncanplay = onVideoCanplay
  videoDom.onplay = onVideoPlay
  videoDom.onplaying = onVideoPlaying
  videoDom.onwaiting = onVideoWaiting
  videoDom.onpause = onVideoPause
  videoDom.onratechange = onVideoRateChange
  videoDom.ondblclick = onVideoDomDoubleClick
  //禁用单击播放暂停
  videoDom.removeEventListener('click', player.toggle)
  //禁用双击全屏
  videoDom.removeEventListener('dblclick', player.fullscreen.toggle)
  //禁用默认触摸事件（移动端）
  //noinspection JSUnresolvedReference
  videoDom.removeEventListener('touchstart', player.touch.onTouchStart)
  //注册nplayer内置事件
  player.el.removeEventListener('mouseleave', player.control.tryHide)
  player.on(EVENT.ENTER_FULLSCREEN, () => onFullScreenStatusChanged(true))
  player.on(EVENT.EXIT_FULLSCREEN, () => onFullScreenStatusChanged(false))
  player.on(EVENT.CONTROL_SHOW, () => onControlBarShowStatusChanged(true))
  player.on(EVENT.CONTROL_HIDE, () => onControlBarShowStatusChanged(false))
  setPlayerControlShowAndHideCallback()
  //未全屏时隐藏除开关以外的弹幕设置项
  calcIsDanmakuExtendSettingsShow()
}

function setPlayerControlShowAndHideCallback() {
  let playerControlDom = playerContainerDom.value.querySelector('.nplayer_control')
  let playerControlShowCallback = player.control.show
  let playerControlHideCallback = player.control.hide
  let playerControlHideCallbackTask
  player.control.show = () => {
    if(playerControlHideCallbackTask != null) return
    beforeControlBarShowStatusChange(true)
    playerControlDom.style.visibility = 'unset'
    playerControlShowCallback()
  }
  player.control.hide = () => {
    beforeControlBarShowStatusChange(false)
    playerControlDom.style.visibility = 'hidden'
    playerControlHideCallbackTask = setTimeout(() => {
      playerControlHideCallback()
      playerControlHideCallbackTask = null
    }, 300)
  }
}

function onPlayerClickFrameClick() {
  if(!componentParams.videoControlBarShowStatus) {
    player.control.showTransient()
  }
  playerClickFrameDom.value.style.display = 'none'
}

function onPlayerClickFrameTouchStart(e) {
  videoDom.dispatchEvent(new TouchEvent('touchstart', codeUtils.convertObjectWhichFromClassToNormal(e)))
}

function onPlayerClickFrameTouchMove(e) {
  videoDom.dispatchEvent(new TouchEvent('touchmove', codeUtils.convertObjectWhichFromClassToNormal(e)))
}

function onPlayerClickFrameTouchEnd(e) {
  videoDom.dispatchEvent(new TouchEvent('touchend', codeUtils.convertObjectWhichFromClassToNormal(e)))
}

function onPlayBtnClick() {
  //若视频未开始播放，则不执行任何操作
  //播放按钮默认的事件监听器会使视频开始播放，然后play事件监听器将会自动更新此标记量
  if(!componentParams.videoPlaying) return
  pauseVideo()
}

//在快进快退动作触发后，视频缓冲触发前，触发seeking事件
function onVideoSeeking() {
  //同步音频与视频的进度
  audioPlayerDom.value.currentTime = videoDom.currentTime
}

function onVideoCanplay() {
  resumeVideo()
}

//在视频即将开始播放时触发
function onVideoPlay() {
  componentParams.videoPlaying = true
  audioPlayerDom.value.currentTime = videoDom.currentTime
  emits('playingStatusChanged', true)
}

//在视频开始播放后触发一次
function onVideoPlaying() {
  audioPlayerDom.value.play()
}

//视频开始缓冲时触发（视频未被手动暂停，但因暂未缓冲完成而被动暂停，此时不会触发pause事件）
function onVideoWaiting() {
  audioPlayerDom.value.pause()
}

//视频被暂停后时触发（不论是手动还是自动）
function onVideoPause() {
  //检查标记量，若视频不是被手动暂停的（视频已暂停，但标记量为true），则立即恢复播放
  if(componentParams.videoPlaying && !videoDom.ended) {
    videoDom.play()
    return
  }
  //同步暂停音频播放
  audioPlayerDom.value.pause()
  componentParams.videoPlaying = false
  emits('playingStatusChanged', false)
}

//视频播放倍速值被更改时触发
function onVideoRateChange() {
  audioPlayerDom.value.playbackRate = videoDom.playbackRate
}

function onVideoDomDoubleClick() {
  if(!componentParams.videoPlaying) {
    videoDom.play()
    return
  }
  pauseVideo()
}

function onFullScreenStatusChanged(fullScreen) {
  componentParams.fullScreen = fullScreen
  player.danmaku.updateFontsize(fullScreen ? 0.85 : 0.7)
  calcIsFullScreenTopBarShow()
  calcIsDanmakuExtendSettingsShow()
  movePlayerClickFrame()
}

function beforeControlBarShowStatusChange(show) {
  emits('beforeControlBarShowStatusChange', show)
  if(!show) {
    hideActivePlayerControlPopover()
    playerContainerDom.value.querySelector('.nplayer_control_bg').classList.add('nplayer_control_bg-hide')
  }
}

function onControlBarShowStatusChanged(show) {
  componentParams.videoControlBarShowStatus = show
  calcIsFullScreenTopBarShow()
  if(!show) playerClickFrameDom.value.style.display = 'block'
}

function onBackIconClick() {
  document.exitFullscreen()
}

async function playVideo() {
  //获取视频流和音频流的URL
  streamInfoList = (await videoApi.streamInfoList({
    videoId: props.videoId,
    episodeId: props.episodeId
  })).data
  //创建容器，并根据清晰度列表计算出对应每一个清晰度的DOM元素
  const fragment = document.createDocumentFragment()
  qualityController.itemElements = streamInfoList.map(info => {
    const qualityItemDom = document.createElement('div')
    qualityItemDom.textContent = info.qualityName
    qualityItemDom.classList.add('quality-item')
    //为清晰度DOM添加事件监听
    qualityItemDom.addEventListener('click', () => {
      if(info.qualityName === nowVideoQualityName) return
      qualityController.btn.textContent = info.qualityName
      qualityController.itemElements.forEach(it => {
        it.classList.remove('quality-item-active')
      })
      qualityItemDom.classList.add('quality-item-active')
      videoDom.src = info.videoStreamUrl
      audioPlayerDom.value.src = info.audioStreamUrl
      nowVideoQualityName = info.qualityName
      loadDanmakuList()
    })
    //添加到容器
    fragment.appendChild(qualityItemDom)
    return qualityItemDom
  })
  //清空清晰度弹出框中原有内容，并放入新的内容
  qualityController.popover.panelEl.innerHTML = ''
  qualityController.popover.panelEl.appendChild(fragment)
  qualityController.el.style.display = 'block'
  //模拟对第一个清晰度项目进行点击
  //noinspection JSCheckFunctionSignatures
  qualityController.itemElements[0].dispatchEvent(new Event('click'))
}

function resumeVideo() {
  videoDom.play().catch(async () => {
    videoPlayingViewJsInterface.simulateClickBeforeVideoPlay()
    await codeUtils.sleep(20)
    videoDom.play()
  })
}

//先更新标记量，再执行暂停
function pauseVideo() {
  componentParams.videoPlaying = false
  videoDom.pause()
}

function loadDanmakuList() {
  videoApi.danmakuList({
    episodeId: props.episodeId
  }).then(res => {
    let list = [ ...res.data ]
    list.forEach(it => {
      //使字段名称兼容nplayer-danmaku
      it.text = it.content
      //noinspection JSUnresolvedReference
      it.color = it.colorRgb
    })
    player.danmaku.resetItems(list)
  })
}

function calcIsFullScreenTopBarShow() {
  if(!componentParams.fullScreen) {
    fullScreenTopBarDom.value.style.display = 'none'
    return
  }
  fullScreenTopBarDom.value.style.display = componentParams.videoControlBarShowStatus ? 'flex' : 'none'
}

function calcIsDanmakuExtendSettingsShow() {
  if(danmakuExtendSettingDoms.length <= 0) {
    let danmakuSettingsContainerDom = playerContainerDom.value.querySelector('.nplayer_danmaku_setting')
    danmakuSettingsContainerDom.querySelectorAll('.nplayer_danmaku_row').forEach((it, i) => {
      if(i === 0) {
        let element = it.querySelector('.nplayer_danmaku_reset')
        danmakuExtendSettingDoms.push({
          element,
          defaultCssDisplay: window.getComputedStyle(element).display
        })
        return
      }
      danmakuExtendSettingDoms.push({
        element: it,
        defaultCssDisplay: window.getComputedStyle(it).display
      })
    })
  }
  danmakuExtendSettingDoms.forEach(it => {
    it.element.style.display = componentParams.fullScreen ? it.defaultCssDisplay : 'none'
  })
}

function hideActivePlayerControlPopover() {
  Object.values(player.controlNamedMap).forEach(it => {
    if(it.popover != null) {
      it.popover.hide()
    } else if(it.hide != null) {
      it.hide()
    }
  })
}

function movePlayerClickFrame() {
  if(componentParams.fullScreen) {
    playerContainerDom.value.querySelector('.nplayer').appendChild(playerClickFrameDom.value)
    return
  }
  customVideoPlayerDom.value.appendChild(playerClickFrameDom.value)
}

defineExpose({
  playVideo,
  pauseVideo,
  resumeVideo
})
</script>

<style scoped lang="scss">
.custom-video-player {
  width: 100%;
  height: 100%;
  position: relative;

  ::v-deep(.player-click-frame) {
    position: absolute;
    top: 0;
    width: 100%;
    height: 100%;
    z-index: 100;
    display: none;
  }
}

.player-container {
  width: 100%;
  height: 100%;
  background-color: black;

  ::v-deep(.nplayer_control:hover~.nplayer_control_bg-hide) {
    opacity: 0 !important;
  }
}

.fullscreen-top-bar {
  position: fixed;
  top: 0;
  width: 100%;
  height: 50px;
  display: none;
  justify-content: space-between;
  align-items: center;
  background-color: rgba(255, 255, 255, 0);
  background-image: linear-gradient(rgba(0, 0, 0, 0.5) 0%, rgba(0, 0, 0, 0) 100%);

  .left {
    display: flex;
    align-items: center;
    margin-left: 15px;

    ::v-deep(.icon-container) {
      width: 30px;
      height: 30px;
    }
  }
}

.audio-player {
  display: none;
}

::v-deep(.nplayer_control-hide) {
  display: none;
}
</style>
<template>
  <div class="custom-video-player" ref="customVideoPlayerDom" @click="onRootDomClick">
    <div class="player-container" ref="playerContainerDom"></div>
    <div class="fullscreen-top-bar" ref="fullScreenTopBarDom">
      <div class="left">
        <back-icon :color="componentParams.backIconColor" @click="onBackIconClick" />
      </div>
    </div>
    <!--
      有时，播放器控制栏控件（按钮、开关、控制条等）在随着控制栏的消失而一并消失后，再在这些控件消失前的位置
      进行点击，会出现即使控件已经消失也仍然会触发控件被点击的行为。
      clickFrame的用途是在控制栏消失后，阻止点击事件传递到控制栏上。它会在控制栏消失后出现在整个播放器区域的
      上层，覆盖住播放器区域，当它被点击时，将会使控制栏显示出来，然后将自身隐藏，控制栏消失后，它又会再次出现。
    -->
    <div class="player-click-frame" ref="playerClickFrameDom"></div>
  </div>
</template>

<script setup>
import { onMounted, onUnmounted, reactive, ref } from 'vue'
import Player, { EVENT, Popover } from 'nplayer'
import Danmaku from '@nplayer/danmaku'
import codeUtils from '@/utils/code'
import BackIcon from '@/components/icon/BackIcon.vue'
import videoPlayingViewJsInterface from '@/androidJsInterfaces/videoPlayingViewJsInterface'
import videoJsInterface from '@/androidJsInterfaces/videoJsInterface'
import { MediaPlayer } from 'dashjs'

const props = defineProps({
  lavsourceId: String,
  videoId: String,
  episodeId: String
})

const componentParams = reactive({
  backIconColor: 'rgb(255, 255, 255)',
  //视频播放状态标记量，仅在手动暂停视频时会被置为false，用于确保视频仅能被手动暂停
  videoPlaying: false,
  //视频是否至少成功开始播放过一次
  videoFirstPlayed: false,
  fullScreen: false,
  videoControlBarShowing: true
})

const customVideoPlayerDom = ref()

const playerContainerDom = ref()

const fullScreenTopBarDom = ref()

const playerClickFrameDom = ref()

//noinspection JSUnusedGlobalSymbols
const qualityController = {
  id: 'quality-controller',
  el: document.createElement('div'),
  btn: null,
  popover: null,
  itemElements: [],
  nameItemElementMap: {},
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

const emits = defineEmits([
  'playingStatusChanged',
  'beforeControlBarShowStatusChange',
  'playingFinished'
])

let player

let dashPlayer

let videoDom

let nowVideoQualityName

let streamInfoList = {
  type: Object,
  default: [
    {
      type: '',
      videoStreamUrl: '',
      audioStreamUrl: '',
      qualityName: '',
      dashManifest: ''
    }
  ]
}

let danmakuExtendSettingDoms = []

let rootDomClickTask

let clickFrameHideTask

let videoStartPlayMonitorTask

let videoStreamUrlRefreshTask

let activeControlPopoverNameSet = {}

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
  //移除封面
  playerContainerDom.value.querySelector('.nplayer_poster').remove()
  //将定义的顶栏DOM移动至nplayer的根DOM中
  let innerPlayerDom = playerContainerDom.value.querySelector('.nplayer')
  innerPlayerDom.appendChild(fullScreenTopBarDom.value)
  innerPlayerDom.removeEventListener('mousemove', player.control.showTransient)
  //阻止控制栏上的点击事件传递到rootDom
  let playerControlDom = innerPlayerDom.querySelector('.nplayer_control')
  playerControlDom.addEventListener('click', e => e.stopPropagation())
  //设置内部视频播放器事件监听
  videoDom.onseeking = onVideoSeeking
  videoDom.oncanplay = onVideoCanplay
  videoDom.onplay = onVideoPlay
  videoDom.onplaying = onVideoPlaying
  videoDom.onwaiting = onVideoWaiting
  videoDom.onpause = onVideoPause
  videoDom.onratechange = onVideoRateChange
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
  setPlayerFunctions()
  //未全屏时隐藏除开关以外的弹幕设置项
  calcIsDanmakuExtendSettingsShow()
}

//改写player对象中所定义的一些函数的原有行为
function setPlayerFunctions() {
  player.seek = onPlayerSeek
  setPlayerControlShowAndHideCallback()
  setOnPlayerControlItemClick()
}

function setPlayerControlShowAndHideCallback() {
  let playerControlDom = playerContainerDom.value.querySelector('.nplayer_control')
  let playerControlShowCallback = player.control.show
  let playerControlHideCallback = player.control.hide
  player.control.show = () => {
    beforeControlBarShowStatusChange(true)
    playerControlDom.style.visibility = 'unset'
    //noinspection JSUnresolvedReference
    clearTimeout(player.control.showTimer)
    playerControlShowCallback()
  }
  player.control.hide = () => {
    beforeControlBarShowStatusChange(false)
    playerControlHideCallback()
  }
}

function setOnPlayerControlItemClick() {
  let itemDefinitions = [ qualityController, 'danmaku-settings', 'settings' ]
  let items = itemDefinitions.map(it => {
    if(!(typeof it === 'string')) return it
    return player.getControlItem(it)
  })
  items.forEach(it => {
    let playerControl = player.control
    it.el.addEventListener('click', () => {
      activeControlPopoverNameSet[it.id] = 1
      clearTimeout(playerControl.showTimer)
      //noinspection JSUnresolvedReference
      playerControl.showTimer = null
    })
    let popoverOnHide = it.popover.onHide
    if(popoverOnHide == null) popoverOnHide = () => {}
    //noinspection JSUnresolvedReference
    it.popover.onHide = () => {
      delete activeControlPopoverNameSet[it.id]
      popoverOnHide()
      if(Object.keys(activeControlPopoverNameSet).length <= 0 && playerControl.showTimer == null) {
        newPlayerControlHideTask()
      }
    }
  })
}

function newPlayerControlHideTask() {
  if(player.control.showTimer != null) clearTimeout(player.control.showTimer)
  //noinspection JSUnresolvedReference
  player.control.showTimer = setTimeout(player.control.hide, player.control.delayHidTime)
}

function onPlayerClickFrameClick(e) {
  e.stopPropagation()
  onRootDomClick()
  if(clickFrameHideTask != null) clearTimeout(clickFrameHideTask)
  clickFrameHideTask = setTimeout(() => {
    playerClickFrameDom.value.style.display = 'none'
  }, 250)
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

//通过nplayer的组件调节视频进度时触发
function onPlayerSeek(time) {
  let duration = player.duration
  if(time >= duration) time = duration - 0.1
  if(time < 0) time = 0
  videoDom.currentTime = time
}

//在快进快退动作触发后，视频缓冲触发前，触发seeking事件
function onVideoSeeking() {
  //ignore
}

function onVideoCanplay() {
  resumeVideo()
}

//在视频从其他状态变更为播放状态时触发（除非手动暂停再播放或者播放结束后重新播放，该事件都只触发一次）
function onVideoPlay() {
  newVideoStartPlayMonitorTask()
  componentParams.videoPlaying = true
  if(!componentParams.videoFirstPlayed) {
    player.control.hide()
    componentParams.videoFirstPlayed = true
  }
  emits('playingStatusChanged', true)
}

//在视频开始播放后触发（调整进度条也会触发）
function onVideoPlaying() {
  if(videoStartPlayMonitorTask != null) {
    clearTimeout(videoStartPlayMonitorTask)
    videoStartPlayMonitorTask = null
  }
  newPlayerControlHideTask()
}

//视频开始缓冲时触发（视频未被手动暂停，但因暂未缓冲完成而被动暂停，此时不会触发pause事件）
function onVideoWaiting() {
  newVideoStartPlayMonitorTask()
}

//视频被暂停后时触发（不论是手动还是自动）
function onVideoPause() {
  //检查标记量，若视频不是被手动暂停的（视频已暂停，但标记量为true），则立即恢复播放
  if(componentParams.videoPlaying && !videoDom.ended) {
    videoDom.play()
    return
  }
  componentParams.videoPlaying = false
  emits('playingStatusChanged', false)
  if(videoDom.ended) {
    player.control.show()
    emits('playingFinished')
  }
}

//视频播放倍速值被更改时触发
function onVideoRateChange() {
  //ignore
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
  componentParams.videoControlBarShowing = show
  calcIsFullScreenTopBarShow()
  if(!show) playerClickFrameDom.value.style.display = 'block'
}

function onBackIconClick() {
  document.exitFullscreen()
}

function onRootDomClick() {
  //防止双击动作触发单击事件
  if(rootDomClickTask != null) {
    clearTimeout(rootDomClickTask)
    rootDomClickTask = null
    onRootDomDoubleClick()
    return
  }
  rootDomClickTask = setTimeout(() => {
    hideOrShowPlayerControl()
    //在触发单击事件后，适当延长可触发双击事件的时间
    setTimeout(() => rootDomClickTask = null, 200)
  }, 250)
}

function onRootDomDoubleClick() {
  if(!componentParams.videoPlaying) {
    videoDom.play()
    return
  }
  pauseVideo()
}

async function initQualityController() {
  //获取视频流和音频流的URL
  streamInfoList = await videoJsInterface.streamInfoList({
    lavsourceId: props.lavsourceId,
    videoId: props.videoId,
    episodeId: props.episodeId
  })
  //创建容器，并根据清晰度列表计算出对应每一个清晰度的DOM元素
  const fragment = document.createDocumentFragment()
  qualityController.nameItemElementMap = {}
  qualityController.itemElements = streamInfoList.map(info => {
    const qualityItemDom = document.createElement('div')
    qualityItemDom.textContent = info.qualityName
    qualityItemDom.classList.add('quality-item')
    //为清晰度DOM添加事件监听
    qualityItemDom.addEventListener('click', () => {
      qualityController.itemElements.forEach(it => {
        it.classList.remove('quality-item-active')
      })
      qualityItemDom.classList.add('quality-item-active')
      qualityController.btn.textContent = info.qualityName
      if(info.qualityName === nowVideoQualityName) return
      if(info.type === 'dash') {
        let url = URL.createObjectURL(new Blob([ info.dashManifest ], { type: 'application/dash+xml' }))
        dashPlayer = MediaPlayer().create()
        dashPlayer.initialize(videoDom, url, false)
      } else {
        videoDom.src = info.videoStreamUrl
      }
      nowVideoQualityName = info.qualityName
      loadDanmakuList()
    })
    //添加到容器
    fragment.appendChild(qualityItemDom)
    qualityController.nameItemElementMap[info.qualityName] = qualityItemDom
    return qualityItemDom
  })
  //清空清晰度弹出框中原有内容，并放入新的内容
  qualityController.popover.panelEl.innerHTML = ''
  qualityController.popover.panelEl.appendChild(fragment)
  qualityController.el.style.display = 'block'
  if(nowVideoQualityName != null) applyNowVideoQuality()
}

async function initAndPlayVideo() {
  nowVideoQualityName = null
  await initQualityController()
  applyNowVideoQuality()
  initVideoStreamUrlRefreshTask()
}

function initVideoStreamUrlRefreshTask() {
  if(videoStreamUrlRefreshTask != null) return
  videoStreamUrlRefreshTask = setInterval(() => {
    initQualityController()
  }, 60 * 1000)
}

function resumeVideo() {
  videoDom.play().catch(async() => {
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

function applyNowVideoQuality() {
  //模拟对清晰度项目进行点击
  //noinspection JSCheckFunctionSignatures
  let qualityControllerItem
  if(nowVideoQualityName == null) {
    qualityControllerItem = qualityController.itemElements[0]
  } else {
    qualityControllerItem = qualityController.nameItemElementMap[nowVideoQualityName]
  }
  qualityControllerItem.dispatchEvent(new Event('click'))
}

function loadDanmakuList() {
  videoJsInterface.danmakuList({
    lavsourceId: props.lavsourceId,
    episodeId: props.episodeId
  }).then(res => {
    res.forEach(it => {
      //使字段名称兼容nplayer-danmaku
      it.text = it.content
      //noinspection JSUnresolvedReference
      it.color = it.colorRgb
    })
    player.danmaku.resetItems(res)
  })
}

function calcIsFullScreenTopBarShow() {
  if(!componentParams.fullScreen) {
    fullScreenTopBarDom.value.style.display = 'none'
    return
  }
  fullScreenTopBarDom.value.style.display = componentParams.videoControlBarShowing ? 'flex' : 'none'
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

function hideOrShowPlayerControl() {
  if(componentParams.videoControlBarShowing) {
    player.control.hide()
  } else {
    if(componentParams.videoPlaying) {
      player.control.showTransient()
    } else {
      player.control.show()
    }
  }
}

function newVideoStartPlayMonitorTask() {
  if(videoStartPlayMonitorTask != null) return
  videoStartPlayMonitorTask = setTimeout(() => {
    initAndPlayVideo()
  }, 8 * 1000)
}

defineExpose({
  getOriginalPlayer: () => player,
  initAndPlayVideo,
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

::v-deep(.nplayer_control-hide) {
  display: none;
}
</style>